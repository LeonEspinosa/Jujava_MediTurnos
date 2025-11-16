// ERROR 25: El módulo y los paquetes estaban incorrectos.
module jujava.mediturnos {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires jbcrypt;
    requires java.base;

    // 🔹 Abre los paquetes de presentación para que JavaFX (via FXML) pueda acceder a ellos.
    opens jujava.mediturnos.presentacion.vista to javafx.fxml;
    opens jujava.mediturnos.presentacion.controladores to javafx.fxml;
    // 🔹 Abre el paquete de modelos para 'binding' de propiedades en la TableView.
    opens jujava.mediturnos.presentacion.modelos to javafx.base;

    // 🔹 Exporta los paquetes que otros módulos (si los hubiera) podrían usar.
    exports jujava.mediturnos;
    exports jujava.mediturnos.presentacion.vista;
    exports jujava.mediturnos.presentacion.controladores;
    exports jujava.mediturnos.presentacion.modelos;

    // 🔹 Exporta los paquetes de lógica y datos para que la capa de presentación pueda usarlos
    exports jujava.mediturnos.logica;
    exports jujava.mediturnos.datos;
    exports jujava.mediturnos.logica.entidades;
}

