package jujava.mediturnos.logica;

import jujava.mediturnos.datos.AccesoDatos;
import jujava.mediturnos.logica.entidades.Administrador;
import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.logica.entidades.Paciente;
import jujava.mediturnos.logica.entidades.Persona;

import java.util.List;
public class GestorUsuario {
    public List<Paciente> pacientes;
    public List<Medico> medicos;
    public List<Administrador> administradores;
    public GestorUsuario() {
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
        Persona persona = buscarUsuarioPorDNI(DNI);
        if (persona != null) {
            if (persona instanceof Paciente) {
                pacientes.remove(persona);
                AccesoDatos.guardarPacientes(pacientes);
            } else if (persona instanceof Medico) {
                medicos.remove(persona);
                AccesoDatos.guardarMedicos(medicos);
            } else if (persona instanceof Administrador) {
                administradores.remove(persona);
                AccesoDatos.guardarAdministradores(administradores);
            }
        }
    }
    // Métodos agregados para Main
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
}

