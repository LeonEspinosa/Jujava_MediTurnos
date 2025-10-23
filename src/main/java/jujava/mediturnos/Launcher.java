package jujava.mediturnos;

// ERROR 24: Esta clase apuntaba a 'HelloApplication' que no existe.
// Debe apuntar a 'AppMain' en el paquete 'presentacion.vista'.
import jujava.mediturnos.presentacion.vista.AppMain;
import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(AppMain.class, args);
    }
}

