package jujava.mediturnos.presentacion.controladores;

// 1. IMPORTAMOS LAS CLASES DE LÓGICA Y MODELOS DE PRESENTACIÓN
import jujava.mediturnos.logica.*;
import jujava.mediturnos.logica.entidades.Persona;
import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.logica.entidades.Administrador;
import jujava.mediturnos.logica.entidades.Paciente;

import jujava.mediturnos.presentacion.modelos.Usuario; // El DTO de presentación
import jujava.mediturnos.datos.AccesoDatos; // Importación necesaria para guardar

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Controlador Lógico (Capa de Presentación) - EL PUENTE.
 * (Versión corregida sin lógica de negocio duplicada)
 */
public class MainController {

    private final GestorUsuario gestorUsuario;
    private final ObservableList<Usuario> masterData;
    private final FilteredList<Usuario> filteredData;
    private Usuario usuarioSeleccionado;

    public MainController() {
        this.gestorUsuario = new GestorUsuario(); // Esto ya no causa un bucle
        this.masterData = FXCollections.observableArrayList();
        cargarDatosDeLogica();
        this.filteredData = new FilteredList<>(masterData, p -> true);
    }

    /**
     * Carga todos los usuarios de la capa de lógica (CSV) y los
     * convierte en DTOs de 'Usuario' (presentación) para la TableView.
     */
    private void cargarDatosDeLogica() {
        masterData.clear();
        for (Paciente p : gestorUsuario.getPacientes()) {
            masterData.add(logicaAPresentacion(p));
        }
        for (Medico m : gestorUsuario.getMedicos()) {
            masterData.add(logicaAPresentacion(m));
        }
        for (Administrador a : gestorUsuario.getAdministradores()) {
            masterData.add(logicaAPresentacion(a));
        }
    }

    /**
     * Mapeador (Helper).
     * Convierte un objeto de Lógica (Persona) en un DTO de Presentación (Usuario).
     */
    private Usuario logicaAPresentacion(Persona p) {
        String rol = "";
        String infoExtra = "";

        if (p instanceof Paciente) {
            rol = "Paciente";
            infoExtra = ((Paciente) p).getObraSocial();
        } else if (p instanceof Medico) {
            rol = "Médico";
            infoExtra = ((Medico) p).getMatricula();
        } else if (p instanceof Administrador) {
            rol = "Administrador";
            infoExtra = ((Administrador) p).getArea();
        }

        return new Usuario(
                String.valueOf(p.getDni()),
                p.getNombre(),
                p.getApellido(),
                rol,
                infoExtra
        );
    }

    public Persona getPersonaLogica(int dni) {
        return gestorUsuario.buscarUsuarioPorDNI(dni);
    }


    // --- Métodos públicos para los Controladores FXML ---

    public FilteredList<Usuario> getFilteredData() {
        return filteredData;
    }

    public Usuario getUsuarioSeleccionado() {
        return usuarioSeleccionado;
    }

    public void setUsuarioSeleccionado(Usuario usuario) {
        this.usuarioSeleccionado = usuario;
    }

    public void buscarUsuarioPorDNI(String dni) {
        String dniTrimmed = dni.trim();
        filteredData.setPredicate(usuario -> {
            if (dniTrimmed.isEmpty()) {
                return true;
            }
            return usuario.getDni().equalsIgnoreCase(dniTrimmed);
        });

        if (filteredData.isEmpty() && !dniTrimmed.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Búsqueda", "No se encontró ningún usuario con ese DNI en la lista cargada.");
        }
    }

