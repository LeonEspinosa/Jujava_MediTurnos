package jujava.mediturnos.presentacion.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import jujava.mediturnos.logica.GestorUsuario;
import jujava.mediturnos.logica.entidades.Persona;


public class LoginViewController {

    @FXML
    private TextField txtDni;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    // Esta es la conexión crucial a tu capa de Lógica.
    private GestorUsuario gestorUsuario;

    // Referencia al Stage (ventana) para poder cerrarla.
    private Stage loginStage;

    // Referencia al controlador de la ventana principal para "despertarlo".
    private MainViewController mainViewController;


    /**
     * Método de inicialización. Se llama automáticamente después de cargar el FXML.
     */
    @FXML
    private void initialize() {
        // Creamos la instancia del gestor de lógica.
        // Esto cargará los CSV (pacientes, medicos, admin) en memoria.
        this.gestorUsuario = new GestorUsuario();
        lblError.setText(""); // Limpiamos cualquier error residual.
    }

    /**
     * Método de configuración (Inyección de Dependencia Manual).
     * Lo usaremos para pasarle la referencia de la ventana principal.
     */
    public void initData(Stage loginStage, MainViewController mainViewController) {
        this.loginStage = loginStage;
        this.mainViewController = mainViewController;
    }


    /**
     * Manejador del evento de clic en el botón "Ingresar".
     * Aquí ocurre la magia de la autenticación.
     */
    @FXML
    private void handleLogin() {
        String dniStr = txtDni.getText();
        String password = txtPassword.getText();

        // 1. Validación de entradas básicas (Capa de Presentación)
        if (dniStr.trim().isEmpty() || password.trim().isEmpty()) {
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
        // Esta es la línea que depende de tu código actualizado.
        Persona usuarioAutenticado = gestorUsuario.autenticarUsuario(dni, password);

        // 3. Verificación de resultado
        if (usuarioAutenticado != null) {
            // ¡ÉXITO!
            System.out.println("Autenticación exitosa para: " + usuarioAutenticado.getNombre());
            mostrarError(""); // Limpiar error

            mainViewController.iniciarAplicacionPrincipal(usuarioAutenticado);
            loginStage.close();

        } else {
            mostrarError("DNI o contraseña incorrectos.");
        }
    }

    /**
     * Método helper para mostrar mensajes de error en la UI.
     */
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }
}
