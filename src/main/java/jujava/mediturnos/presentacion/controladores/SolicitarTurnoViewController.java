package jujava.mediturnos.presentacion.controladores;

import jujava.mediturnos.logica.entidades.Medico;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Collectors;
import java.util.List;

public class SolicitarTurnoViewController {

    @FXML private ComboBox<String> cmbEspecialidad;
    @FXML private ComboBox<String> cmbMedico;
    @FXML private DatePicker dtpFecha;
    @FXML private ComboBox<String> cmbHora;

    private MainController dataController;
    private MainViewController navigationController;
    private int dniPacienteLogueado;
    private List<Medico> medicosCargados;

    @FXML
    public void initialize() {
        ObservableList<String> horas = FXCollections.observableArrayList();
        for (int h = 8; h <= 17; h++) {
            horas.add(String.format("%02d:00", h));
            if (h < 17) horas.add(String.format("%02d:30", h));
        }
        cmbHora.setItems(horas);

        cmbEspecialidad.valueProperty().addListener((obs, oldVal, newVal) -> {
            cargarMedicosPorEspecialidad(newVal);
        });
    }

    public void initData(MainController dataController, MainViewController navigationController, int dniPaciente) {
        this.dataController = dataController;
        this.navigationController = navigationController;
        this.dniPacienteLogueado = dniPaciente;
        cmbEspecialidad.getItems().addAll(dataController.getEspecialidades());
    }

    private void cargarMedicosPorEspecialidad(String especialidad) {
        cmbMedico.getItems().clear();
        if (especialidad != null && !especialidad.trim().isEmpty()) {
            medicosCargados = dataController.getMedicosPorEspecialidad(especialidad);
            List<String> nombresMedicos = medicosCargados.stream()
                    .map(m -> m.getNombre() + " " + m.getApellido() + " (DNI: " + m.getDni() + ")")
                    .collect(Collectors.toList());
            cmbMedico.getItems().addAll(nombresMedicos);
        }
    }


    @FXML
    private void handleConfirmarTurno() {
        String especialidad = cmbEspecialidad.getValue();
        String medicoStr = cmbMedico.getValue();
        LocalDate fecha = dtpFecha.getValue();
        String horaStr = cmbHora.getValue();

        if (especialidad == null || medicoStr == null || fecha == null || horaStr == null) {
            dataController.showAlert(Alert.AlertType.ERROR, "Error", "Debe completar todos los campos.");
            return;
        }

        int dniMedico = -1;
        try {
            String dniPart = medicoStr.substring(medicoStr.lastIndexOf(":") + 1, medicoStr.lastIndexOf(")")).trim();
            dniMedico = Integer.parseInt(dniPart);
        } catch (Exception e) {
            dataController.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo obtener el DNI del médico seleccionado.");
            return;
        }

        LocalTime hora = LocalTime.parse(horaStr);
        LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);

        boolean exito = dataController.solicitarTurno(dniPacienteLogueado, dniMedico, especialidad, fechaHora);

        if (exito) {
            dataController.showAlert(Alert.AlertType.INFORMATION, "Éxito", "Turno solicitado correctamente.");
            navigationController.handleSolicitarTurno();
        } else {
            dataController.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo solicitar el turno. Verifique la fecha y disponibilidad.");
        }
    }
}
