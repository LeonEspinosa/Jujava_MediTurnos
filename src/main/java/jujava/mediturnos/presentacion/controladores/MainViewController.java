package jujava.mediturnos.presentacion.controladores;

import jujava.mediturnos.presentacion.modelos.Usuario;

import jujava.mediturnos.presentacion.vista.AppMain;
import jujava.mediturnos.logica.entidades.Persona;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainViewController {

    @FXML
    private BorderPane contentArea;
    @FXML
    private Label lblUsuarioLogueado;

    private Stage primaryStage;
    private Persona usuarioLogueado;


    private MainController dataController;

    @FXML
    private void initialize() {

        this.dataController = new MainController();
        // Carga la vista de listado por defecto
        //handleListar();
    }

    // --- Métodos de Navegación ---
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void iniciarAplicacionPrincipal(Persona usuario) {
        this.usuarioLogueado = usuario;

        // 1. Mostrar la ventana principal (que estaba oculta)
        if (this.primaryStage != null) {
            this.primaryStage.show();
            // Opcional: Centrarla después de mostrarla
            this.primaryStage.centerOnScreen();
        } else {
            System.err.println("Error: PrimaryStage no fue inyectado en MainViewController.");
        }

        // 2. Actualizar la UI con la info del usuario
        if (lblUsuarioLogueado != null) {
            lblUsuarioLogueado.setText("Usuario: " + usuario.getNombre() + " " + usuario.getApellido());
        } else {
            System.err.println("Advertencia: lblUsuarioLogueado es null. ¿Olvidaste agregarlo a main-view.fxml?");
        }

        // 3. (OPCIONAL) Configurar permisos según el rol
        // ej: if (usuario instanceof Paciente) { btnRegistro.setDisable(true); }

        // 4. Ahora sí, cargar la vista por defecto (Listado)
        handleListar();
    }

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

