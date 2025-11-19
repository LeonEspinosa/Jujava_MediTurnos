package jujava.mediturnos.presentacion.modelos;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.LongProperty;

public class TurnoEstadisticaModel {

    private final StringProperty descripcion;
    private final LongProperty cantidad;

    public TurnoEstadisticaModel(String descripcion, Long cantidad) {
        this.descripcion = new SimpleStringProperty(descripcion);
        this.cantidad = new SimpleLongProperty(cantidad);
    }

    // Propiedades para TableView
    public StringProperty descripcionProperty() { return descripcion; }
    public LongProperty cantidadProperty() { return cantidad; }

    // Getters para lógica
    public String getDescripcion() { return descripcion.get(); }
    public Long getCantidad() { return cantidad.get(); }
}