package jujava.mediturnos.presentacion.controladores;

// ERROR 31: Importación corregida al DTO de presentación
import jujava.mediturnos.presentacion.modelos.Usuario;
// ERROR 32: Importación corregida a la clase AppMain correcta
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

    private MainController dataController; // El controlador de lógica/datos

    @FXML
    private void initialize() {
        // AHORA SÍ: Esto crea el MainController, que a su vez cargará GestorUsuario.
        this.dataController = new MainController();
        // Carga la vista de listado por defecto
        handleListar();
    }

    // --- Métodos de Navegación ---

    @FXML
    public void handleListar() {
        loadView("listado-view.fxml", null);
    }

    @FXML
    public void handleRegistro() {
        loadView("formulario-view.fxml", null);
    }

    @FXML
    public void handleModificacion() {
        Usuario seleccionado = dataController.getUsuarioSeleccionado();
        if (seleccionado == null) {
            dataController.showAlert(javafx.scene.control.Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un usuario en la vista de Listado para modificar.");
            return;
        }
        loadView("formulario-view.fxml", seleccionado);
    }

    @FXML
    private void handleSalir() {
        Platform.exit();
    }

    private void loadView(String fxmlFile, Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader();
            // ERROR 33: Ruta de recursos corregida
            loader.setLocation(AppMain.class.getResource("/jujava/mediturnos/" + fxmlFile));

            Node view = loader.load();

            // Pasa el control (Inyección de Dependencia)
            if (fxmlFile.equals("listado-view.fxml")) {
                ListadoViewController controller = loader.getController();
                controller.init(dataController);
            } else if (fxmlFile.equals("formulario-view.fxml")) {
                FormularioViewController controller = loader.getController();
                controller.initData(dataController, this, usuario);
            }

            contentArea.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
            dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista: " + fxmlFile);
        }
    }
}

