package jujava.mediturnos.presentacion.controladores;

import jujava.mediturnos.presentacion.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField; // Importar PasswordField
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import java.util.List;
// import javafx.scene.layout.VBox;
// import javafx.scene.layout.RowConstraints;

import java.util.Arrays;
import javafx.stage.Stage; // <-- PASO 2.1: AÑADIR IMPORT

public class FormularioViewController {

    @FXML private Label lblTitulo;
    @FXML private ComboBox<String> cmbRol;
    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtGenero;
    @FXML private TextField txtTelefono;

    @FXML private Label lblEspecialidad;
    @FXML private ComboBox<String> cmbEspecialidad;


    @FXML private Label lblPassword;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblConfirmarPassword;
    @FXML private PasswordField txtConfirmarPassword;



    @FXML private Label lblInfoExtra;
    @FXML private TextField txtInfoExtra;


    private MainController dataController;
    private MainViewController navigationController;
    private Usuario usuarioActual;
    private boolean esModificacion;

    private Stage modalStage; // <-- PASO 2.2: AÑADIR VARIABLE

    @FXML
    public void initialize() {
        cmbRol.getItems().addAll(Arrays.asList("Paciente", "Médico", "Administrador"));
        cmbRol.valueProperty().addListener((obs, oldVal, newVal) -> actualizarCamposDinamicos(newVal));

        actualizarCamposDinamicos(null);
    }

    /**
     * Modo 1: Inicializador para el Administrador (EXISTENTE)
     */
    public void initData(MainController dataController, MainViewController navigationController, Usuario usuario) {
        this.dataController = dataController;
        this.navigationController = navigationController;
        this.usuarioActual = usuario;
        this.modalStage = null; // No estamos en modo modal

        if (usuario == null) {

            esModificacion = false;
            lblTitulo.setText("Formulario de Alta (Registro Admin)");
            limpiarCampos();

        } else {

            esModificacion = true;
            lblTitulo.setText("Formulario de Modificación");
            //cargarDatosParaModificacion();

            txtPassword.setPromptText("Nueva Contraseña (dejar vacío para no cambiar)");
            txtConfirmarPassword.setPromptText("Repita la nueva contraseña");
        }

        actualizarCamposDinamicos(cmbRol.getValue());

        if (esModificacion) {
            cargarDatosParaModificacion();
        }
    }

