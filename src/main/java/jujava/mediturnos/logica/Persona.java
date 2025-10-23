package jujava.mediturnos.logica;

public class Usuario {
    int dni;
    String nombre;
    String apellido;
    char genero;
    int telefono;
    GestorUsuario gestor; // ahora se pasa al crear la instancia
    public Usuario() {}
    public Usuario(String nombre, String apellido, int dni, char genero, int telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.genero = genero;
        this.telefono = telefono;
    }

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
    public GestorUsuario getGestor() {
        return gestor;
    }
    public void setGestor(GestorUsuario gestor) {
        this.gestor = gestor;
    }
    // REGISTRAR PACIENTE
    public void registrarPaciente(Paciente p) {
        if (gestor != null && gestor.validarDNIUnico(String.valueOf(p.getDni()))) {
            gestor.pacientes.add(p);
            HelperCSV.guardarPacientes(gestor.getPacientes());
        }
    }
    // MODIFICAR PACIENTE
    public void modificarPaciente(int DNI, String nombre, String apellido, int telefono, String obraSocial) {
        if(gestor != null) {
            Paciente paciente = gestor.buscarPacientePorDNI(DNI);
            if (paciente != null) {
                paciente.setNombre(nombre);
                paciente.setApellido(apellido);
                paciente.setTelefono(telefono);
                paciente.setObraSocial(obraSocial);
                HelperCSV.guardarPacientes(gestor.getPacientes());
            }
        }
    }
}

