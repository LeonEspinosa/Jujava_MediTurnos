package jujava.mediturnos.logica.entidades;
import jujava.mediturnos.datos.AccesoDatos;

public class Persona {
    int dni;
    String nombre;
    String apellido;
    char genero;
    int telefono;
    String passwordHash;

    public Persona() {}
    public Persona(String nombre, String apellido, int dni, char genero, int telefono, String passwordHash) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.genero = genero;
        this.telefono = telefono;
        this.passwordHash= passwordHash;
    }

    // --- Getters y Setters ---
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
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }


}

