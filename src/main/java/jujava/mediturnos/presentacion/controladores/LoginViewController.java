package jujava.mediturnos.presentacion.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import jujava.mediturnos.logica.GestorUsuario;
import jujava.mediturnos.logica.entidades.Persona;
import jujava.mediturnos.logica.entidades.Administrador;
import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.logica.entidades.Paciente;

// --- PASO 3.1: AÑADIR IMPORTS ---
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import jujava.mediturnos.presentacion.vista.AppMain;
import java.io.IOException;


public class LoginViewController {

    @FXML
    private TextField txtDni;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    // --- PASO 3.2: AÑADIR FXML DEL BOTÓN ---
    @FXML
    private Button btnRegistro;

    private GestorUsuario gestorUsuario;
    private Stage loginStage;
    private MainViewController mainViewController;

    @FXML
    private void initialize() {
        this.gestorUsuario = new GestorUsuario();
        lblError.setText("");
    }

    public void initData(Stage loginStage, MainViewController mainViewController) {
        this.loginStage = loginStage;
        this.mainViewController = mainViewController;
    }

    /**
     * Manejador del evento de clic en el botón "Ingresar".
     * (Este método no cambia)
     */
    @FXML
    private void handleLogin() {
        String dniStr = txtDni.getText();
        String password = txtPassword.getText();

        // --- Validaciones básicas ---
        if (dniStr == null || dniStr.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            mostrarError("DNI y Contraseña son obligatorios.");
            return;
        }

        int dni;
        try {
            dni = Integer.parseInt(dniStr.trim());
        } catch (NumberFormatException e) {
            mostrarError("El DNI debe ser un número válido.");
            return;
        }

        // --- Autenticación ---
        Persona usuarioAutenticado = gestorUsuario.autenticarUsuario(dni, password);

        if (usuarioAutenticado != null) {
            mostrarError(""); // limpiar mensaje de error

            // --- Mostrar en consola el tipo de usuario ---
            if (usuarioAutenticado instanceof Administrador) {
                // System.out.println("Login exitoso (Administrador): " + usuarioAutenticado.getNombre());
            } else if (usuarioAutenticado instanceof Medico) {
                // System.out.println("Login exitoso (Médico): " + usuarioAutenticado.getNombre());
            } else if (usuarioAutenticado instanceof Paciente) {
                // System.out.println("Login exitoso (Paciente): " + usuarioAutenticado.getNombre());
            } else {
                // System.out.println("Login exitoso (Desconocido): " + usuarioAutenticado.getNombre());
            }

            // --- Pasar el usuario autenticado a la vista principal ---
            mainViewController.iniciarAplicacionPrincipal(usuarioAutenticado);

            // Cerrar ventana de login
            loginStage.close();

        } else {
            Persona usuarioExiste = gestorUsuario.buscarUsuarioPorDNI(dni);

            if (usuarioExiste == null) {
                mostrarError("DNI no encontrado en el sistema.");
            } else {
                mostrarError("Contraseña incorrecta.");
            }
        }
    }

    /**
     * Muestra mensajes de error en la etiqueta lblError.
     * (Este método no cambia)
     */
    private void mostrarError(String mensaje) {
        if (lblError != null) {
            lblError.setText(mensaje != null ? mensaje : "");
        } else {
            // System.err.println("Error en LoginViewController: lblError es null. Mensaje no mostrado: " + mensaje);
        }
    }


    // --- PASO 3.3: AÑADIR NUEVO MÉTODO HANDLER ---

    /**
     * Manejador del botón "Registrarme".
     * Abre una NUEVA VENTANA (Modal) con el formulario en modo "Paciente".
     */
    @FXML
    private void handleRegistro() {
        try {
            // 1. Crear un cargador para el FXML del formulario
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppMain.class.getResource("/jujava/mediturnos/formulario-view.fxml"));
            VBox registroLayout = (VBox) loader.load();

            // 2. Crear una nueva ventana (Stage) para el formulario
            Stage registroStage = new Stage();
            registroStage.setTitle("Registro de Nuevo Paciente");
            registroStage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana de Login
            registroStage.initOwner(loginStage); // Define la ventana de Login como "padre"
            registroStage.setResizable(false);

            // 3. Crear la escena y aplicar el CSS
            Scene registroScene = new Scene(registroLayout);
            String css = AppMain.class.getResource("/jujava/mediturnos/styles.css").toExternalForm();
            if (css != null) {
                registroScene.getStylesheets().add(css);
            }

            // 4. Obtener el controlador del formulario
            FormularioViewController formController = loader.getController();

            // 5. Crear un NUEVO dataController (hub de datos) solo para esta operación
            // Es necesario porque el registro necesita acceso a la lógica de guardado.
            MainController dataControllerParaRegistro = new MainController();

            // 6. Inyectar dependencias usando el NUEVO método initData
            formController.initData(dataControllerParaRegistro, registroStage);

            // 7. Mostrar la ventana y esperar a que se cierre
            registroStage.setScene(registroScene);
            registroStage.showAndWait();

            this.gestorUsuario = new GestorUsuario();

            // Cuando el usuario cierra la ventana (ya sea guardando o cancelando),
            // la ejecución continúa aquí. El usuario vuelve a la ventana de login.

        } catch (IOException e) {
            // e.printStackTrace();
            mostrarError("Error: No se pudo abrir el formulario de registro.");
        } catch (IllegalStateException e) {
            // e.printStackTrace();
            mostrarError("Error al cargar la vista de registro.");
        }
    }
}