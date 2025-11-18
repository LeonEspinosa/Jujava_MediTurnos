package jujava.mediturnos.logica;

import jujava.mediturnos.datos.AccesoDatos;
import jujava.mediturnos.logica.entidades.Administrador;
import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.logica.entidades.Paciente;
import org.mindrot.jbcrypt.BCrypt;
import jujava.mediturnos.logica.entidades.Turno;

import java.time.LocalDateTime;
import java.time.Month; // <-- AÑADIDO
import java.util.List;
import java.util.Random; // <-- AÑADIDO


public class InicializadorDatos {

    // Contraseña por defecto para todos los usuarios de prueba
    private static final String DEFAULT_PASSWORD = "1234";

    /**
     * POBLA 10 MÉDICOS Y 20 PACIENTES
     */
    public static void verificarYPoblarUsuarios(List<Paciente> pacientes, List<Medico> medicos, List<Administrador> administradores) {

        String hashPorDefecto = BCrypt.hashpw(DEFAULT_PASSWORD, BCrypt.gensalt());

        // --- 1. ADMINISTRADOR POR DEFECTO ---
        if (administradores.isEmpty()) {
            Administrador admin = new Administrador(
                    "Admin", "Default", 0, 'M', 111111, hashPorDefecto, "Sistemas"
            );
            administradores.add(admin);
            AccesoDatos.guardarAdministradores(administradores);
        }

        // --- 2. MÉDICOS (10 en total) ---
        if (medicos.isEmpty()) {
            // Usuarios por defecto (2)
            medicos.add(new Medico("Medico", "Default", 1, 'F', 222222, hashPorDefecto, "M-1234", "Cardiología"));
            medicos.add(new Medico("Juan", "Vanegas", 48456123, 'M', 222223, hashPorDefecto, "M-1235", "Cardiología"));

            // Médicos adicionales (8)
            medicos.add(new Medico("Laura", "Gomez", 10000003, 'F', 333001, hashPorDefecto, "M-2001", "Dermatología"));
            medicos.add(new Medico("Carlos", "Fernandez", 10000004, 'M', 333002, hashPorDefecto, "M-2002", "Pediatría"));
            medicos.add(new Medico("Ana", "Martinez", 10000005, 'F', 333003, hashPorDefecto, "M-2003", "Clínica Médica"));
            medicos.add(new Medico("Pedro", "Rodriguez", 10000006, 'M', 333004, hashPorDefecto, "M-2004", "Traumatología"));
            medicos.add(new Medico("Sofia", "Diaz", 10000007, 'F', 333005, hashPorDefecto, "M-2005", "Ginecología"));
            medicos.add(new Medico("Miguel", "Sanchez", 10000008, 'M', 333006, hashPorDefecto, "M-2006", "Cardiología"));
            medicos.add(new Medico("Lucia", "Perez", 10000009, 'F', 333007, hashPorDefecto, "M-2007", "Dermatología"));
            medicos.add(new Medico("David", "Lopez", 10000010, 'M', 333008, hashPorDefecto, "M-2008", "Pediatría"));

            AccesoDatos.guardarMedicos(medicos);
        }

        // --- 3. PACIENTES (20 en total) ---
        if (pacientes.isEmpty()) {
            // Usuarios por defecto (2)
            pacientes.add(new Paciente("Paciente", "Default", 2, 'M', 333333, hashPorDefecto, "OSDE"));
            pacientes.add(new Paciente("Mateo", "Alvarado", 45123789, 'M', 333334, hashPorDefecto, "OSDE"));

            // Pacientes adicionales (18)
            pacientes.add(new Paciente("Elena", "Blanco", 20000003, 'F', 444001, hashPorDefecto, "Swiss Medical"));
            pacientes.add(new Paciente("Javier", "Moreno", 20000004, 'M', 444002, hashPorDefecto, "PAMI"));
            pacientes.add(new Paciente("Maria", "Jimenez", 20000005, 'F', 444003, hashPorDefecto, "Particular"));
            pacientes.add(new Paciente("Daniel", "Ruiz", 20000006, 'M', 444004, hashPorDefecto, "OSDE"));
            pacientes.add(new Paciente("Carla", "Silva", 20000007, 'F', 444005, hashPorDefecto, "Swiss Medical"));
            pacientes.add(new Paciente("Jorge", "Torres", 20000008, 'M', 444006, hashPorDefecto, "PAMI"));
            pacientes.add(new Paciente("Rocio", "Nuñez", 20000009, 'F', 444007, hashPorDefecto, "Particular"));
            pacientes.add(new Paciente("Adrian", "Romero", 20000010, 'M', 444008, hashPorDefecto, "OSDE"));
            pacientes.add(new Paciente("Valeria", "Flores", 20000011, 'F', 444009, hashPorDefecto, "Swiss Medical"));
            pacientes.add(new Paciente("Ivan", "Acosta", 20000012, 'M', 444010, hashPorDefecto, "PAMI"));
            pacientes.add(new Paciente("Camila", "Vazquez", 20000013, 'F', 444011, hashPorDefecto, "Particular"));
            pacientes.add(new Paciente("Marcos", "Benitez", 20000014, 'M', 444012, hashPorDefecto, "OSDE"));
            pacientes.add(new Paciente("Julieta", "Sosa", 20000015, 'F', 444013, hashPorDefecto, "Swiss Medical"));
            pacientes.add(new Paciente("Diego", "Ramirez", 20000016, 'M', 444014, hashPorDefecto, "PAMI"));
            pacientes.add(new Paciente("Paula", "Gimenez", 20000017, 'F', 444015, hashPorDefecto, "Particular"));
            pacientes.add(new Paciente("Lucas", "Castro", 20000018, 'M', 444016, hashPorDefecto, "OSDE"));
            pacientes.add(new Paciente("Agustina", "Rios", 20000019, 'F', 444017, hashPorDefecto, "Swiss Medical"));
            pacientes.add(new Paciente("Nicolas", "Molina", 20000020, 'M', 444018, hashPorDefecto, "PAMI"));

            AccesoDatos.guardarPacientes(pacientes);
        }
    }


