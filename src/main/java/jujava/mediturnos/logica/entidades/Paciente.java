package jujava.mediturnos.logica.entidades;

// NOTA: Se corrigió una '}' extra mal ubicada en el constructor.
public class Paciente extends Persona { // Extiende de Usuario, no de Persona
    private String obraSocial;

    public Paciente( String nombre, String apellido, int dni, char genero, int telefono,String obraSocial ) {
        super(nombre, apellido, dni, genero, telefono);
        this.obraSocial = obraSocial;
    } // La llave estaba aquí incorrectamente

    public String getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(String obraSocial) {
        this.obraSocial = obraSocial;
    }
}
