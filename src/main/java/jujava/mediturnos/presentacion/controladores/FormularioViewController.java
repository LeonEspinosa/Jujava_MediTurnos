package jujava.mediturnos.presentacion.controladores;

import jujava.mediturnos.presentacion.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField; // Importar PasswordField
import javafx.scene.control.TextField;
// Se elimina la importación de VBox y RowConstraints si ya no se usan directamente para visibilidad
// import javafx.scene.layout.VBox;
// import javafx.scene.layout.RowConstraints;

import java.util.Arrays;

public class FormularioViewController {

    @FXML private Label lblTitulo;
    @FXML private ComboBox<String> cmbRol;
    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtGenero;
    @FXML private TextField txtTelefono;

    // --- Campos de Contraseña ---
    @FXML private Label lblPassword; // Ya lo tenías
    @FXML private PasswordField txtPassword; // Ya lo tenías
    @FXML private Label lblConfirmarPassword; // Nuevo Label
    @FXML private PasswordField txtConfirmarPassword; // Nuevo PasswordField
    // --- Fin Campos de Contraseña ---

    // --- Campos Específicos (ahora individuales) ---
    @FXML private Label lblInfoExtra;
    @FXML private TextField txtInfoExtra;
    // --- Fin Campos Específicos ---

    private MainController dataController;
    private MainViewController navigationController;
    private Usuario usuarioActual;
    private boolean esModificacion;

    @FXML
    public void initialize() {
        cmbRol.getItems().addAll(Arrays.asList("Paciente", "Médico", "Administrador"));
        cmbRol.valueProperty().addListener((obs, oldVal, newVal) -> actualizarCamposDinamicos(newVal));

        // Asegurar estado inicial correcto para campos específicos
        actualizarCamposDinamicos(null); // Oculta los campos específicos al inicio
    }

    public void initData(MainController dataController, MainViewController navigationController, Usuario usuario) {
        this.dataController = dataController;
        this.navigationController = navigationController;
        this.usuarioActual = usuario;

        if (usuario == null) {
            // Modo ALTA
            esModificacion = false;
            lblTitulo.setText("Formulario de Alta (Registro)");
            // Limpiar campos por si acaso
            limpiarCampos();
            // Los campos de contraseña son relevantes
        } else {
            // Modo MODIFICACIÓN
            esModificacion = true;
            lblTitulo.setText("Formulario de Modificación");
            cargarDatosParaModificacion();
            // Los campos de contraseña permiten cambiarla (o no)
            txtPassword.setPromptText("Nueva Contraseña (dejar vacío para no cambiar)");
            txtConfirmarPassword.setPromptText("Repita la nueva contraseña");
        }
        // Actualizar visibilidad campos específicos según el rol cargado (o nulo en alta)
        actualizarCamposDinamicos(cmbRol.getValue());
    }

    // Método para limpiar campos (útil en Alta)
    private void limpiarCampos() {
        cmbRol.setValue(null);
        txtDni.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtGenero.clear();
        txtTelefono.clear();
        txtPassword.clear();
        txtConfirmarPassword.clear();
        txtInfoExtra.clear();
        txtDni.setEditable(true); // Asegurar que DNI sea editable en Alta
        txtDni.setStyle(""); // Quitar estilo de deshabilitado
    }

    // Método para manejar visibilidad de campos específicos (AHORA INDIVIDUAL)
    private void actualizarCamposDinamicos(String rol) {
        boolean visible = false;
        String labelText = "Información Específica:";
        String promptText = "";

        if (rol != null) {
            if ("Médico".equals(rol)) {
                visible = true;
                labelText = "Matrícula del Médico:";
                promptText = "Ej. 1234";
            } else if ("Administrador".equals(rol)) {
                visible = true;
                labelText = "Área del Administrador:";
                promptText = "Ej. Turnos";
            } else if ("Paciente".equals(rol)) {
                visible = true;
                labelText = "Obra Social:";
                promptText = "Ej. OSDE";
            }
        }

        // Aplicar visibilidad y textos a los componentes individuales
        if (lblInfoExtra != null && txtInfoExtra != null) {
            lblInfoExtra.setVisible(visible);
            lblInfoExtra.setManaged(visible);
            txtInfoExtra.setVisible(visible);
            txtInfoExtra.setManaged(visible);

            if (visible) {
                lblInfoExtra.setText(labelText);
                txtInfoExtra.setPromptText(promptText);
            }
        }
    }


