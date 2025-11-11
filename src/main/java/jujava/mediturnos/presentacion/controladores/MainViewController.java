package jujava.mediturnos.presentacion.controladores;

import jujava.mediturnos.presentacion.modelos.Usuario;

import jujava.mediturnos.presentacion.vista.AppMain;
import jujava.mediturnos.logica.entidades.Persona;
import jujava.mediturnos.logica.entidades.Administrador;
import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.logica.entidades.Paciente;

import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainViewController {

    @FXML
    private BorderPane contentArea;
    @FXML
    private Label lblUsuarioLogueado;
    @FXML private VBox menuBox;
    @FXML private VBox menuUsuarioAdmin;
    @FXML private VBox menuUsuarioMedico;
    @FXML private VBox menuUsuarioPaciente;

    private Stage primaryStage;
    private Persona usuarioLogueado;


    private MainController dataController;

    @FXML
    private void initialize() {

        this.dataController = new MainController();
        //handleListar();
    }

    // --- Métodos de Navegación ---
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void iniciarAplicacionPrincipal(Persona usuario) {
        this.usuarioLogueado = usuario;

        // 1. Mostrar la ventana principal
        if (this.primaryStage != null) {
            this.primaryStage.show();

            this.primaryStage.centerOnScreen();
        }
        // 2. Ocultar todos los menús de Turnos al inicio
        menuUsuarioAdmin.setVisible(false);
        menuUsuarioAdmin.setManaged(false);
        menuUsuarioMedico.setVisible(false);
        menuUsuarioMedico.setManaged(false);
        menuUsuarioPaciente.setVisible(false);
        menuUsuarioPaciente.setManaged(false);

        // 3. Controlar la visibilidad del menú de Gestión de Usuarios (solo Admin)
        boolean esAdmin = usuario instanceof Administrador;
        for (Node node : menuBox.getChildren()) {
            // Utilizamos el ID de los botones de Gestión de Usuarios
            if (node.getStyleClass().contains("menu-header") && node.getStyleClass().contains("menu-header")) {
                if (((Label)node).getText().contains("Gestión de Usuarios")) {
                    node.setVisible(esAdmin);
                    node.setManaged(esAdmin);
                }
            } else if (node.getId() != null && (node.getId().equals("btnListar") || node.getId().equals("btnRegistro") || node.getId().equals("btnModificar"))) {
                node.setVisible(esAdmin);
                node.setManaged(esAdmin);
            }
        }
        // 4. Configurar menús específicos por rol y cargar vista inicial
        if (usuario instanceof Administrador) {
            menuUsuarioAdmin.setVisible(true);
            menuUsuarioAdmin.setManaged(true);
            handleGestionarTurnos();

        } else if (usuario instanceof Medico) {
            menuUsuarioMedico.setVisible(true);
            menuUsuarioMedico.setManaged(true);
            handleMiAgenda();

        } else if (usuario instanceof Paciente) {
            menuUsuarioPaciente.setVisible(true);
            menuUsuarioPaciente.setManaged(true);
            handleSolicitarTurno();
        }

        // 5. Actualizar la UI
        if (lblUsuarioLogueado != null) {
            String rol = (usuario instanceof Administrador) ? "Admin" : (usuario instanceof Medico) ? "Médico" : (usuario instanceof Paciente) ? "Paciente" : "Usuario";
            lblUsuarioLogueado.setText("Usuario (" + rol + "): " + usuario.getNombre() + " " + usuario.getApellido());
        }


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

    @FXML
    public void handleSolicitarTurno() {
        if (usuarioLogueado instanceof Paciente) {
            loadView("solicitar-turno-view.fxml", null);
        }
    }
    @FXML
    public void handleMiAgenda() {
        if (usuarioLogueado instanceof Medico) {
            loadView("mi-agenda-view.fxml", null);
        }
    }
    @FXML
    public void handleGestionarTurnos() {
        if (usuarioLogueado instanceof Administrador) {
            loadView("gestionar-turnos-view.fxml", null);
        }
    }

    public void loadView(String fxmlFile, Usuario usuario) {
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
            }else if (fxmlFile.equals("solicitar-turno-view.fxml") && usuarioLogueado instanceof Paciente) {
                SolicitarTurnoViewController controller = loader.getController();
                controller.initData(dataController, this, usuarioLogueado.getDni());
            } else if (fxmlFile.equals("mi-agenda-view.fxml") && usuarioLogueado instanceof Medico) {
                MiAgendaViewController controller = loader.getController();
                controller.initData(dataController, this, usuarioLogueado.getDni());
            } else if (fxmlFile.equals("gestionar-turnos-view.fxml") && usuarioLogueado instanceof Administrador) {
                GestionarTurnosViewController controller = loader.getController();
                controller.initData(dataController, this);
            }

            contentArea.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
            dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista: " + fxmlFile);
        }
    }
}

