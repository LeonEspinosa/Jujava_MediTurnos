package jujava.mediturnos.datos;

import java.io.*;
import java.util.*;
import jujava.mediturnos.logica.*;
public class AccesoDatos {
    private static final String rutaPacientes = "archivos/pacientes.csv";
    private static final String rutaMedicos = "archivos/medicos.csv";
    private static final String rutaAdministrativos = "archivos/administrativos.csv";
    /**
     * Verifica si el directorio padre de la ruta del archivo existe y lo crea si no.
     */
    private static void asegurarDirectorio(String rutaArchivo) {
        File archivo = new File(rutaArchivo);
        File directorio = archivo.getParentFile();
        // Solo procede si la ruta tiene un directorio padre y este no existe
        if (directorio != null && !directorio.exists()) {
            if (directorio.mkdirs()) { // .mkdirs() crea todos los directorios necesarios
                System.out.println(" Directorio de archivos creado: " + directorio.getAbsolutePath());
            } else {
                System.err.println(" Error: No se pudo crear el directorio: " + directorio.getAbsolutePath());
            }
        }
    }
    // PACIENTES
    public static void guardarPacientes(List<Paciente> listaPacientes) {
        asegurarDirectorio(rutaPacientes); // <-- Llamada al método de aseguramiento
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaPacientes))) {
            pw.println("nombre,apellido,dni,genero,telefono,obraSocial");
            for (Paciente p : listaPacientes) {
                pw.println(p.getNombre() + "," +
                        p.getApellido() + "," +
                        p.getDni() + "," +
                        p.getGenero() + "," +
                        p.getTelefono() + "," +
                        p.getObraSocial());
            }
            System.out.println("Pacientes guardados correctamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar pacientes: " + e.getMessage());
        }
    }
    public static List<Paciente> cargarPacientes() {
        List<Paciente> lista = new ArrayList<>();
        File archivo = new File(rutaPacientes);
        if (!archivo.exists()) {
            System.out.println(" No existe el archivo de pacientes. Se devuelve lista vacía.");
            return lista;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; } // salto encabezado
                String[] datos = linea.split(",");
                if (datos.length == 6) {
                    String nombre = datos[0];
                    String apellido = datos[1];
                    int dni = Integer.parseInt(datos[2].trim());
                    char genero = datos[3].trim().charAt(0);
                    int telefono = Integer.parseInt(datos[4].trim());
                    String obraSocial = datos[5];
                    lista.add(new Paciente(nombre, apellido, dni, genero, telefono, obraSocial));
                }
            }
            System.out.println("Pacientes cargados correctamente.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al cargar pacientes: " + e.getMessage());
        }
        return lista;
    }
    // MEDICOS
    public static void guardarMedicos(List<Medico> listaMedicos) {
        asegurarDirectorio(rutaMedicos); // <-- Llamada al método de aseguramiento
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaMedicos))) {
            pw.println("nombre,apellido,dni,genero,telefono,matricula,especialidad");
            for (Medico m : listaMedicos) {

                pw.println(m.getNombre() + "," +
                        m.getApellido() + "," +
                        m.getDni() + "," +
                        m.getGenero() + "," +
                        m.getTelefono() + "," +
                        m.getMatricula() + "," +
                        m.getEspecialidad());
            }
            System.out.println("Médicos guardados correctamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar médicos: " + e.getMessage());
        }
    }
    public static List<Medico> cargarMedicos() {
        List<Medico> lista = new ArrayList<>();
        File archivo = new File(rutaMedicos);
        if (!archivo.exists()) {
            System.out.println("No existe el archivo de médicos. Se devuelve lista vacía.");
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
                    int telefono = Integer.parseInt(datos[4].trim());
                    String matricula = datos[5];
                    String especialidad = datos[6];
                    lista.add(new Medico(nombre, apellido, dni, genero, telefono, matricula, especialidad));
                } else {
                    // Mensaje de advertencia si la línea tiene formato incorrecto
                    System.err.println("Advertencia: Línea de médico inválida (campos incorrectos): " + linea);
                }
            }
            System.out.println("Médicos cargados correctamente.");
        } catch (IOException | NumberFormatException e) {
            System.out.println(" Error al cargar médicos: " + e.getMessage());
        }
        return lista;
    }
    // ADMINISTRADORES
    public static void guardarAdministradores(List<Administrador> listaAdministradores) {
        asegurarDirectorio(rutaAdministrativos); // <-- Llamada al método de aseguramiento
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaAdministrativos))) {
            pw.println("nombre,apellido,dni,genero,telefono,area");
            for (Administrador a : listaAdministradores) {
                pw.println(a.getNombre() + "," +
                        a.getApellido() + "," +
                        a.getDni() + "," +
                        a.getGenero() + "," +
                        a.getTelefono() + "," +
                        a.getArea());
            }
            System.out.println("Administradores guardados correctamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar administradores: " + e.getMessage());
        }
    }
    public static List<Administrador> cargarAdministradores() {
        List<Administrador> lista = new ArrayList<>();
        File archivo = new File(rutaAdministrativos);
        if (!archivo.exists()) {
            System.out.println("No existe el archivo de administradores. Se devuelve lista vacía.");
            return lista;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; }
                String[] datos = linea.split(",");
                if (datos.length == 6) {
                    String nombre = datos[0];
                    String apellido = datos[1];
                    int dni = Integer.parseInt(datos[2].trim());
                    char genero = datos[3].trim().charAt(0);
                    int telefono = Integer.parseInt(datos[4].trim());
                    String area = datos[5];
                    lista.add(new Administrador(nombre, apellido, dni, genero, telefono, area));
                }
            }
            System.out.println("Administradores cargados correctamente.");
        } catch (IOException | NumberFormatException e) {
            System.out.println(" Error al cargar administradores: " + e.getMessage());
        }
        return lista;
    }
}

