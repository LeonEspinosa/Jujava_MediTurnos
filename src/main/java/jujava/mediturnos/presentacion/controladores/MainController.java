package jujava.mediturnos.presentacion.controladores;

import jujava.mediturnos.logica.*;
import jujava.mediturnos.logica.entidades.Persona;
import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.logica.entidades.Administrador;
import jujava.mediturnos.logica.entidades.Paciente;
import jujava.mediturnos.presentacion.modelos.Usuario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class MainController {

    private final GestorUsuario gestorUsuario;
    private final ObservableList<Usuario> masterData;
    private final FilteredList<Usuario> filteredData;
    private Usuario usuarioSeleccionado;

    public MainController() {
        try {
            this.gestorUsuario = new GestorUsuario();
            this.masterData = FXCollections.observableArrayList();
            cargarDatosDeLogica(); // Carga inicial
            this.filteredData = new FilteredList<>(masterData, p -> true);
        } catch (Exception e) {
            // Captura errores durante la inicialización (ej. error al leer CSVs)
            System.err.println("Error fatal al inicializar MainController: " + e.getMessage());
            e.printStackTrace();
            // Considerar mostrar un Alert aquí si es posible o terminar la app
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
            // Podrías mostrar una alerta aquí
            showAlert(Alert.AlertType.ERROR, "Error de Carga", "No se pudieron cargar todos los datos iniciales.");
        }
    }


    private Usuario logicaAPresentacion(Persona p) {
        if (p == null) return null; // Chequeo de nulidad

        String rol = "Desconocido"; // Valor por defecto
        String infoExtra = "";
        String dniStr = String.valueOf(p.getDni()); // Convertir DNI a String

        // Usar instanceof con pattern matching (Java 16+) para seguridad
        if (p instanceof Paciente paciente) {
            rol = "Paciente";
            infoExtra = paciente.getObraSocial() != null ? paciente.getObraSocial() : ""; // Chequeo null
        } else if (p instanceof Medico medico) {
            rol = "Médico";
            infoExtra = medico.getMatricula() != null ? medico.getMatricula() : ""; // Chequeo null
        } else if (p instanceof Administrador admin) {
            rol = "Administrador";
            infoExtra = admin.getArea() != null ? admin.getArea() : ""; // Chequeo null
        }

        // Asegurarse que nombre y apellido no sean null
        String nombre = p.getNombre() != null ? p.getNombre() : "";
        String apellido = p.getApellido() != null ? p.getApellido() : "";


        return new Usuario(dniStr, nombre, apellido, rol, infoExtra);
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
        String dniTrimmed = (dni != null) ? dni.trim() : ""; // Chequeo null
        try {
            filteredData.setPredicate(usuario -> {
                if (usuario == null || usuario.getDni() == null) return false; // Chequeo null en predicado
                if (dniTrimmed.isEmpty()) {
                    return true; // Mostrar todos si la búsqueda está vacía
                }
                // Comparación segura ignorando mayúsculas/minúsculas
                return usuario.getDni().equalsIgnoreCase(dniTrimmed);
            });

            // Mostrar mensaje solo si la búsqueda NO está vacía y no hay resultados
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
            return false; // Error ya mostrado en validarDatosUI
        }

        int dniInt;
        int telInt;
        char genChar;
        try {
            dniInt = Integer.parseInt(dto.getDni().trim());
            // Validar teléfono como número positivo (o permitir 0 si es válido)
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
                String rol = dto.getRol(); // Rol actual del DTO/Formulario
                boolean datosModificados = false;
                boolean passwordModificada = false;

                // --- 1. MODIFICAR CONTRASEÑA (SI SE PROPORCIONÓ) ---
                // Verifica si se ingresó una nueva contraseña en el formulario
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
                    datosModificados = true; // Asumimos que se intentó modificar
                } else if ("Médico".equals(rol)) {
                    Medico m = gestorUsuario.buscarMedicoPorDNI(dniInt);
                    String especialidadActual = (m != null) ? m.getEspecialidad() : "Default";
                    // Asegúrate de pasar dto.getInfoExtra() como matrícula aquí
                    gestorUsuario.modificarMedico(dniInt, dto.getNombre(), dto.getApellido(), genChar, telInt, dto.getInfoExtra(), especialidadActual);
                    datosModificados = true;
                } else if ("Administrador".equals(rol)) {
                    // Asegúrate de pasar dto.getInfoExtra() como área aquí
                    gestorUsuario.modificarAdministrador(dniInt, dto.getNombre(), dto.getApellido(), genChar, telInt, dto.getInfoExtra());
                    datosModificados = true;
                }

                // --- 3. ACTUALIZAR UI y MOSTRAR MENSAJE ---
                if(datosModificados || passwordModificada) {
                    // Refrescar DTO en la lista masterData (más seguro que modificarlo directamente)
                    Optional<Usuario> usuarioEnListaOpt = masterData.stream().filter(u -> u != null && u.getDni() != null && u.getDni().equals(dto.getDni())).findFirst(); // Añadido chequeo null
                    if(usuarioEnListaOpt.isPresent()){
                        Usuario usuarioEnLista = usuarioEnListaOpt.get();
                        // Actualizar el DTO en la lista con los valores del formulario
                        usuarioEnLista.setNombre(dto.getNombre());
                        usuarioEnLista.setApellido(dto.getApellido());
                        usuarioEnLista.setRol(dto.getRol());
                        usuarioEnLista.setInfoExtra(dto.getInfoExtra()); // Actualizar infoExtra también
                        // Forzar refresco de la tabla
                        masterData.set(masterData.indexOf(usuarioEnLista), usuarioEnLista);
                    } else {
                        System.err.println("Advertencia: Usuario modificado (DNI: " + dto.getDni() + ") no encontrado en masterData para refrescar UI.");
                        // Como fallback, recargar todos los datos podría ser una opción,
                        // pero puede ser confuso para el usuario.
                        // cargarDatosDeLogica();
                    }

                    // Construir mensaje de éxito
                    String mensajeExito = "Usuario modificado correctamente.";
                    if (passwordModificada) {
                        mensajeExito += " Contraseña actualizada.";
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", mensajeExito);
                } else {
                    // Si no se modificaron ni datos ni contraseña (ej. se hizo clic en Guardar sin cambiar nada)
                    showAlert(Alert.AlertType.INFORMATION, "Información", "No se detectaron cambios para guardar.");
                    // Podríamos retornar false aquí para indicar que no hubo "éxito" real,
                    // pero mantener true no rompe el flujo de volver al listado.
                    // return false;
                }
            } else {
                // --- LÓGICA DE ALTA ---
                // Validar DNI único (GestorUsuario ya lo hace, pero doble check no daña)
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

                // Crear objeto de Lógica SIN HASH (GestorUsuario lo hará)
                if ("Paciente".equals(rol)) {
                    // Asumiendo constructor Paciente(nombre, apellido, dni, genero, telefono, obraSocial)
                    nuevaPersonaLogica = new Paciente(dto.getNombre(), dto.getApellido(), dniInt, genChar, telInt, null, dto.getInfoExtra());
                    gestorUsuario.agregarPaciente((Paciente) nuevaPersonaLogica, password);
                } else if ("Médico".equals(rol)) {
                    // Asumiendo constructor Medico(nombre, apellido, dni, genero, telefono, matricula, especialidad)
                    nuevaPersonaLogica = new Medico(dto.getNombre(), dto.getApellido(), dniInt, genChar, telInt, null, dto.getInfoExtra(), "Especialidad_Default"); // Añadir especialidad default
                    gestorUsuario.agregarMedico((Medico) nuevaPersonaLogica, password);
                } else if ("Administrador".equals(rol)) {
                    // Asumiendo constructor Administrador(nombre, apellido, dni, genero, telefono, area)
                    nuevaPersonaLogica = new Administrador(dto.getNombre(), dto.getApellido(), dniInt, genChar, telInt, null, dto.getInfoExtra());
                    gestorUsuario.agregarAdministrador((Administrador) nuevaPersonaLogica, password);
                }

                // Verificar si se creó y añadir a la UI
                if (nuevaPersonaLogica != null) {
                    // Buscar el usuario recién creado para obtener el objeto completo (si es necesario)
                    // O simplemente añadir el DTO que ya tenemos
                    masterData.add(dto); // Añadir DTO a la lista observable
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "Usuario '" + dto.getNombre() + "' registrado correctamente.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "No se pudo crear el usuario (Rol no válido o error interno).");
                    return false; // Indicar fallo
                }
            }

            this.usuarioSeleccionado = null; // Deseleccionar después de guardar
            return true; // Indicar éxito

        } catch (Exception e) {
            // Captura general para errores inesperados durante la lógica de negocio
            System.err.println("Error al guardar usuario (DNI: " + dniInt + "): " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error Inesperado", "Ocurrió un error al guardar los datos del usuario.");
            return false; // Indicar fallo
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
            dniAEliminarInt = Integer.parseInt(dniAEliminarStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error Interno", "El DNI seleccionado '" + dniAEliminarStr + "' no es válido.");
            return;
        }


        Optional<ButtonType> result = showConfirmation("¿Seguro que desea eliminar al usuario con DNI " + dniAEliminarStr + "?");

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                // Llamar a la capa de Lógica
                gestorUsuario.eliminarUsuario(dniAEliminarInt); // GestorUsuario maneja la persistencia

                // Si no hubo excepción, actualizar la UI (eliminar de masterData)
                boolean removed = masterData.remove(usuarioSeleccionado);
                if(removed){
                    showAlert(Alert.AlertType.INFORMATION, "Eliminación", "Usuario eliminado correctamente.");
                    usuarioSeleccionado = null; // Deseleccionar
                } else {
                    // Esto no debería pasar si la lógica fue exitosa y el usuario estaba seleccionado
                    System.err.println("Advertencia: No se pudo remover el usuario de masterData después de eliminarlo en lógica.");
                    showAlert(Alert.AlertType.WARNING, "Advertencia", "El usuario fue eliminado, pero la lista no se actualizó correctamente.");
                    cargarDatosDeLogica(); // Recargar como fallback
                }


            } catch (Exception e) {
                // Capturar errores específicos de GestorUsuario si los hubiera, o errores generales
                System.err.println("Error al intentar eliminar usuario DNI " + dniAEliminarInt + ": " + e.getMessage());
                e.printStackTrace(); // Para debug
                showAlert(Alert.AlertType.ERROR, "Error de Eliminación", "Ocurrió un error al eliminar: " + e.getMessage());
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

        // Podrían añadirse más validaciones (ej. formato DNI/Teléfono numérico, longitud, etc.) aquí
        // pero las validaciones de formato principales se hacen al parsear en guardarUsuario.

        return true; // Pasa validaciones básicas
    }


    // --- Métodos de UI (Helpers) ---

    public void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        // Quitar header para un look más simple
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait(); // Espera a que el usuario cierre la alerta
    }

    public Optional<ButtonType> showConfirmation(String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // Tipo confirmación
        alert.setTitle("Confirmación");
        alert.setHeaderText(null); // Sin texto de cabecera
        alert.setContentText(content);
        // Opcional: Cambiar texto de botones si se desea (ej. "Sí", "No")
        // ButtonType buttonTypeYes = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        // ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        // alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        return alert.showAndWait(); // Devuelve el botón presionado
    }
}

