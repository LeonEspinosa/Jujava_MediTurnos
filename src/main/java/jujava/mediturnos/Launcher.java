package jujava.mediturnos;

import jujava.mediturnos.presentacion.vista.AppMain;
import javafx.application.Application;

/**
 * Punto de entrada principal.
 * Lanza la aplicación AppMain.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(AppMain.class, args);
    }
}