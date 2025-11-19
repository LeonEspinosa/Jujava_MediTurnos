package jujava.mediturnos.presentacion.controladores;

import jujava.mediturnos.presentacion.modelos.TurnoModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.Optional;
import javafx.scene.control.ButtonType;

public class GestionarTurnosViewController {

    @FXML private TableView<TurnoModel> tblTurnos;

    @FXML private TableColumn<TurnoModel, Number> colIdTurno;
    @FXML private TableColumn<TurnoModel, String> colFechaHora;
    @FXML private TableColumn<TurnoModel, String> colEspecialidad;
    @FXML private TableColumn<TurnoModel, String> colPaciente;
    @FXML private TableColumn<TurnoModel, String> colDniPaciente;
    @FXML private TableColumn<TurnoModel, String> colMedico;
    @FXML private TableColumn<TurnoModel, String> colDniMedico;
    @FXML private TableColumn<TurnoModel, String> colEstado;

    @FXML private Button btnCancelarTurno;
    @FXML private Button btnModificarTurno;

    private MainController dataController;
    private MainViewController navigationController;

    @FXML
    public void initialize() {
        colIdTurno.setCellValueFactory(cellData -> cellData.getValue().idTurnoProperty());
        colFechaHora.setCellValueFactory(cellData -> cellData.getValue().fechaHoraProperty());
        colEspecialidad.setCellValueFactory(cellData -> cellData.getValue().especialidadProperty());
        colPaciente.setCellValueFactory(cellData -> cellData.getValue().nombrePacienteProperty());
        colDniPaciente.setCellValueFactory(cellData -> cellData.getValue().dniPacienteProperty());
        colMedico.setCellValueFactory(cellData -> cellData.getValue().nombreMedicoProperty());
        colDniMedico.setCellValueFactory(cellData -> cellData.getValue().dniMedicoProperty());
        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
    }

    public void initData(MainController dataController, MainViewController navigationController) {
        this.dataController = dataController;
        this.navigationController = navigationController;
        cargarTurnos();

        btnCancelarTurno.setDisable(true);
        btnModificarTurno.setDisable(true);

        tblTurnos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isSelected = newVal != null;
            boolean isCancellable = isSelected
                    && !"Cancelado".equals(newVal.getEstado())
                    && !"Realizado".equals(newVal.getEstado());

            boolean isModifiable = isSelected
                    && !"Realizado".equals(newVal.getEstado());

            btnCancelarTurno.setDisable(!isCancellable);
            btnModificarTurno.setDisable(!isModifiable);
        });
    }

    private void cargarTurnos() {
        if (dataController != null) {
            tblTurnos.setItems(dataController.getTodosLosTurnos());
        }
    }

    @FXML
    private void handleCancelarTurno() {
        TurnoModel turnoSeleccionado = tblTurnos.getSelectionModel().getSelectedItem();
        if (turnoSeleccionado == null) return;

        if ("Cancelado".equals(turnoSeleccionado.getEstado())) {
            dataController.showAlert(Alert.AlertType.INFORMATION, "Información", "El turno ya está cancelado.");
            return;
        }

        Optional<ButtonType> result = dataController.showConfirmation(
                "¿Está seguro de que desea cancelar el turno ID "
                        + turnoSeleccionado.getIdTurno()
                        + " de " + turnoSeleccionado.nombrePacienteProperty().get() + "?"); // <--- Agregado .get()

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean exito = dataController.cancelarTurno(turnoSeleccionado.getIdTurno());

            if (exito) {
                dataController.showAlert(Alert.AlertType.INFORMATION, "Éxito", "Turno cancelado correctamente.");
                cargarTurnos();
            } else {
                dataController.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cancelar el turno.");
            }
        }
    }

    @FXML
    private void handleModificarTurno() {
        TurnoModel turnoSeleccionado = tblTurnos.getSelectionModel().getSelectedItem();

        if (turnoSeleccionado == null) {
            dataController.showAlert(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un turno para modificar.");
            return;
        }

        if ("Realizado".equals(turnoSeleccionado.getEstado())) {
            dataController.showAlert(Alert.AlertType.WARNING, "Advertencia", "No se puede modificar un turno ya realizado.");
            return;
        }

        //navigationController.loadView("formulario-modificar-turno-view.fxml", null);
        navigationController.loadViewModificarTurno(turnoSeleccionado);
    }
}
