module com.example.myjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    // 🔹 Abre los paquetes necesarios para FXML y controladores
    opens com.example.myjavafx to javafx.fxml;
    opens com.example.myjavafx.view to javafx.fxml;
    opens com.example.myjavafx.controller to javafx.fxml;
    opens com.example.myjavafx.model to javafx.base; // solo este usa javafx.base

    // 🔹 Exporta los paquetes que otros módulos pueden usar
    exports com.example.myjavafx;
    exports com.example.myjavafx.view;
    exports com.example.myjavafx.controller;
    exports com.example.myjavafx.model;
}
