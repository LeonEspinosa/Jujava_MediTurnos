package jujava.mediturnos.presentacion.controladores;
import jujava.mediturnos.logica.ServicioExportacion;
import jujava.mediturnos.presentacion.modelos.TurnoEstadisticaModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ReportesViewController {


   // A. Componentes FXML
   @FXML private DatePicker dtpFechaInicio;
   @FXML private DatePicker dtpFechaFin;
   @FXML private Button btnGenerar;
   @FXML private Button btnExportarExcel;


   @FXML private Label lblTasaAsistencia;
   @FXML private Label lblTasaCancelacion;


   @FXML private TableView<TurnoEstadisticaModel> tblEspecialidad;
   @FXML private TableColumn<TurnoEstadisticaModel, String> colEspecialidadDesc;
   @FXML private TableColumn<TurnoEstadisticaModel, Long> colEspecialidadCant;


   @FXML private TableView<TurnoEstadisticaModel> tblMedico;
   @FXML private TableColumn<TurnoEstadisticaModel, String> colMedicoDesc;
   @FXML private TableColumn<TurnoEstadisticaModel, Long> colMedicoCant;


   @FXML private TableView<TurnoEstadisticaModel> tblEstado;
   @FXML private TableColumn<TurnoEstadisticaModel, String> colEstadoDesc;
   @FXML private TableColumn<TurnoEstadisticaModel, Long> colEstadoCant;


   @FXML private TableView<TurnoEstadisticaModel> tblObraSocial;
   @FXML private TableColumn<TurnoEstadisticaModel, String> colObraSocialDesc;
   @FXML private TableColumn<TurnoEstadisticaModel, Long> colObraSocialCant;




   // B. Variables de instancia
   private MainController dataController;
   private MainViewController navigationController;


   // Para guardar los resultados y poder exportar
   private Map<String, Long> datosPorEspecialidad;
   private Map<String, Long> datosPorMedico;
   private Map<String, Long> datosPorEstado;
   private Map<String, Long> datosPorObraSocial;


   @FXML
   public void initialize() {
       // Configurar las columnas de las tablas usando el modelo TurnoEstadisticaModel
       colEspecialidadDesc.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
       colEspecialidadCant.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());


       colMedicoDesc.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
       colMedicoCant.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());


       colEstadoDesc.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
       colEstadoCant.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());


       colObraSocialDesc.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());
       colObraSocialCant.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());


       // Inicializar etiquetas de tasas
       lblTasaAsistencia.setText("N/A");
       lblTasaCancelacion.setText("N/A");
       btnExportarExcel.setDisable(true);
   }


   public void initData(MainController dataController, MainViewController navigationController) {
       this.dataController = dataController;
       this.navigationController = navigationController;
       // Al cargar la vista, podría ser útil generar un reporte inicial (ej. el mes actual o sin filtrar)
       // handleGenerarReporte();
   }


   // C. Implementar handleGenerarReporte
   @FXML
   private void handleGenerarReporte() {
       LocalDate fechaInicio = dtpFechaInicio.getValue();
       LocalDate fechaFin = dtpFechaFin.getValue();


       // 1. Validación de fechas
       if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
           dataController.showAlert(Alert.AlertType.ERROR, "Error de Fechas", "La Fecha de Inicio no puede ser posterior a la Fecha Fin.");
           return;
       }


       // 2. Llamar a los métodos del dataController (se asume que MainController tiene los métodos implementados)
       try {
           datosPorEspecialidad = dataController.getTurnosPorEspecialidad(fechaInicio, fechaFin);
           datosPorMedico = dataController.getTurnosPorMedicoReporte(fechaInicio, fechaFin);
           datosPorEstado = dataController.getTurnosPorEstado(fechaInicio, fechaFin);
           datosPorObraSocial = dataController.getTurnosPorObraSocial(fechaInicio, fechaFin);


           // 3. Poblar las TableViews
           tblEspecialidad.setItems(convertMapToObservableList(datosPorEspecialidad));
           tblMedico.setItems(convertMapToObservableList(datosPorMedico));
           tblEstado.setItems(convertMapToObservableList(datosPorEstado));
           tblObraSocial.setItems(convertMapToObservableList(datosPorObraSocial));


           // 4. Cálculo de Tasas y actualización de etiquetas
           calcularYMostrarTasas(datosPorEstado);


           // 5. Habilitar Exportar
           btnExportarExcel.setDisable(false);


           dataController.showAlert(Alert.AlertType.INFORMATION, "Reporte Generado", "Se generó el reporte para el período seleccionado.");


       } catch (Exception e) {
           e.printStackTrace();
           dataController.showAlert(Alert.AlertType.ERROR, "Error en Reporte", "Ocurrió un error al generar el reporte.");
       }


   }


   /**
    * Helper para convertir Map<String, Long> a ObservableList<TurnoEstadisticaModel>.
    */
   private ObservableList<TurnoEstadisticaModel> convertMapToObservableList(Map<String, Long> map) {
       List<TurnoEstadisticaModel> list = new ArrayList<>();
       if (map == null) return FXCollections.observableArrayList(list);


       map.forEach((descripcion, cantidad) -> {
           list.add(new TurnoEstadisticaModel(descripcion, cantidad));
       });


       // Ordenar por Cantidad (descendente)
       list.sort((a, b) -> Long.compare(b.getCantidad(), a.getCantidad()));


       return FXCollections.observableArrayList(list);
   }


   /**
    * Calcula y actualiza las etiquetas de Tasa de Asistencia y Tasa de Cancelación.
    */
   private void calcularYMostrarTasas(Map<String, Long> datosPorEstado) {
       if (datosPorEstado == null || datosPorEstado.isEmpty()) {
           lblTasaAsistencia.setText("N/A");
           lblTasaCancelacion.setText("N/A");
           return;
       }


       long realizados = datosPorEstado.getOrDefault("Realizado", 0L);
       long cancelados = datosPorEstado.getOrDefault("Cancelado", 0L);
              // Aquí incluiremos TODOS los estados para el cálculo de tasas:
       long total = datosPorEstado.values().stream().mapToLong(Long::longValue).sum();


       if (total == 0) {
           lblTasaAsistencia.setText("0.00%");
           lblTasaCancelacion.setText("0.00%");
           return;
       }


       // Fórmulas de Tasa: (Realizados / Total) * 100 y (Cancelados / Total) * 100
       double tasaAsistencia = (double) realizados / total * 100;
       double tasaCancelacion = (double) cancelados / total * 100;


       // Formato con dos decimales y el símbolo de porcentaje
       lblTasaAsistencia.setText(String.format("%.2f%%", tasaAsistencia));
       lblTasaCancelacion.setText(String.format("%.2f%%", tasaCancelacion));
   }




   @FXML
private void handleExportarExcel() {
    try {
        ServicioExportacion.exportarEstadisticasAExcel(
            dtpFechaInicio.getValue(),
            dtpFechaFin.getValue(),
            datosPorEspecialidad,
            datosPorMedico,
            datosPorEstado,
            datosPorObraSocial
        );
        dataController.showAlert(Alert.AlertType.INFORMATION, "Exportación Exitosa", "El reporte fue exportado correctamente.");
    } catch (Exception e) {
        e.printStackTrace();
        dataController.showAlert(Alert.AlertType.ERROR, "Error de Exportación", "Ocurrió un error al exportar el archivo.");
    }
}
}


