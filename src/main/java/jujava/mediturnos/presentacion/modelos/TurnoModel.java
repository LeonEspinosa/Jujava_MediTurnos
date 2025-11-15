package jujava.mediturnos.presentacion.modelos;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TurnoModel {

    private final SimpleIntegerProperty idTurno;
    private final StringProperty dniPaciente;
    private final StringProperty nombrePaciente;
    private final StringProperty dniMedico;
    private final StringProperty nombreMedico;
    private final StringProperty especialidad;
    private final StringProperty fechaHora;
    private final StringProperty estado;

    public TurnoModel(int idTurno, String dniPaciente, String nombrePaciente, String dniMedico, String nombreMedico, String especialidad, String fechaHora, String estado) {
        this.idTurno = new SimpleIntegerProperty(idTurno);
        this.dniPaciente = new SimpleStringProperty(dniPaciente);
        this.nombrePaciente = new SimpleStringProperty(nombrePaciente);
        this.dniMedico = new SimpleStringProperty(dniMedico);
        this.nombreMedico = new SimpleStringProperty(nombreMedico);
        this.especialidad = new SimpleStringProperty(especialidad);
        this.fechaHora = new SimpleStringProperty(fechaHora);
        this.estado = new SimpleStringProperty(estado);
    }

    // Propiedades para TableView
    public SimpleIntegerProperty idTurnoProperty() { return idTurno; }
    public StringProperty dniPacienteProperty() { return dniPaciente; }
    public StringProperty nombrePacienteProperty() { return nombrePaciente; }
    public StringProperty dniMedicoProperty() { return dniMedico; }
    public StringProperty nombreMedicoProperty() { return nombreMedico; }
    public StringProperty especialidadProperty() { return especialidad; }
    public StringProperty fechaHoraProperty() { return fechaHora; }
    public StringProperty estadoProperty() { return estado; }

    // Getters para lógica
    public int getIdTurno() { return idTurno.get(); }
    public String getDniPaciente() { return dniPaciente.get(); }
    public String getDniMedico() { return dniMedico.get(); }
    public String getEspecialidad() { return especialidad.get(); }
    public String getFechaHora() { return fechaHora.get(); }
    public String getEstado() { return estado.get(); }
}