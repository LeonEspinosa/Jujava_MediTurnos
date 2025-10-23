module com.example.myjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    // Abrir la raíz para el Launcher
    opens jujava.mediturnos to javafx.fxml;
    exports jujava.mediturnos;

    // Abrir Controladores (para que FXML los encuentre)
    opens jujava.mediturnos.presentacion.controladores to javafx.fxml;
    exports jujava.mediturnos.presentacion.controladores;

    // Abrir Modelos de Vista (para JavaFX Property binding)
    opens jujava.mediturnos.presentacion.modelos to javafx.base;
    exports jujava.mediturnos.presentacion.modelos;

    // Abrir Vistas
    opens jujava.mediturnos.presentacion.vista to javafx.fxml;
    exports jujava.mediturnos.presentacion.vista;

    // Exportar lógica y datos
    exports jujava.mediturnos.logica;
    exports jujava.mediturnos.logica.entidades;
    exports jujava.mediturnos.datos;
}
