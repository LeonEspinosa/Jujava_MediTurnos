package jujava.mediturnos.logica;

public class Paciente extends Persona {
    private String obraSocial;

    public Paciente( String nombre, String apellido, int dni, char genero, int telefono,String obraSocial ) {
        super(nombre, apellido, dni, genero, telefono);
        this.obraSocial = obraSocial;

    public String getObraSocial() {
        return obraSocial;}

    }
