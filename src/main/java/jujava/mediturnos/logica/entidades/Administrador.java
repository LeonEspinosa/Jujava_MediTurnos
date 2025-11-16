package jujava.mediturnos.logica.entidades;

import jujava.mediturnos.datos.AccesoDatos;
import jujava.mediturnos.logica.entidades.Persona;


public class Administrador extends Persona{

    String area;

    public Administrador() {}

    public Administrador(String nombre, String apellido, int dni, char genero, int telefono,String passwordHash, String area ) {
        super(nombre, apellido, dni, genero, telefono, passwordHash);
        this.area = area;}
    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }
    @Override
    public String toString() {
        return "administrador " +
                "nombre:" + getNombre() +
                ", apellido:" + getApellido()+
                ", dni:" + getDni() +
                ", genero:" + getGenero() +
                ", telefono:" + getTelefono() +
                ", area:" + getArea();
    }

}