    /**
     * PASO 2.3: AÑADIR NUEVO MÉTODO initData
     * Modo 2: Inicializador para Auto-Registro de Paciente (NUEVO)
     */
    public void initData(MainController dataController, Stage modalStage) {
        this.dataController = dataController;
        this.modalStage = modalStage; // Guardamos la ventana modal
        this.navigationController = null; // No hay navegación
        this.usuarioActual = null;
        this.esModificacion = false;

        lblTitulo.setText("Registro de Nuevo Paciente");
        limpiarCampos();

        // Forzar y bloquear el rol "Paciente"
        cmbRol.setValue("Paciente");
        cmbRol.setDisable(true);
        actualizarCamposDinamicos("Paciente");
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
        if (cmbEspecialidad != null) {
            cmbEspecialidad.getItems().clear();
            cmbEspecialidad.setValue(null);
        }
        txtDni.setEditable(true);
        txtDni.setStyle("");
        cmbRol.setDisable(false); // Habilitar CmbRol por si estaba deshabilitado
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
        boolean medicoVisible = "Médico".equals(rol);

        if (lblEspecialidad != null && cmbEspecialidad != null) {
            lblEspecialidad.setVisible(medicoVisible);
            lblEspecialidad.setManaged(medicoVisible);
            cmbEspecialidad.setVisible(medicoVisible);
            cmbEspecialidad.setManaged(medicoVisible);

            if (medicoVisible && dataController != null) {
                // Si es médico, poblamos el ComboBox
                try {
                    List<String> especialidades = dataController.getEspecialidades();
                    cmbEspecialidad.setItems(FXCollections.observableArrayList(especialidades));
                } catch (Exception e) {
                    // System.err.println("Error al cargar especialidades en el formulario: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // Si no es médico, limpiamos
                cmbEspecialidad.getItems().clear();
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

        if ("Médico".equals(usuarioActual.getRol()) && dataController != null) {
            try {
                // Buscamos al médico en la capa de lógica para obtener su especialidad actual
                jujava.mediturnos.logica.entidades.Persona p = dataController.getPersonaLogica(Integer.parseInt(usuarioActual.getDni()));
                if (p instanceof jujava.mediturnos.logica.entidades.Medico) {
                    String especialidadActual = ((jujava.mediturnos.logica.entidades.Medico) p).getEspecialidad();
                    cmbEspecialidad.setValue(especialidadActual); // Seleccionamos la especialidad actual
                }
            } catch (NumberFormatException e) {
                // System.err.println("Error al parsear DNI en cargarDatosParaModificacion: " + e.getMessage());
                e.printStackTrace();
            }
        }
        txtPassword.clear(); // Limpiar campos de contraseña en modificación
        txtConfirmarPassword.clear();
    }

    @FXML
    private void handleGuardar() {
        String dni = txtDni.getText();
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String rol = cmbRol.getValue();
        String infoExtra = (lblInfoExtra.isVisible()) ? txtInfoExtra.getText() : "";
        String genero = txtGenero.getText();
        String telefono = txtTelefono.getText();
        String password = txtPassword.getText();
        String confirmarPassword = txtConfirmarPassword.getText();

        String especialidad = null;
        if ("Médico".equals(rol)) {
            especialidad = cmbEspecialidad.getValue();
            if (especialidad == null || especialidad.trim().isEmpty()) {
                dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "Debe seleccionar una especialidad para el médico.");
                return;
            }
        }

        Usuario usuarioParaGuardar;
        String passwordAGuardar = null;

        if (esModificacion) {
            // ... (LÓGICA DE MODIFICACIÓN EXISTENTE - NO CAMBIA) ...
            usuarioParaGuardar = usuarioActual;
            usuarioParaGuardar.setNombre(nombre);
            usuarioParaGuardar.setApellido(apellido);
            usuarioParaGuardar.setRol(rol);
            usuarioParaGuardar.setInfoExtra(infoExtra);

            if (!password.isEmpty()) {
                if (!password.equals(confirmarPassword)) {
                    dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "Las contraseñas no coinciden.");
                    return;
                }

                if (password.length() < 4) {
                    dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "La nueva contraseña debe tener al menos 4 caracteres.");
                    return;
                }
                passwordAGuardar = password;
            } else {

                if (!confirmarPassword.isEmpty()) {
                    dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "Ingrese la nueva contraseña en ambos campos para cambiarla, o deje ambos vacíos.");
                    return;
                }
                passwordAGuardar = null;
            }

        } else {
            // --- Lógica ALTA ---
            usuarioParaGuardar = new Usuario(dni, nombre, apellido, rol, infoExtra);


            if (password.isEmpty()) {
                dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "La contraseña es obligatoria para nuevos usuarios.");
                return;
            }
            if (!password.equals(confirmarPassword)) {
                dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "Las contraseñas no coinciden.");
                return;
            }
            // Validar longitud mínima si se desea
            if (password.length() < 4) {
                dataController.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error", "La contraseña debe tener al menos 4 caracteres.");
                return;
            }
            passwordAGuardar = password;
        }

        // Llamar a MainController pasando la contraseña
        boolean exito = dataController.guardarUsuario(usuarioParaGuardar, genero, telefono, esModificacion, passwordAGuardar);


        // --- PASO 2.4: MODIFICAR LÓGICA DE NAVEGACIÓN POST-GUARDADO ---
        if (exito) {
            if (navigationController != null) {
                // Modo Admin: volver a la lista
                navigationController.handleListar();
            } else if (modalStage != null) {
                // Modo Registro: mostrar alerta de éxito y cerrar ventana
                dataController.showAlert(javafx.scene.control.Alert.AlertType.INFORMATION, "Registro Exitoso", "¡Usuario paciente registrado correctamente! Ya puede iniciar sesión.");
                modalStage.close();
            }
        }
    }

    @FXML
    private void handleCancelar() {

        // --- PASO 2.5: MODIFICAR LÓGICA DE CANCELAR ---
        if (navigationController != null) {
            // Modo Admin: volver a la lista
            navigationController.handleListar();
        } else if (modalStage != null) {
            // Modo Registro: solo cerrar la ventana
            modalStage.close();
        }
    }
}