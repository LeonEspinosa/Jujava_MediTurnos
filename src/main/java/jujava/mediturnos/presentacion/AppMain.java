package jujava.mediturnos.presentacion;

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

            // Usamos una ruta absoluta desde la raíz de 'resources'
            // Esto es más robusto.
            loader.setLocation(AppMain.class.getResource("/com/example/myjavafx/view/main-view.fxml"));

            BorderPane rootLayout = (BorderPane) loader.load();

            // 2. Crear la escena
            Scene scene = new Scene(rootLayout);

            // 3. (IMPORTANTE) Cargar la hoja de estilos CSS
            // También usamos una ruta absoluta
            String css = AppMain.class.getResource("/com/example/myjavafx/view/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            // 4. Configurar el Stage
            primaryStage.setTitle("Gestión de Turnos (FXML)");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            // Imprimir un error detallado si el FXML o CSS no se encuentran
            e.printStackTrace();
            System.err.println("Error: No se pudo cargar 'main-view.fxml' o 'styles.css'.");
            System.err.println("Asegúrese de que estén en 'src/main/resources/com/example/myjavafx/view/'");
        }
    }
}

