package jujava.mediturnos.presentacion.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la vista gestionar-especialidades-view.fxml.
 * Permite a los administradores agregar y eliminar especialidades.
 */
public class GestionarEspecialidadesViewController {

    @FXML
    private TextField txtNuevaEspecialidad;
    @FXML
    private Button btnAgregar;
    @FXML
    private ListView<String> lstEspecialidades;
    @FXML
    private Button btnEliminar;

    private MainController dataController;
    private MainViewController navigationController;

    @FXML
    public void initialize() {
        // Deshabilitar el botón de eliminar por defecto
        btnEliminar.setDisable(true);

        // Habilitar el botón de eliminar solo si hay un ítem seleccionado
        lstEspecialidades.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    btnEliminar.setDisable(newSelection == null);
                });
    }

    /**
     * Inyecta los controladores principales para permitir la comunicación
     * con la lógica de negocio y la navegación.
     */
    public void initData(MainController dataController, MainViewController navigationController) {
        this.dataController = dataController;
        this.navigationController = navigationController;
        cargarEspecialidades();
    }

    /**
     * Carga (o recarga) la lista de especialidades desde el MainController
     * y la muestra en el ListView.
     */
    private void cargarEspecialidades() {
        if (dataController != null) {
            try {
                List<String> especialidades = dataController.getEspecialidades();
                ObservableList<String> items = FXCollections.observableArrayList(especialidades);
                lstEspecialidades.setItems(items);
            } catch (Exception e) {
                dataController.showAlert(Alert.AlertType.ERROR, "Error", "No se pudieron cargar las especialidades.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Manejador del botón "Agregar".
     * Toma el texto del TextField y llama al MainController para agregarlo.
     */
    @FXML
    private void handleAgregar() {
        String nuevaEspecialidad = txtNuevaEspecialidad.getText();

        if (nuevaEspecialidad == null || nuevaEspecialidad.trim().isEmpty()) {
            dataController.showAlert(Alert.AlertType.WARNING, "Campo vacío", "Debe ingresar un nombre para la especialidad.");
            return;
        }

        // Llamamos al controlador principal (que llama a la lógica)
        boolean exito = dataController.agregarEspecialidad(nuevaEspecialidad);

        if (exito) {
            dataController.showAlert(Alert.AlertType.INFORMATION, "Éxito", "Especialidad '" + nuevaEspecialidad + "' agregada correctamente.");
            cargarEspecialidades(); // Recargar la lista
            txtNuevaEspecialidad.clear(); // Limpiar el campo
        } else {
            // GestorTurnos imprime el error específico (ej. "ya existe")
            dataController.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo agregar la especialidad. Es probable que ya exista.");
        }
    }

    /**
     * Manejador del botón "Eliminar".
     * Toma la selección del ListView y llama al MainController para eliminarla.
     */
    @FXML
    private void handleEliminar() {
        String seleccionada = lstEspecialidades.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            dataController.showAlert(Alert.AlertType.WARNING, "Sin selección", "Debe seleccionar una especialidad de la lista para eliminar.");
            return;
        }

        // Pedir confirmación
        Optional<ButtonType> result = dataController.showConfirmation(
                "¿Está seguro de que desea eliminar la especialidad '" + seleccionada + "'?\n\n" +
                        "NOTA: No podrá eliminarla si un médico la tiene asignada."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Llamamos al controlador principal (que llama a la lógica)
            boolean exito = dataController.eliminarEspecialidad(seleccionada);

            if (exito) {
                dataController.showAlert(Alert.AlertType.INFORMATION, "Éxito", "Especialidad '" + seleccionada + "' eliminada correctamente.");
                cargarEspecialidades(); // Recargar la lista
            } else {
                // GestorTurnos imprime el error específico (ej. "en uso")
                dataController.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo eliminar la especialidad. Verifique que no esté en uso por algún médico.");
            }
        }
    }
}