package jujava.mediturnos.logica;

import jujava.mediturnos.datos.AccesoDatos;
import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.logica.entidades.Turno;
import jujava.mediturnos.logica.entidades.Paciente;
import jujava.mediturnos.logica.GestorUsuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GestorTurnos {
    private List<Turno> turnos;
    private final GestorUsuario gestorUsuario;

    public GestorTurnos(GestorUsuario gestorUsuario) {
        this.gestorUsuario = gestorUsuario;
        this.turnos = AccesoDatos.cargarTurnos();
    }

    public List<Turno> getTurnos() {
        return turnos;
    }

    private int generarNuevoIdTurno() {
        return turnos.stream()
                .mapToInt(Turno::getIdTurno)
                .max()
                .orElse(0) + 1;
    }

    // --- MÉTODOS REQUERIDOS ---

    public boolean solicitarTurno(int dniPaciente, int dniMedico, String especialidad, LocalDateTime fechaHora) {
        if (fechaHora == null || fechaHora.isBefore(LocalDateTime.now())) {
            System.err.println("Error: La fecha/hora es inválida.");
            return false;
        }
        if (gestorUsuario.buscarMedicoPorDNI(dniMedico) == null) {
            System.err.println("Error: Médico con DNI " + dniMedico + " no encontrado.");
            return false;
        }

        int newId = generarNuevoIdTurno();
        Turno nuevoTurno = new Turno(newId, dniPaciente, dniMedico, especialidad, fechaHora, "Pendiente");
        this.turnos.add(nuevoTurno);
        AccesoDatos.guardarTurnos(this.turnos);
        return true;
    }

    public List<Turno> getTurnosPorMedico(int dniMedico) {
        return turnos.stream()
                .filter(t -> t.getDniMedico() == dniMedico)
                .collect(Collectors.toList());
    }

    public boolean cancelarTurno(int idTurno) {
        Turno turno = turnos.stream()
                .filter(t -> t.getIdTurno() == idTurno)
                .findFirst()
                .orElse(null);

        if (turno != null && !"Cancelado".equals(turno.getEstado())) {
            turno.setEstado("Cancelado");
            AccesoDatos.guardarTurnos(this.turnos);
            return true;
        }
        return false;
    }

    public boolean completarTurno(int idTurno) { //completa el turno marcandolo como realizado
        Turno turno = turnos.stream()
                .filter(t -> t.getIdTurno() == idTurno)
                .findFirst()
                .orElse(null);

        if (turno != null && !"Cancelado".equals(turno.getEstado()) && !"Realizado".equals(turno.getEstado())) {
            turno.setEstado("Realizado");
            AccesoDatos.guardarTurnos(this.turnos);
            return true;
        }
        return false;
    }
    //modificamos los campos principales de un truno (usado x admin)
    public boolean modificarTurno(int idTurno, int dniMedico, String especialidad, LocalDateTime fechaHora, String estado) {
        Turno turno = turnos.stream()
                .filter(t -> t.getIdTurno() == idTurno)
                .findFirst()
                .orElse(null);

        // El turno debe existir y no estar en estado final (Cancelado/Realizado)
        if (turno == null || "Realizado".equals(turno.getEstado())) {
            return false;
        }

        // La nueva fecha no debe ser pasada, a menos que se quiera marcar como Realizado
        if (fechaHora.isBefore(LocalDateTime.now()) && !"Realizado".equals(estado)) {
            System.err.println("Error: No se puede asignar un turno en el pasado (excepto si se marca como Realizado).");
            return false;
        }

        // Actualizar datos
        turno.setDniMedico(dniMedico);
        turno.setEspecialidad(especialidad);
        turno.setFechaHora(fechaHora);
        turno.setEstado(estado);

        AccesoDatos.guardarTurnos(this.turnos);
        return true;
    }

    public List<Turno> getTodosLosTurnos() {
        return new ArrayList<>(turnos);
    }

    // --- MÉTODOS HELPER (para la UI del Paciente) ---
    public List<String> getEspecialidadesDisponibles() {
        return gestorUsuario.getMedicos().stream()
                .map(Medico::getEspecialidad)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Medico> getMedicosPorEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return gestorUsuario.getMedicos().stream()
                .filter(m -> especialidad.equals(m.getEspecialidad()))
                .collect(Collectors.toList());
    }

    //BUSCAR TURNO
    public Turno buscarTurno(int idTurno) {
        for (Turno t : turnos) {
            if (t.getIdTurno() == idTurno) return t;}
        return null;}

    //VALIDACION TURNO
    public boolean validacionTurnoHorario(int dniMedico, LocalDateTime fechaHora) {
        for (Turno t : turnos) {
            if (t.getDniMedico() == dniMedico &&
                    t.getFechaHora().equals(fechaHora) &&
                    t.getEstado().equalsIgnoreCase("pendiente")) {
                return false;}}//horario no disponible
        return true;} // horario disponible


    //MODIFICAR TURNO
    public void modificarTurno(Turno turnoModificado) {
        Turno turnoOriginal = buscarTurno(turnoModificado.getIdTurno());
        if (turnoOriginal == null) {
            System.out.println("turno no encontrado");
            return;}

        //valida que el paciente exista
        GestorUsuario gestor= new GestorUsuario();
        Paciente paciente = gestor.buscarPacientePorDNI(turnoModificado.getDniPaciente());
        if (paciente == null) {
            System.out.println("paciente no existe");
            return;}

        // valida que el medico este disponible
        if (!validacionTurnoHorario(turnoModificado.getDniMedico(), turnoModificado.getFechaHora())) {
            System.out.println("el medico ya tiene un turno en ese horario");
            return;}

        // aplica las modificaciones
        turnoOriginal.setDniPaciente(turnoModificado.getDniPaciente());
        turnoOriginal.setDniMedico(turnoModificado.getDniMedico());
        turnoOriginal.setEspecialidad(turnoModificado.getEspecialidad());
        turnoOriginal.setFechaHora(turnoModificado.getFechaHora());
        turnoOriginal.setEstado(turnoModificado.getEstado());

        AccesoDatos.guardarTurnos(turnos);
        System.out.println("turno modificado");}


}