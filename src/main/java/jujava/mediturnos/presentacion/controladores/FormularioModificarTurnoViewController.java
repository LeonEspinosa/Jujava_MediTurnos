package jujava.mediturnos.presentacion.controladores;

import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.presentacion.modelos.TurnoModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FormularioModificarTurnoViewController {

    // Componentes FXML
    @FXML private Label lblIdTurno;
    @FXML private Label lblPaciente;
    @FXML private Label lblEspecialidad;
    @FXML private ComboBox<String> cmbMedico;
    @FXML private DatePicker dtpFecha;
    @FXML private ComboBox<String> cmbHora;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Button btnGuardar;

    // Dependencias
    private MainController dataController;
    private MainViewController navigationController;
    private TurnoModel turnoActual;
    private List<Medico> medicosCargados; // Lista de médicos de la especialidad
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    @FXML
    public void initialize() {
        // Inicializar ComboBox de Horas (reutilizando lógica de Solicitud)
        ObservableList<String> horas = FXCollections.observableArrayList();
        for (int h = 8; h <= 17; h++) {
            horas.add(String.format("%02d:00", h));
            if (h < 17) horas.add(String.format("%02d:30", h));
        }
        cmbHora.setItems(horas);

        // Inicializar ComboBox de Estados
        cmbEstado.getItems().addAll(Arrays.asList("Pendiente", "Confirmado", "Cancelado", "Realizado"));
        dtpFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate hoy = LocalDate.now();

                // Deshabilita la celda si la fecha es anterior a hoy
                // (Permitimos "hoy" por si se modifica un turno para más tarde en el día)
                setDisable(empty || date.isBefore(hoy));

                if (date.isBefore(hoy)) {
                    setStyle("-fx-background-color: #ffc0c0;");
                }
            }
        });

        // Listener para actualizar la lista de médicos (Aunque no se permite cambiar Especialidad,
        // este listener sirve para reaccionar si por alguna razón la Especialidad cambiara o se actualizara)
        // cmbMedico.valueProperty().addListener((obs, oldVal, newVal) -> cargarMedicosPorEspecialidad(lblEspecialidad.getText()));
    }

    /**
     * Inyección de datos al inicializar la vista.
     */
    public void initData(MainController dataController, MainViewController navigationController, TurnoModel turno) {
        this.dataController = dataController;
        this.navigationController = navigationController;
        this.turnoActual = turno;

        if (turno != null) {
            cargarDatosParaModificacion(turno);
        } else {
            // Manejo de error si no se pasó un turno
            dataController.showAlert(Alert.AlertType.ERROR, "Error", "No se encontró el turno a modificar.");
            navigationController.handleGestionarTurnos();
        }
    }

    private void cargarDatosParaModificacion(TurnoModel turno) {
        // 1. Cargar datos de solo lectura
        lblIdTurno.setText(String.valueOf(turno.getIdTurno()));
        lblPaciente.setText(turno.nombrePacienteProperty() + " (DNI: " + turno.getDniPaciente() + ")");
        lblEspecialidad.setText(turno.getEspecialidad());

        // 2. Determinar Fecha y Hora actual (debe parsear el String a LocalDateTime)
        LocalDateTime ldt = LocalDateTime.parse(turno.getFechaHora(), FORMATTER);
        dtpFecha.setValue(ldt.toLocalDate());
        cmbHora.setValue(ldt.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));

        // 3. Cargar estado
        cmbEstado.setValue(turno.getEstado());

        // 4. Cargar ComboBox de Médicos para la especialidad actual
        cargarMedicosPorEspecialidad(turno.getEspecialidad());

        // 5. Seleccionar el médico actual
        String medicoActualStr = turno.nombreMedicoProperty() + " (DNI: " + turno.getDniMedico() + ")";
        cmbMedico.setValue(medicoActualStr);

        // 6. Deshabilitar la edición de estados si el turno ya fue 'Realizado'
        if ("Realizado".equals(turno.getEstado())) {
            cmbEstado.setDisable(true);
            btnGuardar.setDisable(true);
            dataController.showAlert(Alert.AlertType.INFORMATION, "Información", "El turno ya fue realizado y no puede ser modificado.");
        }
    }

    private void cargarMedicosPorEspecialidad(String especialidad) {
        cmbMedico.getItems().clear();
        if (dataController != null && especialidad != null && !especialidad.trim().isEmpty()) {
            medicosCargados = dataController.getMedicosPorEspecialidad(especialidad);
            List<String> nombresMedicos = medicosCargados.stream()
                    .map(m -> m.getNombre() + " " + m.getApellido() + " (DNI: " + m.getDni() + ")")
                    .collect(Collectors.toList());
            cmbMedico.getItems().addAll(nombresMedicos);
        }
    }

    @FXML
    private void handleGuardar() {
        // Validaciones
        String medicoStr = cmbMedico.getValue();
        LocalDate fecha = dtpFecha.getValue();
        String horaStr = cmbHora.getValue();
        String estado = cmbEstado.getValue();

        if (medicoStr == null || fecha == null || horaStr == null || estado == null) {
            dataController.showAlert(Alert.AlertType.ERROR, "Error", "Debe completar todos los campos.");
            return;
        }

        int dniMedico = -1;
        try {
            // Extraer DNI del médico seleccionado
            String dniPart = medicoStr.substring(medicoStr.lastIndexOf(":") + 1, medicoStr.lastIndexOf(")")).trim();
            dniMedico = Integer.parseInt(dniPart);
        } catch (Exception e) {
            dataController.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo obtener el DNI del médico seleccionado.");
            return;
        }

        // Construir el nuevo LocalDateTime
        LocalTime hora = LocalTime.parse(horaStr);
        LocalDateTime nuevaFechaHora = LocalDateTime.of(fecha,hora);

        // Llamada a la lógica de modificación
        boolean exito = dataController.modificarTurnoAdmin(
                turnoActual.getIdTurno(),
                dniMedico,
                lblEspecialidad.getText(), // La especialidad no se modifica
                nuevaFechaHora,
                estado
        );

        if (exito) {
            dataController.showAlert(Alert.AlertType.INFORMATION, "Éxito", "Turno modificado correctamente.");
            navigationController.handleGestionarTurnos();
        } else {
            dataController.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo modificar el turno. Verifique la fecha/disponibilidad o DNI.");
        }
    }

    @FXML
    private void handleCancelar() {
        // Vuelve a la vista de gestión de turnos
        navigationController.handleGestionarTurnos();
    }
}