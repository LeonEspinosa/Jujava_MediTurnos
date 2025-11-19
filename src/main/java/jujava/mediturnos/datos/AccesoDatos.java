package jujava.mediturnos.datos;

import jujava.mediturnos.logica.entidades.Administrador;
import jujava.mediturnos.logica.entidades.Medico;
import jujava.mediturnos.logica.entidades.Paciente;
import jujava.mediturnos.logica.entidades.Turno;
import java.time.LocalDateTime;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AccesoDatos {
    private static final String rutaPacientes = "archivos/pacientes.csv";
    private static final String rutaMedicos = "archivos/medicos.csv";
    private static final String rutaAdministrativos = "archivos/administrativos.csv";
    private static final String rutaTurnos = "archivos/turnos.csv";
    private static final String FORMATO_FECHA_HORA = "yyyy-MM-dd HH:mm";
    private static final String rutaEspecialidades = "archivos/especialidades.csv";

    private static void asegurarDirectorio(String rutaArchivo) {
        File archivo = new File(rutaArchivo);
        File directorio = archivo.getParentFile();

        if (directorio != null && !directorio.exists()) {
            if (directorio.mkdirs()) { // .mkdirs() crea todos los directorios necesarios
            }
        }
    }
    // PACIENTES
    public static void guardarPacientes(List<Paciente> listaPacientes) {
        asegurarDirectorio(rutaPacientes); // <-- Llamada al método de aseguramiento
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaPacientes))) {
            pw.println("nombre,apellido,dni,genero,telefono,obraSocial,PasswordHash");
            for (Paciente p : listaPacientes) {
                pw.println(p.getNombre() + "," +
                        p.getApellido() + "," +
                        p.getDni() + "," +
                        p.getGenero() + "," +
                        p.getTelefono() + "," +
                        p.getObraSocial()+","+
                        p.getPasswordHash());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<Paciente> cargarPacientes() {
        List<Paciente> lista = new ArrayList<>();
        File archivo = new File(rutaPacientes);
        if (!archivo.exists()) {
            return lista;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; } // salto encabezado
                String[] datos = linea.split(",");
                if (datos.length == 7) {
                    String nombre = datos[0];
                    String apellido = datos[1];
                    int dni = Integer.parseInt(datos[2].trim());
                    char genero = datos[3].trim().charAt(0);
                    //int telefono = Integer.parseInt(datos[4].trim());
                    long telefono = Long.parseLong(datos[4].trim());
                    String obraSocial = datos[5];
                    String passwordHash = datos[6];
                    lista.add(new Paciente(nombre, apellido, dni, genero, telefono,passwordHash, obraSocial));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return lista;
    }
    // MEDICOS
    public static void guardarMedicos(List<Medico> listaMedicos) {
        asegurarDirectorio(rutaMedicos); // <-- Llamada al método de aseguramiento
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaMedicos))) {
            pw.println("nombre,apellido,dni,genero,telefono,matricula,especialidad,passwordHash");
            for (Medico m : listaMedicos) {

                pw.println(m.getNombre() + "," +
                        m.getApellido() + "," +
                        m.getDni() + "," +
                        m.getGenero() + "," +
                        m.getTelefono() + "," +
                        m.getMatricula() + "," +
                        m.getEspecialidad()+ "," +
                        m.getPasswordHash());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<Medico> cargarMedicos() {
        List<Medico> lista = new ArrayList<>();
        File archivo = new File(rutaMedicos);
        if (!archivo.exists()) {
            return lista;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; } // salto encabezado
                String[] datos = linea.split(",");
                if (datos.length == 8) {
                    String nombre = datos[0];
                    String apellido = datos[1];
                    int dni = Integer.parseInt(datos[2].trim());
                    char genero = datos[3].trim().charAt(0);
                    //int telefono = Integer.parseInt(datos[4].trim());
                    long telefono = Long.parseLong(datos[4].trim());
                    String matricula = datos[5];
                    String especialidad = datos[6];
                    String passwordHash = datos[7];
                    lista.add(new Medico(nombre, apellido,dni, genero, telefono,passwordHash, matricula, especialidad));
                } else {
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return lista;
    }
    // ADMINISTRADORES
    public static void guardarAdministradores(List<Administrador> listaAdministradores) {
        asegurarDirectorio(rutaAdministrativos); // <-- Llamada al método de aseguramiento
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaAdministrativos))) {
            pw.println("nombre,apellido,dni,genero,telefono,area,passwordHash");
            for (Administrador a : listaAdministradores) {
                pw.println(a.getNombre() + "," +
                        a.getApellido() + "," +
                        a.getDni() + "," +
                        a.getGenero() + "," +
                        a.getTelefono() + "," +
                        a.getArea()+","+
                        a.getPasswordHash());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<Administrador> cargarAdministradores() {
        List<Administrador> lista = new ArrayList<>();
        File archivo = new File(rutaAdministrativos);
        if (!archivo.exists()) {
            return lista;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; }
                String[] datos = linea.split(",");
                if (datos.length == 7) {
                    String nombre = datos[0];
                    String apellido = datos[1];
                    int dni = Integer.parseInt(datos[2].trim());
                    char genero = datos[3].trim().charAt(0);
                    //int telefono = Integer.parseInt(datos[4].trim());
                    long telefono = Long.parseLong(datos[4].trim());
                    String area = datos[5];
                    String passwordHash = datos[6];
                    lista.add(new Administrador(nombre, apellido, dni, genero, telefono, passwordHash, area));
                }

            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static void guardarTurnos(List<Turno> listaTurnos) {
        asegurarDirectorio(rutaTurnos);
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaTurnos))) {
            pw.println("idTurno,dniPaciente,dniMedico,especialidad,fechaHora,estado");
            for (Turno t : listaTurnos) {
                pw.println(t.getIdTurno() + "," +
                        t.getDniPaciente() + "," +
                        t.getDniMedico() + "," +
                        t.getEspecialidad() + "," +
                        t.getFechaHora().format(java.time.format.DateTimeFormatter.ofPattern(FORMATO_FECHA_HORA)) + "," +
                        t.getEstado());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Turno> cargarTurnos() {
        List<Turno> lista = new ArrayList<>();
        File archivo = new File(rutaTurnos);
        if (!archivo.exists()) {
            return lista;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primera = true;
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(FORMATO_FECHA_HORA);
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; } // salto encabezado
                String[] datos = linea.split(",");
                if (datos.length == 6) {
                    try {
                        int idTurno = Integer.parseInt(datos[0].trim());
                        int dniPaciente = Integer.parseInt(datos[1].trim());
                        int dniMedico = Integer.parseInt(datos[2].trim());
                        String especialidad = datos[3];
                        LocalDateTime fechaHora = LocalDateTime.parse(datos[4].trim(), formatter);
                        String estado = datos[5];
                        lista.add(new Turno(idTurno, dniPaciente, dniMedico, especialidad, fechaHora, estado));
                    } catch (NumberFormatException | java.time.format.DateTimeParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }
    public static void guardarEspecialidades(List<String> listaEspecialidades) {
        asegurarDirectorio(rutaEspecialidades);
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaEspecialidades))) {
            for (String especialidad : listaEspecialidades) {
                pw.println(especialidad);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> cargarEspecialidades() {
        List<String> lista = new ArrayList<>();
        File archivo = new File(rutaEspecialidades);
        if (!archivo.exists()) {
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                lista.add(linea.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lista;
    }
}





