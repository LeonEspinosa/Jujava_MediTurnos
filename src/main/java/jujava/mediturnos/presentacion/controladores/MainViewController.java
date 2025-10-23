package jujava.mediturnos.presentacion.controladores;

import jujava.mediturnos.presentacion.modelos.Usuario;
import jujava.mediturnos.presentacion.vista.AppMain;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainViewController {

    @FXML
    private BorderPane contentArea;

    private MainController dataController; // Controlador de lógica/datos

    @FXML
    private void initialize() {
        // Inicializamos el controlador de datos
        this.dataController = new MainController();

        // Carga inicial de la vista de listado
        handleListar();
    }

    // --- Métodos de Navegación ---
    @FXML
    public void handleListar() {
        loadView("/jujava/mediturnos/listado-view.fxml", null);
    }

    @FXML
    public void handleRegistro() {
        loadView("/jujava/mediturnos/formulario-view.fxml", null);
    }

    @FXML
    public void handleModificacion() {
        Usuario seleccionado = dataController.getUsuarioSeleccionado();
        if (seleccionado == null) {
            dataController.showAlert(javafx.scene.control.Alert.AlertType.WARNING,
                    "Advertencia", "Debe seleccionar un usuario en la vista de Listado para modificar.");
            return;
        }
        loadView("/jujava/mediturnos/formulario-view.fxml", seleccionado);
    }

    @FXML
    private void handleSalir() {
        Platform.exit();
    }

    /**
     * Carga dinámicamente otra vista en el área central.
     * @param fxmlPath ruta absoluta desde resources
     * @param usuario usuario opcional para pasar datos
     */
    private void loadView(String fxmlPath, Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(AppMain.class.getResource(fxmlPath));

            Node view = loader.load();

            // Inyección de controladores según la vista
            if (fxmlPath.endsWith("listado-view.fxml")) {
                ListadoViewController controller = loader.getController();
                controller.init(dataController);
            } else if (fxmlPath.endsWith("formulario-view.fxml")) {
                FormularioViewController controller = loader.getController();
                controller.initData(dataController, this, usuario);
            }

            contentArea.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
            dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR,
                    "Error", "No se pudo cargar la vista: " + fxmlPath);
        }
    }
}
