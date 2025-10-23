package jujava.mediturnos.logica.entidades;

import jujava.mediturnos.datos.AccesoDatos;
import jujava.mediturnos.logica.entidades.Persona;

// ERROR 44: Asegúrate que extienda de Persona
public class Administrador extends Persona{

    String area;

    // ERROR 45 (CRÍTICO): Se elimina la instancia de GestorUsuario que causaba el bucle.
    // private GestorUsuario gestor=new GestorUsuario();

    public Administrador() {}

    public Administrador(String nombre, String apellido, int dni, char genero, int telefono,String area ) {
        super(nombre, apellido, dni, genero, telefono);
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

    // ERROR 46: Se elimina toda la lógica de negocio de la entidad.
}

