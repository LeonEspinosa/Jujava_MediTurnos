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

public class ServicioExportacion {

    public static void exportarEstadisticasAExcel(LocalDate inicio, LocalDate fin,
        Map<String, Long> datosEsp, Map<String, Long> datosMed,
        Map<String, Long> datosEst, Map<String, Long> datosOS) {

        // Elegir ubicación del archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo Excel (*.xlsx)", "*.xlsx"));
        File archivo = fileChooser.showSaveDialog(null); // null si no tenés referencia a Stage

        if (archivo != null) {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {

                // Crear hojas con datos
                crearHoja(workbook, "Por Especialidad", datosEsp, inicio, fin);
                crearHoja(workbook, "Por Médico", datosMed, inicio, fin);
                crearHoja(workbook, "Por Estado", datosEst, inicio, fin);
                crearHoja(workbook, "Por Obra Social", datosOS, inicio, fin);

                // Guardar archivo
                try (FileOutputStream fos = new FileOutputStream(archivo)) {
                    workbook.write(fos);
                }

            } catch (IOException e) {
                e.printStackTrace();
                // Podés lanzar una excepción personalizada si querés manejarla en el controlador
            }
        }
    }

    private static void crearHoja(XSSFWorkbook workbook, String nombreHoja, Map<String, Long> datos,
                                  LocalDate inicio, LocalDate fin) {
        XSSFSheet hoja = workbook.createSheet(nombreHoja);
        int filaActual = 0;

        // Cabecera con rango de fechas
        Row cabecera = hoja.createRow(filaActual++);
        Cell celdaCabecera = cabecera.createCell(0);
        String textoCabecera = (inicio != null && fin != null)
            ? "Reporte para el período: " + inicio + " a " + fin
            : "Reporte Total";
        celdaCabecera.setCellValue(textoCabecera);

        // Títulos de columnas
        Row titulos = hoja.createRow(filaActual++);
        titulos.createCell(0).setCellValue("Categoría");
        titulos.createCell(1).setCellValue("Cantidad");

        // Datos
        for (Map.Entry<String, Long> entrada : datos.entrySet()) {
            Row fila = hoja.createRow(filaActual++);
            fila.createCell(0).setCellValue(entrada.getKey());
            fila.createCell(1).setCellValue(entrada.getValue());
        }

        // Autoajuste de columnas
        hoja.autoSizeColumn(0);
        hoja.autoSizeColumn(1);
    }
}

