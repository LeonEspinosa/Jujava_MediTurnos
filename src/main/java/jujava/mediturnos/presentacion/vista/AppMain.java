package jujava.mediturnos.presentacion.vista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jujava.mediturnos.presentacion.controladores.LoginViewController;
import jujava.mediturnos.presentacion.controladores.MainViewController;

import java.io.IOException;


public class AppMain extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private MainViewController mainViewController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Gestión de Turnos (MediTurnos)");

        // 1. Cargar el FXML de la ventana principal (main-view.fxml)
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppMain.class.getResource("/jujava/mediturnos/main-view.fxml"));
            rootLayout = (BorderPane) loader.load();

            mainViewController = loader.getController();

            // Inyectamos el Stage principal en el MainViewController
            mainViewController.setPrimaryStage(this.primaryStage);

            // 2. Crear la escena principal
            Scene scene = new Scene(rootLayout);

            // 3. Cargar la hoja de estilos CSS
            String css = AppMain.class.getResource("/jujava/mediturnos/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            // 4. Configurar el Stage principal
            primaryStage.setScene(scene);

            // primaryStage.show();

            mostrarVentanaLogin();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: No se pudo cargar 'main-view.fxml'.");

        } catch (IllegalStateException e) {
            e.printStackTrace();
            System.err.println("Error: Problema al obtener el controlador o cargar FXML. ¿Está bien configurado?");

        }
    }


    public void mostrarVentanaLogin() {
        try {
            // Cargar el FXML del login
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppMain.class.getResource("/jujava/mediturnos/login-view.fxml"));
            VBox loginLayout = (VBox) loader.load(); // El root de login-view.fxml es un VBox

            // Crear un nuevo Stage (ventana) para el login
            Stage loginStage = new Stage();
            loginStage.setTitle("Inicio de Sesión");
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.initOwner(primaryStage);
            loginStage.setResizable(false);

            Scene loginScene = new Scene(loginLayout);

            // Aplicar la misma hoja de estilos al login
            String css = AppMain.class.getResource("/jujava/mediturnos/styles.css").toExternalForm();
            if (css != null) {
                loginScene.getStylesheets().add(css);
            } else {
                System.err.println("Advertencia: No se encontró 'styles.css' para el login.");
            }


            loginStage.setScene(loginScene);

            // --- INYECCIÓN DE DEPENDENCIA ---

            LoginViewController loginController = loader.getController();
            if (loginController != null && mainViewController != null) {
                loginController.initData(loginStage, mainViewController);
            } else {
                System.err.println("Error: No se pudo obtener el LoginViewController o MainViewController para la inyección.");

                return;
            }

            loginStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: No se pudo cargar 'login-view.fxml'.");

        } catch (IllegalStateException e) {
            e.printStackTrace();
            System.err.println("Error: Problema al obtener el controlador de login. ¿Está bien configurado?");

        }
    }

    /**
     * Punto de entrada principal de la aplicación.
     * @param args Argumentos de línea de comandos (no utilizados aquí).
     */
    public static void main(String[] args) {
        launch(args);
    }
}

