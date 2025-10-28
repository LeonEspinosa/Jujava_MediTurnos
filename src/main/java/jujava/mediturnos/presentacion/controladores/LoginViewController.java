package jujava.mediturnos.presentacion.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import jujava.mediturnos.logica.GestorUsuario;
import jujava.mediturnos.logica.entidades.Administrador;
import jujava.mediturnos.logica.entidades.Persona;


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
     * Modificado para mostrar mensaje específico si el usuario no es Admin.
     */
    @FXML
    private void handleLogin() {
        String dniStr = txtDni.getText();
        String password = txtPassword.getText();

        // 1. Validación de entradas básicas
        if (dniStr == null || dniStr.trim().isEmpty() || password == null || password.trim().isEmpty()) { // Chequeo null añadido
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

        // 2. Autenticación (Llamada a la Capa de Lógica)
        Persona usuarioAutenticado = gestorUsuario.autenticarUsuario(dni, password);

        // 3. Verificación de resultado
        if (usuarioAutenticado != null) {
            // --- ÉXITO ---

            // 3.1 VERIFICAR SI ES ADMINISTRADOR (Si solo ellos pueden entrar)
            if (usuarioAutenticado instanceof Administrador) {
                System.out.println("Autenticación exitosa para Administrador: " + usuarioAutenticado.getNombre());
                mostrarError(""); // Limpiar error
                mainViewController.iniciarAplicacionPrincipal(usuarioAutenticado);
                loginStage.close();
            } else {
                // Es un usuario válido (Paciente/Medico) pero no tiene permiso para entrar
                System.out.println("Intento de login por usuario no administrador: " + usuarioAutenticado.getNombre());
                mostrarError("Acceso denegado. Solo los administradores pueden iniciar sesión.");
            }

        } else {

            Persona usuarioExiste = gestorUsuario.buscarUsuarioPorDNI(dni); // Busca solo por DNI
            if (usuarioExiste != null && !(usuarioExiste instanceof Administrador)) {

                mostrarError("Acceso denegado. Solo los administradores pueden iniciar sesión.");
            } else if (usuarioExiste == null){

                mostrarError("DNI no encontrado en el sistema.");
            } else {

                mostrarError("DNI o contraseña incorrectos.");
            }
        }
    }

    /**
     * Método helper para mostrar mensajes de error en la UI.
     */
    private void mostrarError(String mensaje) {

        if (lblError != null) {
            lblError.setText(mensaje != null ? mensaje : "");
        } else {
            System.err.println("Error en LoginViewController: lblError es null. Mensaje no mostrado: " + mensaje);
        }
    }
}

