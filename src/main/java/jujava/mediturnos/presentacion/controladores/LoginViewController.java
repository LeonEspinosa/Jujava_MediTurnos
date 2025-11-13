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

public class LoginViewController {

    @FXML
    private TextField txtDni;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

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
     * Ahora permite el login de todos los roles (Admin, Médico, Paciente)
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
                System.out.println("Login exitoso (Administrador): " + usuarioAutenticado.getNombre());
            } else if (usuarioAutenticado instanceof Medico) {
                System.out.println("Login exitoso (Médico): " + usuarioAutenticado.getNombre());
            } else if (usuarioAutenticado instanceof Paciente) {
                System.out.println("Login exitoso (Paciente): " + usuarioAutenticado.getNombre());
            } else {
                System.out.println("Login exitoso (Desconocido): " + usuarioAutenticado.getNombre());
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
     */
    private void mostrarError(String mensaje) {
        if (lblError != null) {
            lblError.setText(mensaje != null ? mensaje : "");
        } else {
            System.err.println("Error en LoginViewController: lblError es null. Mensaje no mostrado: " + mensaje);
        }
    }
}