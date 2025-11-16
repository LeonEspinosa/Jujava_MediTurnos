package jujava.mediturnos.presentacion.controladores;

import jujava.mediturnos.presentacion.modelos.TurnoModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Optional;

public class MiAgendaViewController {

    @FXML private TableView<TurnoModel> tblTurnos;
    @FXML private TableColumn<TurnoModel, Number> colIdTurno;
    @FXML private TableColumn<TurnoModel, String> colFechaHora;
    @FXML private TableColumn<TurnoModel, String> colPaciente;
    @FXML private TableColumn<TurnoModel, String> colDniPaciente;
    @FXML private TableColumn<TurnoModel, String> colEspecialidad;
    @FXML private TableColumn<TurnoModel, String> colEstado;
    @FXML private Button btnCompletarTurno; // Nuevo Botón


    private MainController dataController;
    private MainViewController navigationController;
    private int dniMedicoLogueado;


    @FXML
    public void initialize() {
        // Configuración de columnas (Se mantiene igual)
        colIdTurno.setCellValueFactory(cellData -> cellData.getValue().idTurnoProperty());
        colFechaHora.setCellValueFactory(cellData -> cellData.getValue().fechaHoraProperty());
        colPaciente.setCellValueFactory(cellData -> cellData.getValue().nombrePacienteProperty());
        colDniPaciente.setCellValueFactory(cellData -> cellData.getValue().dniPacienteProperty());
        colEspecialidad.setCellValueFactory(cellData -> cellData.getValue().especialidadProperty());
        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());

        // Listener para habilitar/deshabilitar el botón de completar
        btnCompletarTurno.setDisable(true);
        tblTurnos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            // Se habilita si hay selección y el estado NO es Cancelado o Realizado
            boolean puedeCompletar = newVal != null
                    && !"Cancelado".equals(newVal.getEstado())
                    && !"Realizado".equals(newVal.getEstado());
            btnCompletarTurno.setDisable(!puedeCompletar);
        });
    }

    public void initData(MainController dataController, MainViewController navigationController, int dniMedico) {
        this.dataController = dataController;
        this.navigationController = navigationController;
        this.dniMedicoLogueado = dniMedico;
        cargarTurnos();
    }

    private void cargarTurnos() {
        if (dataController != null) {
            // Carga los turnos filtrados por el DNI del médico logueado
            tblTurnos.setItems(dataController.getTurnosParaMedico(dniMedicoLogueado));
        }
    }

    @FXML
    private void handleCompletarTurno() {
        TurnoModel turnoSeleccionado = tblTurnos.getSelectionModel().getSelectedItem();

        if (turnoSeleccionado == null) return; // Doble chequeo

        if ("Realizado".equals(turnoSeleccionado.getEstado())) {
            dataController.showAlert(Alert.AlertType.INFORMATION, "Información", "El turno ya fue marcado como 'Realizado'.");
            return;
        }

        Optional<ButtonType> result = dataController.showConfirmation("¿Confirma que desea marcar como 'Realizado' el turno ID " + turnoSeleccionado.getIdTurno() + "?");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Asumo que MainController tiene un metodo "completarTurno' que llama a la lógica
            boolean exito = dataController.completarTurno(turnoSeleccionado.getIdTurno());

            if (exito) {
                dataController.showAlert(Alert.AlertType.INFORMATION, "Éxito", "Turno marcado como 'Realizado' correctamente.");
                cargarTurnos(); // Recarga la tabla para reflejar el cambio de estado
            } else {
                dataController.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo marcar el turno como 'Realizado'.");
            }
        }
    }
}