    public static void verificarYPoblarEspecialidades(List<String> especialidades) {
        if (especialidades.isEmpty()) {
            especialidades.add("Cardiología");
            especialidades.add("Dermatología");
            especialidades.add("Pediatría");
            especialidades.add("Clínica Médica");
            especialidades.add("Traumatología");
            especialidades.add("Ginecología");

            AccesoDatos.guardarEspecialidades(especialidades);
        }
    }

    /**
     * POBLA 67 TURNOS DE PRUEBA (NOV-DIC) RESPETANDO LA LÓGICA DE NEGOCIO
     * Asume que "hoy" es 20 de Noviembre de 2025, 10:00 AM
     */
    public static void verificarYPoblarTurnos(List<Turno> turnos) {

        if (!turnos.isEmpty()) {
            return; // Si ya hay turnos, no hacer nada
        }

        // --- 1. DEFINIR DATOS DE PRUEBA ---

        // Asumimos que "hoy" es 20 de Noviembre de 2025, 10:00 AM
        LocalDateTime hoy = LocalDateTime.of(2025, Month.NOVEMBER, 20, 10, 0);

        // Listas de DNIs (deben coincidir con los creados en verificarYPoblarUsuarios)
        List<Integer> medicosDNIs = List.of(
                1, 48456123, 10000003, 10000004, 10000005,
                10000006, 10000007, 10000008, 10000009, 10000010
        );
        List<Integer> pacientesDNIs = List.of(
                2, 45123789, 20000003, 20000004, 20000005, 20000006, 20000007, 20000008,
                20000009, 20000010, 20000011, 20000012, 20000013, 20000014, 20000015,
                20000016, 20000017, 20000018, 20000019, 20000020
        );
        // Lista de especialidades (de verificarYPoblarEspecialidades)
        List<String> especialidades = List.of(
                "Cardiología", "Dermatología", "Pediatría",
                "Clínica Médica", "Traumatología", "Ginecología"
        );

        // Estados posibles según la lógica
        List<String> estadosPasados = List.of("Realizado", "Realizado", "Realizado", "Cancelado"); // 75% Realizado
        List<String> estadosFuturos = List.of("Pendiente", "Pendiente", "Pendiente", "Cancelado"); // 75% Pendiente

        Random rand = new Random(42); // Usamos una semilla fija para que los datos sean siempre los mismos
        int idTurno = 1;

        // --- 2. GENERAR TURNOS PASADOS (1 al 19 de Nov) ---
        // (No pueden ser "Pendientes")
        for (int dia = 1; dia < 20; dia++) { // Loop hasta el día 19
            // Generar 2 turnos por cada día pasado
            for (int i = 0; i < 2; i++) {
                LocalDateTime fecha = LocalDateTime.of(2025, Month.NOVEMBER, dia, 9 + i * 3, (i == 0 ? 0 : 30)); // Ej. 9:00 y 12:30

                int medicoDNI = medicosDNIs.get(rand.nextInt(medicosDNIs.size()));
                int pacienteDNI = pacientesDNIs.get(rand.nextInt(pacientesDNIs.size()));
                String especialidad = especialidades.get(rand.nextInt(especialidades.size()));
                String estado = estadosPasados.get(rand.nextInt(estadosPasados.size()));

                turnos.add(new Turno(idTurno++, pacienteDNI, medicoDNI, especialidad, fecha, estado));
            }
        }
        // Total: 19 dias * 2 turnos/dia = 38 turnos

        // --- 3. GENERAR TURNOS DE "HOY" (20 de Nov) ---
        // (Lógica especial: "hoy" es a las 10:00 AM)

        // Turno de la mañana (pasado) -> Realizado o Cancelado
        turnos.add(new Turno(idTurno++,
                pacientesDNIs.get(0), medicosDNIs.get(0), "Cardiología",
                hoy.withHour(9).withMinute(0), "Realizado" // 9:00 AM, antes de las 10:00
        ));

        // Turno de la tarde (futuro) -> Pendiente o Cancelado
        turnos.add(new Turno(idTurno++,
                pacientesDNIs.get(1), medicosDNIs.get(1), "Cardiología",
                hoy.withHour(14).withMinute(30), "Pendiente" // 14:30, después de las 10:00
        ));

        // Turno cancelado de hoy
        turnos.add(new Turno(idTurno++,
                pacientesDNIs.get(2), medicosDNIs.get(2), "Dermatología",
                hoy.withHour(16).withMinute(0), "Cancelado" // 16:00, futuro
        ));
        // Total: 3 turnos

        // --- 4. GENERAR TURNOS FUTUROS (21 al 30 de Nov) ---
        // (No pueden ser "Realizados")
        for (int dia = 21; dia <= 30; dia++) {
            // Generar 1 turno por día
            LocalDateTime fecha = LocalDateTime.of(2025, Month.NOVEMBER, dia, 11, 0);

            int medicoDNI = medicosDNIs.get(rand.nextInt(medicosDNIs.size()));
            int pacienteDNI = pacientesDNIs.get(rand.nextInt(pacientesDNIs.size()));
            String especialidad = especialidades.get(rand.nextInt(especialidades.size()));
            String estado = estadosFuturos.get(rand.nextInt(estadosFuturos.size()));

            turnos.add(new Turno(idTurno++, pacienteDNI, medicoDNI, especialidad, fecha, estado));
        }
        // Total: 10 dias * 1 turno/dia = 10 turnos

        // --- 5. GENERAR ALGUNOS TURNOS DE DICIEMBRE ---
        // (No pueden ser "Realizados")
        for (int dia = 1; dia <= 16; dia++) {
            LocalDateTime fecha = LocalDateTime.of(2025, Month.DECEMBER, dia, 10 + (dia % 5), (dia % 2 == 0 ? 0 : 30)); // Horas y minutos variados

            int medicoDNI = medicosDNIs.get(rand.nextInt(medicosDNIs.size()));
            int pacienteDNI = pacientesDNIs.get(rand.nextInt(pacientesDNIs.size()));
            String especialidad = especialidades.get(rand.nextInt(especialidades.size()));
            String estado = estadosFuturos.get(rand.nextInt(estadosFuturos.size()));

            turnos.add(new Turno(idTurno++, pacienteDNI, medicoDNI, especialidad, fecha, estado));
        }
        // Total: 16 turnos

        // TOTAL CREADO: 38 + 3 + 10 + 16 = 67 Turnos.

        // Guardar los nuevos turnos
        AccesoDatos.guardarTurnos(turnos);
    }
}