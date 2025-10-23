package jujava.mediturnos.logica;

import jujava.mediturnos.datos.AccesoDatos;
import jujava.mediturnos.logica.entidades.Persona;
import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.logica.entidades.Administrador;
import jujava.mediturnos.logica.entidades.Paciente;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase es el CORAZÓN de la capa de Lógica.
 * Es la única que interactúa con AccesoDatos y contiene TODA la lógica de negocio.
 */
public class GestorUsuario {
    public List<Paciente> pacientes;
    public List<Medico> medicos;
    public List<Administrador> administradores;

    public GestorUsuario() {
        // Esto ahora se ejecuta UNA SOLA VEZ, porque el bucle está roto.
        this.pacientes = AccesoDatos.cargarPacientes();
        this.medicos = AccesoDatos.cargarMedicos();
        this.administradores = AccesoDatos.cargarAdministradores();
    }

    public List<Paciente> getPacientes() { return pacientes; }
    public List<Medico> getMedicos() { return medicos; }
    public List<Administrador> getAdministradores() { return administradores; }

    public boolean validarDNIUnico(String dniStr) {
        if (dniStr == null || dniStr.trim().isEmpty()) {
            System.out.println("Error. El campo DNI está vacío.");
            return false;
        }
        int dni;
        try {
            dni = Integer.parseInt(dniStr.trim());
        } catch (NumberFormatException e) {
            System.out.println("Error. El DNI debe ser un número entero positivo.");
            return false;
        }
        for (Paciente p : pacientes) if (p.getDni() == dni) return false;
        for (Medico m : medicos) if (m.getDni() == dni) return false;
        for (Administrador a : administradores) if (a.getDni() == dni) return false;
        return true;
    }

    public Persona buscarUsuarioPorDNI(int dni) {
        for (Paciente p : pacientes) if (p.getDni() == dni) return p;
        for (Medico m : medicos) if (m.getDni() == dni) return m;
        for (Administrador a : administradores) if (a.getDni() == dni) return a;
        return null;
    }

    public Paciente buscarPacientePorDNI(int dni) {
        for (Paciente p : pacientes) if (p.getDni() == dni) return p;
        return null;
    }

    public Medico buscarMedicoPorDNI(int dni) {
        for (Medico m : medicos) if (m.getDni() == dni) return m;
        return null;
    }

    public void eliminarUsuario(int DNI){
        Persona usuario = buscarUsuarioPorDNI(DNI);
        if (usuario != null) {
            if (usuario instanceof Paciente) {
                pacientes.remove(usuario);
                AccesoDatos.guardarPacientes(pacientes);
            } else if (usuario instanceof Medico) {
                medicos.remove(usuario);
                AccesoDatos.guardarMedicos(medicos);
            } else if (usuario instanceof Administrador) {
                administradores.remove(usuario);
                AccesoDatos.guardarAdministradores(administradores);
            }
        }
    }

    // --- MÉTODOS DE ALTA (REGISTRO) ---
    public void agregarPaciente(Paciente p){
        if(validarDNIUnico(String.valueOf(p.getDni()))){
            pacientes.add(p);
            AccesoDatos.guardarPacientes(pacientes);
        } else System.out.println("DNI ya existente.");
    }

    public void agregarMedico(Medico m){
        if(validarDNIUnico(String.valueOf(m.getDni()))){
            medicos.add(m);
            AccesoDatos.guardarMedicos(medicos);
        } else System.out.println("DNI ya existente.");
    }

    public void agregarAdministrador(Administrador a){
        if(validarDNIUnico(String.valueOf(a.getDni()))){
            administradores.add(a);
            AccesoDatos.guardarAdministradores(administradores);
        } else System.out.println("DNI ya existente.");
    }

    // --- MÉTODOS DE MODIFICACIÓN (NUEVOS, movidos desde las entidades) ---

    /**
     * Lógica de negocio para modificar un Paciente.
     */
    public void modificarPaciente(int DNI, String nombre, String apellido, char genero, int telefono, String obraSocial) {
        Paciente paciente = buscarPacientePorDNI(DNI);
        if (paciente != null) {
            paciente.setNombre(nombre);
            paciente.setApellido(apellido);
            paciente.setGenero(genero);
            paciente.setTelefono(telefono);
            paciente.setObraSocial(obraSocial);
            AccesoDatos.guardarPacientes(pacientes);
        }
    }

    /**
     * Lógica de negocio para modificar un Médico.
     */
    public void modificarMedico(int DNI, String nombre, String apellido, char genero, int telefono, String matricula, String especialidad){
        Medico medico = buscarMedicoPorDNI(DNI);
        if (medico != null) {
            medico.setNombre(nombre);
            medico.setApellido(apellido);
            medico.setGenero(genero);
            medico.setTelefono(telefono);
            medico.setMatricula(matricula);
            medico.setEspecialidad(especialidad); // Considerar añadir especialidad al formulario
            AccesoDatos.guardarMedicos(medicos);
        }
    }

    /**
     * Lógica de negocio para modificar un Administrador.
     */
    public void modificarAdministrador(int DNI, String nombre, String apellido, char genero, int telefono, String area){
        Persona usuario = buscarUsuarioPorDNI(DNI);
        if(usuario instanceof Administrador administrador){
            administrador.setNombre(nombre);
            administrador.setApellido(apellido);
            administrador.setGenero(genero);
            administrador.setTelefono(telefono);
            administrador.setArea(area);
            AccesoDatos.guardarAdministradores(administradores);
        }
    }
}

