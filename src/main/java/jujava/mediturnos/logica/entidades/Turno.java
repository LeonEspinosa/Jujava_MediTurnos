package jujava.mediturnos.logica.entidades;

import java.time.LocalDateTime;

public class Turno {
    private int idTurno;
    private int dniPaciente;
    private int dniMedico;
    private String especialidad;
    private LocalDateTime fechaHora;
    private String estado; // Pendiente, Confirmado, Cancelado, Realizado

    public Turno() {
    }

    public Turno(int idTurno, int dniPaciente, int dniMedico, String especialidad, LocalDateTime fechaHora, String estado) {
        this.idTurno = idTurno;
        this.dniPaciente = dniPaciente;
        this.dniMedico = dniMedico;
        this.especialidad = especialidad;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    // --- Getters y Setters ---
    public int getIdTurno() {
        return idTurno;
    }
    // ... otros getters y setters

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public int getDniPaciente() {
        return dniPaciente;
    }

    public void setDniPaciente(int dniPaciente) {
        this.dniPaciente = dniPaciente;
    }

    public int getDniMedico() {
        return dniMedico;
    }

    public void setDniMedico(int dniMedico) {
        this.dniMedico = dniMedico;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}