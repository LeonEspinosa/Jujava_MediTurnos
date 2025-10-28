package jujava.mediturnos.logica;

import jujava.mediturnos.datos.AccesoDatos;
import jujava.mediturnos.logica.entidades.Persona;
import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.logica.entidades.Administrador;
import jujava.mediturnos.logica.entidades.Paciente;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;


public class GestorUsuario {
    public List<Paciente> pacientes;
    public List<Medico> medicos;
    public List<Administrador> administradores;
    private static final int DEFAULT_ADMIN_DNI = 0;
    //public static final String DEFAULT_ADMIN_PASS = "admin";

    public GestorUsuario() {
        this.pacientes = AccesoDatos.cargarPacientes();
        this.medicos = AccesoDatos.cargarMedicos();
        this.administradores = AccesoDatos.cargarAdministradores();
        crearAdminPorDefectoSiNoExiste();
    }

    private void crearAdminPorDefectoSiNoExiste() {
        Persona adminExistente = buscarUsuarioPorDNI(DEFAULT_ADMIN_DNI);


        if (adminExistente == null || !(adminExistente instanceof Administrador)) {
            System.out.println("Administrador por defecto (DNI: " + DEFAULT_ADMIN_DNI + ") no encontrado. Creando...");
            //Datos de Ejemplo
            String nombre = "Admin";
            String apellido = "Default";
            char genero = 'M';
            int telefono = 12345678;
            String area = "Sistemas";
            String contraseña = "1234";

            String passwordHash = BCrypt.hashpw(contraseña, BCrypt.gensalt());
            Administrador adminPorDefecto = new Administrador(nombre, apellido, DEFAULT_ADMIN_DNI, genero, telefono, passwordHash, area);
            this.administradores.add(adminPorDefecto);
            AccesoDatos.guardarAdministradores(this.administradores);

            System.out.println("Administrador por defecto creado y guardado.");
        } else {
            System.out.println("Administrador por defecto (DNI: " + DEFAULT_ADMIN_DNI + ") ya existe.");
        }
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

    public void eliminarUsuario(int DNI) {
        Persona usuario = buscarUsuarioPorDNI(DNI);
        if (usuario != null) {
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
        } else System.out.println("DNI ya existente.");
    }



    public void agregarMedico(Medico m, String password){
        if(validarDNIUnico(String.valueOf(m.getDni()))){
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            m.setPasswordHash(hash);
            medicos.add(m);
            AccesoDatos.guardarMedicos(medicos);
        } else {
            System.out.println("DNI ya existente.");
        }
    }



    public void agregarAdministrador(Administrador a, String password){
        if(validarDNIUnico(String.valueOf(a.getDni()))){
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            a.setPasswordHash(hash);
            administradores.add(a);
            AccesoDatos.guardarAdministradores(administradores);
        } else {
            System.out.println("DNI ya existente.");
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
        if (BCrypt.checkpw(passwordIngresada, hashGuardado)) {
            return usuario;
        } else {
            return null;
        }

    }

    public boolean modificarPasswordUsuario(int DNI, String nuevaPassword) {
        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            System.err.println("Error modificar contraseña: La nueva contraseña no puede estar vacía.");
            return false;
        }
        if (DNI == DEFAULT_ADMIN_DNI && nuevaPassword.length() < 4) { // Ejemplo: Requerir longitud mínima para admin
            System.err.println("Error modificar contraseña: La contraseña del admin por defecto debe tener al menos 4 caracteres.");
            return false;
        }

        Persona usuario = buscarUsuarioPorDNI(DNI);
        if (usuario == null) {
            System.err.println("Error modificar contraseña: Usuario con DNI " + DNI + " no encontrado.");
            return false;
        }

        try {
            // Generar el nuevo hash
            String nuevoHash = BCrypt.hashpw(nuevaPassword.trim(), BCrypt.gensalt());
            // Actualizar el hash en el objeto Persona
            usuario.setPasswordHash(nuevoHash);

            // Guardar en el archivo CSV
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
                System.out.println("Contraseña actualizada para DNI: " + DNI);
                return true;
            } else {
                System.err.println("Error modificar contraseña: No se pudo determinar el tipo de usuario para guardar DNI: " + DNI);
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error al hashear o guardar la nueva contraseña para DNI " + DNI + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