    private void cargarDatosParaModificacion() {
        if (usuarioActual == null) return;

        txtDni.setText(usuarioActual.getDni());
        txtDni.setEditable(false);
        txtDni.setStyle("-fx-background-color: #eeeeee;");

        txtNombre.setText(usuarioActual.getNombre());
        txtApellido.setText(usuarioActual.getApellido());

        if (dataController != null) {
            jujava.mediturnos.logica.entidades.Persona p = dataController.getPersonaLogica(Integer.parseInt(usuarioActual.getDni()));
            if (p != null) {
                txtGenero.setText(String.valueOf(p.getGenero()));
                txtTelefono.setText(String.valueOf(p.getTelefono()));
            }
        }

        cmbRol.setValue(usuarioActual.getRol()); // Esto dispara actualizarCamposDinamicos
        txtInfoExtra.setText(usuarioActual.getInfoExtra()); // Cargar dato específico
        txtPassword.clear(); // Limpiar campos de contraseña en modificación
        txtConfirmarPassword.clear();
    }

    @FXML
    private void handleGuardar() {
        String dni = txtDni.getText();
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String rol = cmbRol.getValue();
        String infoExtra = (lblInfoExtra.isVisible()) ? txtInfoExtra.getText() : ""; // Usar visibilidad del label
        String genero = txtGenero.getText();
        String telefono = txtTelefono.getText();
        String password = txtPassword.getText(); // Siempre recolectar
        String confirmarPassword = txtConfirmarPassword.getText(); // Siempre recolectar

        Usuario usuarioParaGuardar;
        String passwordAGuardar = null; // Variable para pasar a MainController

        if (esModificacion) {
            // --- Lógica MODIFICACIÓN ---
            usuarioParaGuardar = usuarioActual; // Usamos el DTO existente
            usuarioParaGuardar.setNombre(nombre);
            usuarioParaGuardar.setApellido(apellido);
            usuarioParaGuardar.setRol(rol);
            usuarioParaGuardar.setInfoExtra(infoExtra);

            // Validar contraseña SÓLO si se ingresó algo
            if (!password.isEmpty()) {
                if (!password.equals(confirmarPassword)) {
                    dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "Las contraseñas no coinciden.");
                    return;
                }
                // Validar longitud mínima si se desea
                if (password.length() < 4) { // Ejemplo de longitud mínima
                    dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "La nueva contraseña debe tener al menos 4 caracteres.");
                    return;
                }
                passwordAGuardar = password; // Marcar para guardar la nueva contraseña
            } else {
                // Si ambos campos están vacíos, no se cambia la contraseña
                if (!confirmarPassword.isEmpty()) {
                    dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "Ingrese la nueva contraseña en ambos campos para cambiarla, o deje ambos vacíos.");
                    return;
                }
                passwordAGuardar = null; // Indica a MainController que no hay cambio de contraseña
            }

        } else {
            // --- Lógica ALTA ---
            usuarioParaGuardar = new Usuario(dni, nombre, apellido, rol, infoExtra); // Creamos DTO nuevo

            // Validar contraseña SIEMPRE en Alta
            if (password.isEmpty()) {
                dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "La contraseña es obligatoria para nuevos usuarios.");
                return;
            }
            if (!password.equals(confirmarPassword)) {
                dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "Las contraseñas no coinciden.");
                return;
            }
            // Validar longitud mínima si se desea
            if (password.length() < 4) { // Ejemplo de longitud mínima
                dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "La contraseña debe tener al menos 4 caracteres.");
                return;
            }
            passwordAGuardar = password; // Siempre se guarda la contraseña en Alta
        }

        // Llamar a MainController pasando la contraseña (o null si no cambia en modif.)
        boolean exito = dataController.guardarUsuario(usuarioParaGuardar, genero, telefono, esModificacion, passwordAGuardar);

        if (exito) {
            navigationController.handleListar();
        }
    }

    @FXML
    private void handleCancelar() {
        navigationController.handleListar();
    }
}

