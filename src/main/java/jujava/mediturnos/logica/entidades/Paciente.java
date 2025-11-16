package jujava.mediturnos.logica.entidades;

public class Paciente extends Persona {
    private String obraSocial;

    public Paciente( String nombre, String apellido, int dni, char genero, int telefono,String passwordHash, String obraSocial) {
        super(nombre, apellido, dni, genero, telefono, passwordHash);
        this.obraSocial = obraSocial;
    }

    public String getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(String obraSocial) {
        this.obraSocial = obraSocial;
    }
}