    /**
     * ¡CONEXIÓN DE ESCRITURA! (Versión Simplificada)
     * Ahora solo delega la lógica a GestorUsuario.
     */
    public boolean guardarUsuario(Usuario dto, String genero, String telefono, boolean esModificacion) {

        if (!validarDatosUI(dto, genero, telefono)) {
            return false;
        }

        int dniInt;
        int telInt;
        char genChar;
        try {
            dniInt = Integer.parseInt(dto.getDni().trim());
            telInt = Integer.parseInt(telefono.trim());
            genChar = genero.trim().toUpperCase().charAt(0);
            if (genChar != 'M' && genChar != 'F') throw new Exception("Género debe ser M o F.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error de Formato", "DNI y Teléfono deben ser números. Género debe ser M o F.");
            return false;
        }

        if (esModificacion) {
            // --- LÓGICA DE MODIFICACIÓN (Ahora delegada) ---

            // ERROR 47: La capa de presentación (MainController) ya no llama a AccesoDatos.
            // Delega la responsabilidad a la capa de Lógica (GestorUsuario).

            String rol = dto.getRol();
            if ("Paciente".equals(rol)) {
                gestorUsuario.modificarPaciente(dniInt, dto.getNombre(), dto.getApellido(), genChar, telInt, dto.getInfoExtra());
            } else if ("Médico".equals(rol)) {
                // Asumimos "Especialidad_Default" ya que el formulario no la pide
                String especialidadActual = (gestorUsuario.buscarMedicoPorDNI(dniInt) != null) ? gestorUsuario.buscarMedicoPorDNI(dniInt).getEspecialidad() : "Default";
                gestorUsuario.modificarMedico(dniInt, dto.getNombre(), dto.getApellido(), genChar, telInt, dto.getInfoExtra(), especialidadActual);
            } else if ("Administrador".equals(rol)) {
                gestorUsuario.modificarAdministrador(dniInt, dto.getNombre(), dto.getApellido(), genChar, telInt, dto.getInfoExtra());
            }

            // Actualizar la UI (masterData)
            usuarioSeleccionado.setNombre(dto.getNombre());
            usuarioSeleccionado.setApellido(dto.getApellido());
            usuarioSeleccionado.setRol(dto.getRol());
            usuarioSeleccionado.setInfoExtra(dto.getInfoExtra());

            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Usuario modificado correctamente.");

        } else {
            // --- LÓGICA DE ALTA ---
            if (!gestorUsuario.validarDNIUnico(dto.getDni())) {
                showAlert(Alert.AlertType.ERROR, "Error", "El DNI ingresado ya existe.");
                return false;
            }

            Persona nuevaPersonaLogica = null;
            String rol = dto.getRol();

            if ("Paciente".equals(rol)) {
                nuevaPersonaLogica = new Paciente(dto.getNombre(), dto.getApellido(), dniInt, genChar, telInt, dto.getInfoExtra());
                gestorUsuario.agregarPaciente((Paciente) nuevaPersonaLogica);
            } else if ("Médico".equals(rol)) {
                nuevaPersonaLogica = new Medico(dto.getNombre(), dto.getApellido(), dniInt, genChar, telInt, dto.getInfoExtra(), "Especialidad_Default");
                gestorUsuario.agregarMedico((Medico) nuevaPersonaLogica);
            } else if ("Administrador".equals(rol)) {
                nuevaPersonaLogica = new Administrador(dto.getNombre(), dto.getApellido(), dniInt, genChar, telInt, dto.getInfoExtra());
                gestorUsuario.agregarAdministrador((Administrador) nuevaPersonaLogica);
            }

            if (nuevaPersonaLogica != null) {
                masterData.add(dto);
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Usuario registrado correctamente.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo crear el usuario.");
                return false;
            }
        }

        this.usuarioSeleccionado = null;
        return true;
    }

    public void eliminarUsuarioSeleccionado() {
        if (usuarioSeleccionado == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una fila para eliminar.");
            return;
        }

        Optional<ButtonType> result = showConfirmation("¿Seguro que desea eliminar al usuario con DNI " + usuarioSeleccionado.getDni() + "?");

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                int dniInt = Integer.parseInt(usuarioSeleccionado.getDni());
                // 1. Llamar a la capa de Lógica
                gestorUsuario.eliminarUsuario(dniInt);

                // 2. Si no hay error, actualizar la UI
                masterData.remove(usuarioSeleccionado);
                showAlert(Alert.AlertType.INFORMATION, "Eliminación", "Usuario eliminado correctamente.");
                usuarioSeleccionado = null;

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al eliminar: " + e.getMessage());
            }
        }
    }

    private boolean validarDatosUI(Usuario u, String genero, String telefono) {
        if (u.getDni().trim().isEmpty() || u.getNombre().trim().isEmpty() || u.getRol() == null || genero.trim().isEmpty() || telefono.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "DNI, Nombre, Rol, Género y Teléfono son obligatorios.");
            return false;
        }
        if (("Médico".equals(u.getRol()) || "Administrador".equals(u.getRol()) || "Paciente".equals(u.getRol()))
                && u.getInfoExtra().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Rellene la información específica para el rol seleccionado (Obra Social, Matrícula o Área).");
            return false;
        }
        return true;
    }

    // --- Métodos de UI (Helpers) ---

    public void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public Optional<ButtonType> showConfirmation(String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        return alert.showAndWait();
    }
}

