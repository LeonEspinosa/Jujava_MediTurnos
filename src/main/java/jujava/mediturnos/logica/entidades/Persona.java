package jujava.mediturnos.logica.entidades;

import jujava.mediturnos.datos.AccesoDatos;

public class Persona {
    int dni;
    String nombre;
    String apellido;
    char genero;
    int telefono;

    public Persona() {}
    public Persona(String nombre, String apellido, int dni, char genero, int telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.genero = genero;
        this.telefono = telefono;
    }

    // --- Getters y Setters (Correctos) ---
    public int getDni() {
        return dni;
    }
    public void setDni(int dni) {
        this.dni = dni;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public char getGenero() {
        return genero;
    }
    public void setGenero(char genero) {
        this.genero = genero;
    }
    public int getTelefono() {
        return telefono;
    }
    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    // ERROR 41: Toda la lógica de negocio (como registrar/modificar)
    // se elimina de la entidad. Esto ahora vivirá en GestorUsuario.
}

