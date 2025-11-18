package jujava.mediturnos.logica;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

/**
 * Clase de servicio desacoplada para manejar la lógica de exportación de datos
 * a un archivo Excel (.xlsx) utilizando Apache POI.
 */
public class ServicioExportacion {

    /**
     * Método estático principal que genera y guarda un archivo Excel con 4 hojas de estadísticas.
     * Muestra un FileChooser de JavaFX para que el usuario elija dónde guardar.
     *
     * @param ventana La ventana (Stage/Window) actual. Se usa para que el diálogo "Guardar Como" aparezca sobre la aplicación.
     * @param inicio Fecha de inicio del filtro (puede ser null).
     * @param fin Fecha de fin del filtro (puede ser null).
     * @param datosEsp Map con datos de Especialidades.
     * @param datosMed Map con datos de Médicos.
     * @param datosEst Map con datos de Estados.
     * @param datosOS Map con datos de Obras Sociales.
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
    public static void exportarEstadisticasAExcel(Window ventana, LocalDate inicio, LocalDate fin,
                                                  Map<String, Long> datosEsp, Map<String, Long> datosMed,
                                                  Map<String, Long> datosEst, Map<String, Long> datosOS) throws IOException {

        // 1. Configurar el FileChooser (diálogo "Guardar Como")
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("Reporte_MediTurnos_" + LocalDate.now().toString() + ".xlsx");

        File archivo = fileChooser.showSaveDialog(ventana);

        // 3. Si el usuario seleccionó un archivo (no presionó "Cancelar")
        if (archivo != null) {
            // Usamos un try-with-resources para asegurar que el Workbook se cierre
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {

                // 4. Crear las 4 hojas de cálculo
                crearHoja(workbook, "Por Especialidad", datosEsp, inicio, fin);
                crearHoja(workbook, "Por Médico", datosMed, inicio, fin);
                crearHoja(workbook, "Por Estado", datosEst, inicio, fin);
                crearHoja(workbook, "Por Obra Social", datosOS, inicio, fin);

                // 5. Escribir el archivo en el disco
                // (Usamos otro try-with-resources para el FileOutputStream)
                try (FileOutputStream fos = new FileOutputStream(archivo)) {
                    workbook.write(fos);
                }
                // Si llegamos aquí, la exportación fue exitosa.
                // El controlador (ReportesViewController) mostrará el alerta de éxito.

            } catch (IOException e) {
                // Si ocurre un error (ej. disco lleno, permisos), imprimimos el error
                // y lanzamos la excepción para que el controlador la atrape y muestre un alerta de error.
                e.printStackTrace();
                throw e; // Relanzamos la excepción
            }
        }
        // Si 'archivo' es null (el usuario canceló), el método simplemente termina
        // sin hacer nada y sin lanzar error.
    }

    /**
     * Método helper privado para crear una hoja de cálculo individual dentro del Workbook.
     */
    private static void crearHoja(XSSFWorkbook workbook, String nombreHoja, Map<String, Long> datos,
                                  LocalDate inicio, LocalDate fin) {

        XSSFSheet hoja = workbook.createSheet(nombreHoja);
        int filaActual = 0;

        // Estilo para la cabecera (negrita) - CORREGIDO
        XSSFFont fontCabecera = workbook.createFont();
        fontCabecera.setBold(true);

        CellStyle estiloCabecera = workbook.createCellStyle();
        estiloCabecera.setFont(fontCabecera);

        // Fila 0: Cabecera con rango de fechas
        Row cabecera = hoja.createRow(filaActual++);
        Cell celdaCabecera = cabecera.createCell(0);
        String textoCabecera = (inicio != null && fin != null)
                ? "Reporte para el período: " + inicio.toString() + " a " + fin.toString()
                : "Reporte Total (Sin filtro de fecha)";
        celdaCabecera.setCellValue(textoCabecera);

        // Fila 1: Títulos de columnas (en negrita)
        Row titulos = hoja.createRow(filaActual++);
        Cell celdaTituloCat = titulos.createCell(0);
        celdaTituloCat.setCellValue("Categoría");
        celdaTituloCat.setCellStyle(estiloCabecera);

        Cell celdaTituloCant = titulos.createCell(1);
        celdaTituloCant.setCellValue("Cantidad");
        celdaTituloCant.setCellStyle(estiloCabecera);


        // Filas 2..N: Datos del Map
        if (datos != null) {
            for (Map.Entry<String, Long> entrada : datos.entrySet()) {
                Row fila = hoja.createRow(filaActual++);
                fila.createCell(0).setCellValue(entrada.getKey());
                fila.createCell(1).setCellValue(entrada.getValue());
            }
        }

        // Autoajuste de tamaño de las columnas
        hoja.autoSizeColumn(0);
        hoja.autoSizeColumn(1);
    }
}