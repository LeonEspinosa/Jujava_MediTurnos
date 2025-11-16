package jujava.mediturnos.presentacion.controladores;

import jujava.mediturnos.logica.*;
import jujava.mediturnos.logica.entidades.Persona;
import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.logica.entidades.Administrador;
import jujava.mediturnos.logica.entidades.Paciente;
import jujava.mediturnos.logica.entidades.Turno;
import jujava.mediturnos.presentacion.modelos.Usuario;
import jujava.mediturnos.presentacion.modelos.TurnoModel; // Nuevo import

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController {

    private final GestorUsuario gestorUsuario;
    private final GestorTurnos gestorTurno;
    private final ObservableList<Usuario> masterData;
    private final FilteredList<Usuario> filteredData;
    private Usuario usuarioSeleccionado;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public MainController() {
        try {
            this.gestorUsuario = new GestorUsuario();
            this.gestorTurno = new GestorTurnos(this.gestorUsuario);
            this.masterData = FXCollections.observableArrayList();
            cargarDatosDeLogica(); // Carga inicial
            this.filteredData = new FilteredList<>(masterData, p -> true);
        } catch (Exception e) {

            System.err.println("Error fatal al inicializar MainController: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException("No se pudo inicializar el controlador principal.", e);
        }

    }

    private void cargarDatosDeLogica() {
        masterData.clear(); // Limpiar antes de cargar
        try {
            if (gestorUsuario.getPacientes() != null) {
                for (Paciente p : gestorUsuario.getPacientes()) {
                    if (p != null) masterData.add(logicaAPresentacion(p));
                }
            }
            if (gestorUsuario.getMedicos() != null) {
                for (Medico m : gestorUsuario.getMedicos()) {
                    if (m != null) masterData.add(logicaAPresentacion(m));
                }
            }
            if (gestorUsuario.getAdministradores() != null) {
                for (Administrador a : gestorUsuario.getAdministradores()) {
                    if (a != null) masterData.add(logicaAPresentacion(a));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar datos de lógica a presentación: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar todos los datos iniciales.");
        }
    }


    private Usuario logicaAPresentacion(Persona p) {
        if (p == null) return null; // Chequeo de nulidad

        String rol = "Desconocido"; // Valor por defecto
        String infoExtra = "";
        String dniStr = String.valueOf(p.getDni()); // Convertir DNI a String


        if (p instanceof Paciente paciente) {
            rol = "Paciente";
            infoExtra = paciente.getObraSocial() != null ? paciente.getObraSocial() : "";
        } else if (p instanceof Medico medico) {
            rol = "Médico";
            infoExtra = medico.getMatricula() != null ? medico.getMatricula() : "";
        } else if (p instanceof Administrador admin) {
            rol = "Administrador";
            infoExtra = admin.getArea() != null ? admin.getArea() : "";
        }


        String nombre = p.getNombre() != null ? p.getNombre() : "";
        String apellido = p.getApellido() != null ? p.getApellido() : "";


        return new Usuario(dniStr, nombre, apellido, rol, infoExtra);
    }

    private TurnoModel turnoAListaPresentacion(Turno t) {
        if (t == null) return null;
        Persona paciente = getPersonaLogica(t.getDniPaciente());
        Persona medico = getPersonaLogica(t.getDniMedico());

        String nombreP = (paciente != null) ? paciente.getNombre() + " " + paciente.getApellido() : "DNI no encontrado";
        String nombreM = (medico != null) ? medico.getNombre() + " " + medico.getApellido() : "DNI no encontrado";

        String fechaHoraStr = t.getFechaHora().format(FORMATTER);

        return new TurnoModel(
                t.getIdTurno(),
                String.valueOf(t.getDniPaciente()),
                nombreP,
                String.valueOf(t.getDniMedico()),
                nombreM,
                t.getEspecialidad(),
                fechaHoraStr,
                t.getEstado()
        );
    }
    // Busca en lógica, maneja posible error de parseo
    public Persona getPersonaLogica(String dniStr) {
        if (dniStr == null || dniStr.trim().isEmpty()) {
            return null;
        }
        try {
            int dniInt = Integer.parseInt(dniStr.trim());
            return gestorUsuario.buscarUsuarioPorDNI(dniInt);
        } catch (NumberFormatException e) {
            System.err.println("Error al buscar persona lógica: DNI inválido '" + dniStr + "'");
            return null;
        }
    }
    // Sobrecarga para mantener compatibilidad si se usa con int
    public Persona getPersonaLogica(int dniInt) {
        return gestorUsuario.buscarUsuarioPorDNI(dniInt);
    }


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
        String dniTrimmed = (dni != null) ? dni.trim() : "";
        try {
            filteredData.setPredicate(usuario -> {
                if (usuario == null || usuario.getDni() == null) return false;
                if (dniTrimmed.isEmpty()) {
                    return true;
                }

                return usuario.getDni().equalsIgnoreCase(dniTrimmed);
            });


            if (filteredData.isEmpty() && !dniTrimmed.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Búsqueda", "No se encontró ningún usuario con DNI: " + dniTrimmed);
            }
        } catch (Exception e) {
            System.err.println("Error durante la búsqueda por DNI: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error de Búsqueda", "Ocurrió un error inesperado al filtrar.");
        }
    }


    /**
     * Guarda (Alta o Modificación) un usuario.
     * Ahora recibe la contraseña (puede ser null si no se modifica).
     */
    public boolean guardarUsuario(Usuario dto, String genero, String telefono, boolean esModificacion, String password) {

        // Validación básica de DTO y datos extra
        if (dto == null || !validarDatosUI(dto, genero, telefono)) {
            return false;
        }

        int dniInt;
        int telInt;
        char genChar;
        try {
            dniInt = Integer.parseInt(dto.getDni().trim());

            telInt = Integer.parseInt(telefono.trim());
            if (telInt < 0) throw new NumberFormatException("Teléfono no puede ser negativo.");

            // Validar Género (M o F)
            String generoTrim = genero.trim().toUpperCase();
            if (generoTrim.length() != 1 || (generoTrim.charAt(0) != 'M' && generoTrim.charAt(0) != 'F')) {
                throw new IllegalArgumentException("Género debe ser 'M' o 'F'.");
            }
            genChar = generoTrim.charAt(0);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error de Formato", "DNI y Teléfono deben ser números válidos. " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Error de Formato", e.getMessage());
            return false;
        } catch (Exception e){
            showAlert(Alert.AlertType.ERROR, "Error de Formato", "Error inesperado al procesar DNI, Teléfono o Género.");
            System.err.println("Error parseando datos numéricos/char: " + e.getMessage());
            return false;
        }

        try { // Envolver lógica de negocio en try-catch
            if (esModificacion) {
                // --- LÓGICA DE MODIFICACIÓN ---
                String rol = dto.getRol();
                boolean datosModificados = false;
                boolean passwordModificada = false;

                // --- 1. MODIFICAR CONTRASEÑA (SI SE PROPORCIONÓ) ---

                if (password != null && !password.isEmpty()) {
                    // Llama al método específico en GestorUsuario para cambiar SÓLO la contraseña
                    passwordModificada = gestorUsuario.modificarPasswordUsuario(dniInt, password);
                    if(!passwordModificada){
                        // Podríamos mostrar una advertencia si falló, pero GestorUsuario ya loguea el error
                        System.err.println("Advertencia desde MainController: Falló la modificación de contraseña para DNI " + dniInt);
                    }
                }

                // --- 2. MODIFICAR OTROS DATOS ---
                // Siempre intentamos modificar los otros datos, usando los valores actuales del DTO
                // que fueron actualizados desde el formulario en FormularioViewController.
                // Es crucial que 'dto.getInfoExtra()' contenga el valor correcto del campo 'txtInfoExtra'.
                if ("Paciente".equals(rol)) {
                    gestorUsuario.modificarPaciente(dniInt, dto.getNombre(), dto.getApellido(), genChar, telInt, dto.getInfoExtra());
                    datosModificados = true;
                } else if ("Médico".equals(rol)) {
                    Medico m = gestorUsuario.buscarMedicoPorDNI(dniInt);
                    String especialidadActual = (m != null) ? m.getEspecialidad() : "Default";
                    gestorUsuario.modificarMedico(dniInt, dto.getNombre(), dto.getApellido(), genChar, telInt, dto.getInfoExtra(), especialidadActual);
                    datosModificados = true;
                } else if ("Administrador".equals(rol)) {

                    gestorUsuario.modificarAdministrador(dniInt, dto.getNombre(), dto.getApellido(), genChar, telInt, dto.getInfoExtra());
                    datosModificados = true;
                }

                // --- 3. ACTUALIZAR UI y MOSTRAR MENSAJE ---
                if(datosModificados || passwordModificada) {

                    Optional<Usuario> usuarioEnListaOpt = masterData.stream().filter(u -> u != null && u.getDni() != null && u.getDni().equals(dto.getDni())).findFirst(); // Añadido chequeo null
                    if(usuarioEnListaOpt.isPresent()){
                        Usuario usuarioEnLista = usuarioEnListaOpt.get();

                        usuarioEnLista.setNombre(dto.getNombre());
                        usuarioEnLista.setApellido(dto.getApellido());
                        usuarioEnLista.setRol(dto.getRol());
                        usuarioEnLista.setInfoExtra(dto.getInfoExtra());

                        masterData.set(masterData.indexOf(usuarioEnLista), usuarioEnLista);
                    } else {
                        System.err.println("Advertencia: Usuario modificado (DNI: " + dto.getDni() + ") no encontrado en masterData para refrescar UI.");

                    }

                    // Construir mensaje de éxito
                    String mensajeExito = "Usuario modificado correctamente.";
                    if (passwordModificada) {
                        mensajeExito += " Contraseña actualizada.";
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", mensajeExito);
                } else {

                    showAlert(Alert.AlertType.INFORMATION, "Información", "No se detectaron cambios para guardar.");

                }
            } else {
                // --- LÓGICA DE ALTA ---
                // Validar DNI único
                if (!gestorUsuario.validarDNIUnico(dto.getDni())) {
                    showAlert(Alert.AlertType.ERROR, "Error", "El DNI ingresado ya existe.");
                    return false;
                }
                // Validar que la contraseña no sea nula o vacía (FormularioViewController ya lo hizo, pero doble check)
                if (password == null || password.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "La contraseña es obligatoria.");
                    return false;
                }


                Persona nuevaPersonaLogica = null;
                String rol = dto.getRol();

                // Crear objeto de Lógica SIN HASH
                if ("Paciente".equals(rol)) {

                    nuevaPersonaLogica = new Paciente(dto.getNombre(), dto.getApellido(), dniInt, genChar, telInt, null, dto.getInfoExtra());
                    gestorUsuario.agregarPaciente((Paciente) nuevaPersonaLogica, password);
                } else if ("Médico".equals(rol)) {

                    nuevaPersonaLogica = new Medico(dto.getNombre(), dto.getApellido(), dniInt, genChar, telInt, null, dto.getInfoExtra(), "Especialidad_Default"); // Añadir especialidad default
                    gestorUsuario.agregarMedico((Medico) nuevaPersonaLogica, password);
                } else if ("Administrador".equals(rol)) {

                    nuevaPersonaLogica = new Administrador(dto.getNombre(), dto.getApellido(), dniInt, genChar, telInt, null, dto.getInfoExtra());
                    gestorUsuario.agregarAdministrador((Administrador) nuevaPersonaLogica, password);
                }

                // Verificar si se creó y añadir a la UI
                if (nuevaPersonaLogica != null) {

                    masterData.add(dto);
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Usuario '" + dto.getNombre() + "' registrado correctamente.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "No se pudo crear el usuario (Rol no válido o error interno).");
                    return false;
                }
            }

            this.usuarioSeleccionado = null;
            return true;

        } catch (Exception e) {

            System.err.println("Error al guardar usuario (DNI: " + dniInt + "): " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error al guardar los datos del usuario.");
            return false;
        }
    }


    public void eliminarUsuarioSeleccionado() {
        if (usuarioSeleccionado == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar una fila para eliminar.");
            return;
        }

        // Obtener DNI del seleccionado de forma segura
        String dniAEliminarStr = usuarioSeleccionado.getDni();
        int dniAEliminarInt;
        try {
            // Validar que el DNI no sea null o vacío antes de parsear
            if (dniAEliminarStr == null || dniAEliminarStr.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error Interno", "El usuario seleccionado no tiene un DNI válido.");
                return;
            }
            dniAEliminarInt = Integer.parseInt(dniAEliminarStr.trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error Interno", "El DNI seleccionado '" + dniAEliminarStr + "' no es un número válido.");
            return;
        }

        // --- PREVENIR ELIMINACIÓN DEL ADMIN POR DEFECTO ---
        // Asumiendo que DEFAULT_ADMIN_DNI es 0 como en GestorUsuario
        final int DEFAULT_ADMIN_DNI = 0; // Podrías hacerlo una constante de clase si prefieres
        if (dniAEliminarInt == DEFAULT_ADMIN_DNI) {
            showAlert(Alert.AlertType.ERROR, "Operación no permitida", "No se puede eliminar al administrador por defecto (DNI: 0).");
            return;
        }
        // --- FIN PREVENCIÓN ---


        Optional<ButtonType> result = showConfirmation("¿Seguro que desea eliminar al usuario con DNI " + dniAEliminarStr + "?");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // 1. Llamar a la capa de Lógica para eliminar y persistir
                gestorUsuario.eliminarUsuario(dniAEliminarInt); // GestorUsuario ahora maneja la persistencia y errores internos

                // 2. Si la lógica no lanzó excepción, proceder a actualizar la UI
                // --- CORRECCIÓN: Buscar y eliminar por DNI en masterData ---
                final String dniFinal = dniAEliminarStr.trim(); // DNI a buscar
                Optional<Usuario> usuarioParaEliminarOpt = masterData.stream()
                        .filter(u -> u != null && u.getDni() != null && u.getDni().equals(dniFinal))
                        .findFirst();

                if (usuarioParaEliminarOpt.isPresent()) {
                    boolean removed = masterData.remove(usuarioParaEliminarOpt.get());
                    if (removed) {
                        showAlert(Alert.AlertType.INFORMATION, "Eliminación", "Usuario eliminado correctamente.");
                        usuarioSeleccionado = null; // Deseleccionar
                    } else {
                        // Esto sería muy raro si se encontró el Optional
                        System.err.println("Advertencia: Se encontró el usuario en masterData pero remove() falló.");
                        showAlert(Alert.AlertType.WARNING, "Error de UI", "No se pudo actualizar la lista visualmente.");
                        cargarDatosDeLogica(); // Recargar como fallback si falla la eliminación visual
                    }
                } else {
                    // Si GestorUsuario eliminó pero no lo encontramos en masterData (raro)
                    System.err.println("Advertencia: Usuario DNI " + dniFinal + " eliminado en lógica, pero no encontrado en masterData para actualizar UI.");
                    showAlert(Alert.AlertType.WARNING, "Advertencia", "El usuario fue eliminado, pero la lista no se actualizó correctamente.");
                    cargarDatosDeLogica(); // Recargar como fallback
                }
                // --- FIN CORRECCIÓN ---

            } catch (IllegalArgumentException e) {
                // Capturar errores específicos si GestorUsuario los lanza (ej. DNI inválido)
                System.err.println("Error al eliminar (lógica): " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Error de Datos", e.getMessage());
            } catch (RuntimeException e) { // Capturar otras posibles excepciones de la lógica
                System.err.println("Error inesperado en lógica al eliminar DNI " + dniAEliminarInt + ": " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error en la capa de lógica al eliminar.");
            } catch (Exception e) {
                // Capturar errores generales inesperados
                System.err.println("Error inesperado al intentar eliminar usuario DNI " + dniAEliminarInt + ": " + e.getMessage());
                e.printStackTrace(); // Para debug
                showAlert(Alert.AlertType.ERROR, "Error de Eliminación", "Ocurrió un error inesperado al eliminar.");
            }
        }
    }

    // Validación básica de campos UI (podría ser más extensa)
    private boolean validarDatosUI(Usuario u, String genero, String telefono) {
        // Chequeos básicos de nulidad y vacío
        if (u == null) {
            showAlert(Alert.AlertType.ERROR, "Error Interno", "Referencia de usuario nula.");
            return false;
        }
        if (u.getDni() == null || u.getDni().trim().isEmpty() ||
                u.getNombre() == null || u.getNombre().trim().isEmpty() ||
                u.getApellido() == null || u.getApellido().trim().isEmpty() || // Añadido chequeo apellido
                u.getRol() == null || // Rol debe estar seleccionado
                genero == null || genero.trim().isEmpty() ||
                telefono == null || telefono.trim().isEmpty())
        {
            showAlert(Alert.AlertType.ERROR, "Campos Obligatorios", "DNI, Nombre, Apellido, Rol, Género y Teléfono son obligatorios.");
            return false;
        }

        // Validación de infoExtra según el rol
        String rol = u.getRol();
        String infoExtra = u.getInfoExtra() != null ? u.getInfoExtra().trim() : ""; // Chequeo null

        if (("Médico".equals(rol) && infoExtra.isEmpty()) ||
                ("Administrador".equals(rol) && infoExtra.isEmpty()) ||
                ("Paciente".equals(rol) && infoExtra.isEmpty()))
        {
            String campoFaltante = "desconocido";
            if ("Médico".equals(rol)) campoFaltante = "Matrícula";
            else if ("Administrador".equals(rol)) campoFaltante = "Área";
            else if ("Paciente".equals(rol)) campoFaltante = "Obra Social";

            showAlert(Alert.AlertType.ERROR, "Campo Obligatorio", "Debe ingresar la información específica para el rol '" + rol + "': " + campoFaltante + ".");
            return false;
        }



        return true;
    }

    public ObservableList<TurnoModel> getTurnosParaMedico(int dniMedico) {
        List<Turno> turnosLogica = gestorTurno.getTurnosPorMedico(dniMedico);
        List<TurnoModel> turnosPresentacion = turnosLogica.stream()
                .map(this::turnoAListaPresentacion)
                .collect(Collectors.toList());
        return FXCollections.observableArrayList(turnosPresentacion);
    }

    public ObservableList<TurnoModel> getTodosLosTurnos() {
        List<Turno> turnosLogica = gestorTurno.getTodosLosTurnos();
        List<TurnoModel> turnosPresentacion = turnosLogica.stream()
                .map(this::turnoAListaPresentacion)
                .collect(Collectors.toList());
        return FXCollections.observableArrayList(turnosPresentacion);
    }

    public boolean cancelarTurno(int idTurno) {
        return gestorTurno.cancelarTurno(idTurno);
    }

    public List<String> getEspecialidades() {
        // Llama a GestorTurnos, que a su vez usa GestorUsuario para obtener las especialidades.
        return gestorTurno.getEspecialidadesDisponibles();
    }

    public List<Medico> getMedicosPorEspecialidad(String especialidad) {
        return gestorTurno.getMedicosPorEspecialidad(especialidad);
    }

    public boolean solicitarTurno(int dniPaciente, int dniMedico, String especialidad, LocalDateTime fechaHora) {
        return gestorTurno.solicitarTurno(dniPaciente, dniMedico, especialidad, fechaHora);
    }

    public boolean modificarTurnoAdmin(int idTurno, int dniMedico, String especialidad, LocalDateTime fechaHora, String estado) {
        return gestorTurno.modificarTurno(idTurno, dniMedico, especialidad, fechaHora, estado);
    }

    public boolean completarTurno(int idTurno) {
        return gestorTurno.completarTurno(idTurno);
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // Tipo confirmación
        alert.setTitle("Confirmación");
        alert.setHeaderText(null); // Sin texto de cabecera
        alert.setContentText(content);

        // ButtonType buttonTypeYes = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        // ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        // alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        return alert.showAndWait(); // Devuelve el botón presionado
    }

    public boolean agregarEspecialidad(String nuevaEspecialidad) {
        return gestorTurno.agregarEspecialidad(nuevaEspecialidad);
    }

    public boolean eliminarEspecialidad(String especialidad) {
        return gestorTurno.eliminarEspecialidad(especialidad);
    }
}

