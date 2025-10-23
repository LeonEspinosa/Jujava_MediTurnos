package jujava.mediturnos.logica.entidades;

import jujava.mediturnos.datos.AccesoDatos;

// ERROR 42: Asegúrate que extienda de Persona
public class Medico extends Persona {
    String matricula;
    String especialidad;
    public Medico() {}
    public Medico(String nombre, String apellido, int dni, char genero, int telefono, String matricula, String especialidad) {
        super(nombre, apellido, dni, genero, telefono);
        this.matricula = matricula;
        this.especialidad = especialidad;
    }
    public String getMatricula() {
        return matricula;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    public String getEspecialidad() {
        return especialidad;
    }
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    @Override
    public String toString() {
        return "Medico [nombre=" + getNombre() + ", apellido=" + getApellido() +
                ", dni=" + getDni() + ", genero=" + getGenero() +
                ", telefono=" + getTelefono() + ", matricula=" + matricula +
                ", especialidad=" + especialidad + "]";
    }

    // ERROR 43: Se elimina toda la lógica de negocio de la entidad.
}

