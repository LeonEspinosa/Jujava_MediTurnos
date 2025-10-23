package jujava.mediturnos.presentacion.vista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Vista Principal (Clase Application).
 * Ahora solo carga el FXML principal (main-view.fxml)
 * y adjunta la hoja de estilos CSS.
 */
public class AppMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. Cargar el FXML de la ventana principal
            FXMLLoader loader = new FXMLLoader();

            // ERROR 26: La ruta a los recursos FXML estaba incorrecta.
            // Debe apuntar a la raíz de 'resources' y luego al paquete.
            loader.setLocation(AppMain.class.getResource("/jujava/mediturnos/main-view.fxml"));

            BorderPane rootLayout = (BorderPane) loader.load();

            // 2. Crear la escena
            Scene scene = new Scene(rootLayout);

            // 3. (IMPORTANTE) Cargar la hoja de estilos CSS
            // ERROR 27: La ruta al CSS también estaba incorrecta.
            String css = AppMain.class.getResource("/jujava/mediturnos/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            // 4. Configurar el Stage
            primaryStage.setTitle("Gestión de Turnos (FXML)");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            // Imprimir un error detallado si el FXML o CSS no se encuentran
            e.printStackTrace();
            System.err.println("Error: No se pudo cargar 'main-view.fxml' o 'styles.css'.");
            System.err.println("Asegúrese de que estén en 'src/main/resources/jujava/mediturnos/'");
        }
    }
}

