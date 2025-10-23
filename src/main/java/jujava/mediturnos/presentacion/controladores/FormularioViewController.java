package jujava.mediturnos.presentacion.controladores;

// ERROR 35: Importación corregida al DTO de presentación
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
    // Campos nuevos del FXML
    @FXML
    private TextField txtGenero;
    @FXML
    private TextField txtTelefono;
    @FXML
    private VBox vbDatosEspecificos;
    @FXML
    private Label lblInfoExtra;
    @FXML
    private TextField txtInfoExtra;

    private MainController dataController; // Para lógica de datos
    private MainViewController navigationController; // Para navegar
    private Usuario usuarioActual; // DTO de presentación
    private boolean esModificacion;

    /**
     * Llamado automáticamente después de cargar el FXML.
     */
    @FXML
    public void initialize() {
        // Poblar el ComboBox de Roles
        cmbRol.getItems().addAll(Arrays.asList("Paciente", "Médico", "Administrador"));

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

        txtInfoExtra.setText("");

        if ("Médico".equals(rol)) {
            vbDatosEspecificos.setVisible(true);
            lblInfoExtra.setText("Matrícula del Médico:");
            txtInfoExtra.setPromptText("Ej. 1234");
        } else if ("Administrador".equals(rol)) {
            vbDatosEspecificos.setVisible(true);
            lblInfoExtra.setText("Área del Administrador:");
            txtInfoExtra.setPromptText("Ej. Turnos");
        } else if ("Paciente".equals(rol)) {
            vbDatosEspecificos.setVisible(true);
            lblInfoExtra.setText("Obra Social:");
            txtInfoExtra.setPromptText("Ej. OSDE");
        } else {
            // Rol no reconocido o nulo
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
        txtDni.setStyle("-fx-background-color: #eeeeee;"); // Estilo visual para DNI bloqueado

        txtNombre.setText(usuarioActual.getNombre());
        txtApellido.setText(usuarioActual.getApellido());

        // Cargar Género y Teléfono
        // Esta llamada al "puente" (MainController) busca en la lógica (GestorUsuario)
        if (dataController != null) {
            jujava.mediturnos.logica.entidades.Persona p = dataController.getPersonaLogica(Integer.parseInt(usuarioActual.getDni()));
            if (p != null) {
                txtGenero.setText(String.valueOf(p.getGenero()));
                txtTelefono.setText(String.valueOf(p.getTelefono()));
            }
        }

        // Esto dispara el listener y llama a actualizarCamposDinamicos()
        cmbRol.setValue(usuarioActual.getRol());
        txtInfoExtra.setText(usuarioActual.getInfoExtra());
    }

    @FXML
    private void handleGuardar() {
        // 1. Recolectar datos de la UI
        String dni = txtDni.getText();
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String rol = cmbRol.getValue();
        String infoExtra = vbDatosEspecificos.isVisible() ? txtInfoExtra.getText() : "";
        String genero = txtGenero.getText();
        String telefono = txtTelefono.getText();

        Usuario usuarioParaGuardar;

        if (esModificacion) {
            // Si es modificación, usamos el DTO existente (usuarioActual)
            // MainController se encargará de actualizar sus propiedades
            usuarioParaGuardar = usuarioActual;
            // Actualizamos el DTO localmente ANTES de enviarlo
            usuarioParaGuardar.setNombre(nombre);
            usuarioParaGuardar.setApellido(apellido);
            usuarioParaGuardar.setRol(rol);
            usuarioParaGuardar.setInfoExtra(infoExtra);
        } else {
            // Creamos un nuevo DTO
            usuarioParaGuardar = new Usuario(dni, nombre, apellido, rol, infoExtra);
        }

        // 2. Enviamos al "puente" (MainController)
        // Pasamos el DTO y los datos extra (genero, telefono)
        boolean exito = dataController.guardarUsuario(usuarioParaGuardar, genero, telefono, esModificacion);

        // 3. Si el guardado fue exitoso, navegamos de vuelta al listado
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

