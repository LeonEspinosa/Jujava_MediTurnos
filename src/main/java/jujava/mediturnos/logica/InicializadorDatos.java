package jujava.mediturnos.logica;

import jujava.mediturnos.datos.AccesoDatos;
import jujava.mediturnos.logica.entidades.Administrador;
import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.logica.entidades.Paciente;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;


public class InicializadorDatos {

    // Contraseña por defecto para todos los usuarios de prueba
    private static final String DEFAULT_PASSWORD = "1234";

    /**
     * Verifica si las listas de usuarios (Pacientes, Médicos, Admin) están vacías
     * y, de ser así, crea los usuarios por defecto para pruebas.
     */
    public static void verificarYPoblarUsuarios(List<Paciente> pacientes, List<Medico> medicos, List<Administrador> administradores) {

        String hashPorDefecto = BCrypt.hashpw(DEFAULT_PASSWORD, BCrypt.gensalt());

        // 1. Crear Administrador por defecto (DNI 0)
        if (administradores.isEmpty()) {
            System.out.println("No se encontraron Administradores. Creando Admin por defecto (DNI: 0)...");
            Administrador admin = new Administrador(
                    "Admin", "Default", 0, 'M', 111111, hashPorDefecto, "Sistemas"
            );
            administradores.add(admin);
            AccesoDatos.guardarAdministradores(administradores);
        }

        // 2. Crear Médico por defecto (DNI 1)
        if (medicos.isEmpty()) {
            System.out.println("No se encontraron Médicos. Creando Médico por defecto (DNI: 1)...");
            Medico medico = new Medico(
                    "Medico", "Default", 1, 'F', 222222, hashPorDefecto, "M-1234", "Cardiología"
            );
            Medico medico2 = new Medico(
                    "Juan", "Vanegas", 48456123, 'F', 222222, hashPorDefecto, "M-1234", "Cardiología"
            );
            medicos.add(medico);
            medicos.add(medico2);
            AccesoDatos.guardarMedicos(medicos);
        }

        // 3. Crear Paciente por defecto (DNI 2)
        if (pacientes.isEmpty()) {
            System.out.println("No se encontraron Pacientes. Creando Paciente por defecto (DNI: 2)...");
            Paciente paciente = new Paciente(
                    "Paciente", "Default", 2, 'M', 333333, hashPorDefecto, "OSDE"
            );
            Paciente paciente2 = new Paciente(
                    "Mateo", "Alvarado", 45123789, 'M', 333333, hashPorDefecto, "OSDE"
            );
            pacientes.add(paciente);
            pacientes.add(paciente2);
            AccesoDatos.guardarPacientes(pacientes);
        }
    }

    /**
     * Verifica si la lista de especialidades está vacía y, de ser así,
     * crea especialidades por defecto.
     * @param especialidades La lista de especialidades cargada desde AccesoDatos.
     */
    public static void verificarYPoblarEspecialidades(List<String> especialidades) {
        if (especialidades.isEmpty()) {
            System.out.println("No se encontraron Especialidades. Creando lista por defecto...");
            especialidades.add("Cardiología");
            especialidades.add("Dermatología");
            especialidades.add("Pediatría");
            especialidades.add("Clínica Médica");
            especialidades.add("Traumatología");
            especialidades.add("Ginecología");

            AccesoDatos.guardarEspecialidades(especialidades);
        }
    }
}