package jujava.mediturnos.presentacion.controladores;

import jujava.mediturnos.presentacion.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Arrays;

/**
 * Controlador para formulario-view.fxml.
 * Maneja la lógica de Alta y Modificación.
 */
public class FormularioViewController {

    @FXML
    private Label lblTitulo;
    @FXML
    private ComboBox<String> cmbRol;
    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private VBox vbDatosEspecificos;
    @FXML
    private Label lblInfoExtra;
    @FXML
    private TextField txtInfoExtra;

    private MainController dataController; // Para lógica de datos
    private MainViewController navigationController; // Para navegar
    private Usuario usuarioActual;
    private boolean esModificacion;

    /**
     * Llamado automáticamente después de cargar el FXML.
     */
    @FXML
    public void initialize() {
        // Poblar el ComboBox de Roles
        cmbRol.getItems().addAll(Arrays.asList("Paciente", "Médico", "Administrativo"));

        // Listener para la lógica de campos dinámicos
        cmbRol.valueProperty().addListener((obs, oldVal, newVal) -> actualizarCamposDinamicos(newVal));
    }

    /**
     * Método de inicialización manual para pasar datos.
     */
    public void initData(MainController dataController, MainViewController navigationController, Usuario usuario) {
        this.dataController = dataController;
        this.navigationController = navigationController;
        this.usuarioActual = usuario;

        if (usuario == null) {
            // Modo ALTA
            esModificacion = false;
            lblTitulo.setText("Formulario de Alta (Registro)");
        } else {
            // Modo MODIFICACIÓN
            esModificacion = true;
            lblTitulo.setText("Formulario de Modificación");
            cargarDatosParaModificacion();
        }
    }

    /**
     * Muestra u oculta campos según el Rol.
     */
    private void actualizarCamposDinamicos(String rol) {
        if (rol == null) {
            vbDatosEspecificos.setVisible(false);
            return;
        }

        vbDatosEspecificos.setVisible(true);
        txtInfoExtra.setText("");

        if ("Médico".equals(rol)) {
            lblInfoExtra.setText("Matrícula del Médico:");
            txtInfoExtra.setPromptText("Ej. 1234");
        } else if ("Administrativo".equals(rol)) {
            lblInfoExtra.setText("Sector del Administrativo:");
            txtInfoExtra.setPromptText("Ej. Turnos");
        } else {
            // Paciente
            vbDatosEspecificos.setVisible(false);
        }
    }

    /**
     * Rellena el formulario si estamos en modo Modificación.
     */
    private void cargarDatosParaModificacion() {
        if (usuarioActual == null) return;

        txtDni.setText(usuarioActual.getDni());
        txtDni.setEditable(false);
        txtDni.setStyle("-fx-background-color: #eeeeee;");

        txtNombre.setText(usuarioActual.getNombre());
        txtApellido.setText(usuarioActual.getApellido());

        // Esto dispara el listener y llama a actualizarCamposDinamicos()
        cmbRol.setValue(usuarioActual.getRol());
        txtInfoExtra.setText(usuarioActual.getInfoExtra());
    }

    @FXML
    private void handleGuardar() {
        String dni = txtDni.getText();
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String rol = cmbRol.getValue();
        String infoExtra = vbDatosEspecificos.isVisible() ? txtInfoExtra.getText() : "";

        Usuario usuarioParaGuardar;

        if (esModificacion) {
            // Actualizamos el objeto existente
            usuarioActual.setNombre(nombre);
            usuarioActual.setApellido(apellido);
            usuarioActual.setRol(rol);
            usuarioActual.setInfoExtra(infoExtra);
            usuarioParaGuardar = usuarioActual;
        } else {
            // Creamos un nuevo DTO
            usuarioParaGuardar = new Usuario(dni, nombre, apellido, rol, infoExtra);
        }

        // Enviamos al controlador de lógica para validar y guardar
        boolean exito = dataController.guardarUsuario(usuarioParaGuardar, esModificacion);

        // Si el guardado fue exitoso, navegamos de vuelta al listado
        if (exito) {
            navigationController.handleListar();
        }
    }

    @FXML
    private void handleCancelar() {
        // Pedimos al controlador de navegación que vuelva al listado
        navigationController.handleListar();
    }
}
