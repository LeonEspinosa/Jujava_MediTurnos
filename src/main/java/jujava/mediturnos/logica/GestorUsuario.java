package jujava.mediturnos.logica;

import jujava.mediturnos.datos.AccesoDatos;
import jujava.mediturnos.logica.entidades.Persona;
import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.logica.entidades.Administrador;
import jujava.mediturnos.logica.entidades.Paciente;
import jujava.mediturnos.logica.entidades.Turno;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;


public class GestorUsuario {
    public List<Paciente> pacientes;
    public List<Medico> medicos;
    public List<Administrador> administradores;

    public GestorUsuario() {
        this.pacientes = AccesoDatos.cargarPacientes();
        this.medicos = AccesoDatos.cargarMedicos();
        this.administradores = AccesoDatos.cargarAdministradores();
        InicializadorDatos.verificarYPoblarUsuarios(this.pacientes, this.medicos, this.administradores);
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    public List<Administrador> getAdministradores() {
        return administradores;
    }

    public boolean validarDNIUnico(String dniStr) {
        if (dniStr == null || dniStr.trim().isEmpty()) {
            return false;
        }
        int dni;
        try {
            dni = Integer.parseInt(dniStr.trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        if (dni == 0 || dni == 1 || dni == 2) {
            Persona usuarioExistente = buscarUsuarioPorDNI(dni);
            // Solo es un problema si el usuario ya existe (que debería)
            if (usuarioExistente != null) {
                return false;
            }
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

    public void eliminarUsuario(int DNI) {
        Persona usuario = buscarUsuarioPorDNI(DNI);
        if (usuario != null) {
            if (DNI == 0 || DNI == 1 || DNI == 2) {
                return;
            }
            if (usuario instanceof Paciente) {
                pacientes.remove(usuario);
                AccesoDatos.guardarPacientes(pacientes);

            } else if (usuario instanceof Medico) {
                medicos.remove(usuario);
                AccesoDatos.guardarMedicos(medicos);

            } else if (usuario instanceof Administrador) {
                // (Excluyendo DNI 0, que se maneja en MainController)
                administradores.remove(usuario);
                AccesoDatos.guardarAdministradores(administradores);
            }
        }
    }

    // --- MÉTODOS DE ALTA (REGISTRO) ---

    public void agregarPaciente(Paciente p, String passwordIngresada) { //string password q agregue
        if (validarDNIUnico(String.valueOf(p.getDni()))) {
            String passwordPlana = passwordIngresada; //linea q agregue
            String hashGuardado = BCrypt.hashpw(passwordPlana, BCrypt.gensalt());//linea q agregue
            p.setPasswordHash(hashGuardado); //linea q agregue CONSULTARR
            pacientes.add(p);
            AccesoDatos.guardarPacientes(pacientes);
        }
    }



    public void agregarMedico(Medico m, String password){
        if(validarDNIUnico(String.valueOf(m.getDni()))){
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            m.setPasswordHash(hash);
            medicos.add(m);
            AccesoDatos.guardarMedicos(medicos);
        }
    }



    public void agregarAdministrador(Administrador a, String password){
        if(validarDNIUnico(String.valueOf(a.getDni()))){
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            a.setPasswordHash(hash);
            administradores.add(a);
            AccesoDatos.guardarAdministradores(administradores);
        }
    }


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

    public void modificarMedico(int DNI, String nombre, String apellido, char genero, int telefono, String matricula, String especialidad) {
        Medico medico = buscarMedicoPorDNI(DNI);
        if (medico != null) {
            medico.setNombre(nombre);
            medico.setApellido(apellido);
            medico.setGenero(genero);
            medico.setTelefono(telefono);
            medico.setMatricula(matricula);
            medico.setEspecialidad(especialidad);
            AccesoDatos.guardarMedicos(medicos);
        }
    }


    public void modificarAdministrador(int DNI, String nombre, String apellido, char genero, int telefono, String area) {
        Persona usuario = buscarUsuarioPorDNI(DNI);
        if (usuario instanceof Administrador administrador) {
            administrador.setNombre(nombre);
            administrador.setApellido(apellido);
            administrador.setGenero(genero);
            administrador.setTelefono(telefono);
            administrador.setArea(area);
            AccesoDatos.guardarAdministradores(administradores);
        }
    }

    public Persona autenticarUsuario(int dni, String passwordIngresada) {
        Persona usuario = this.buscarUsuarioPorDNI(dni);
        if (usuario == null) {
            return null;
        }
        String hashGuardado = usuario.getPasswordHash();
        if (hashGuardado == null || hashGuardado.isEmpty()) {
            return null;
        }
        if (BCrypt.checkpw(passwordIngresada, hashGuardado)) {
            return usuario;
        } else {
            return null;
        }

    }

    public boolean modificarPasswordUsuario(int DNI, String nuevaPassword) {
        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            return false;
        }
        if (DNI == 0 && nuevaPassword.length() < 4) {
            return false;
        }

        Persona usuario = buscarUsuarioPorDNI(DNI);
        if (usuario == null) {
            return false;
        }

        try {
            // Generar el nuevo hash
            String nuevoHash = BCrypt.hashpw(nuevaPassword.trim(), BCrypt.gensalt());
            usuario.setPasswordHash(nuevoHash);

            // Guardar
            boolean guardado = false;
            if (usuario instanceof Paciente) {
                AccesoDatos.guardarPacientes(pacientes);
                guardado = true;
            } else if (usuario instanceof Medico) {
                AccesoDatos.guardarMedicos(medicos);
                guardado = true;
            } else if (usuario instanceof Administrador) {
                AccesoDatos.guardarAdministradores(administradores);
                guardado = true;
            }

            if(guardado) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

