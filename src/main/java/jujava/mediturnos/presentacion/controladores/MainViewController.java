package jujava.mediturnos.presentacion.controladores;

import com.example.myjavafx.model.Usuario;
import com.example.myjavafx.view.AppMain;
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
        // ERROR 1 ARREGLADO: El constructor de MainController ahora está vacío.
        this.dataController = new MainController();

        handleListar();
    }

    // --- Métodos de Navegación ---
    // ERRORES 6 y 7 ARREGLADOS: Se cambiaron de 'private' a 'public'

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
        // ERRORES 2 y 3 ARREGLADOS:
        // Los métodos getUsuarioSeleccionado() y showAlert() ahora existen en dataController
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
            // Usamos una ruta absoluta desde la raíz de 'resources'
            loader.setLocation(AppMain.class.getResource("/com/example/myjavafx/view/" + fxmlFile));

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
            // ERROR 4 ARREGLADO: showAlert() ahora existe
            dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista: " + fxmlFile);
        }
    }
}

