package com.example.farmaciaproject;

import java.sql.Date;
import java.time.LocalDate;

import javafx.beans.property.*;

public class Producto {
    private IntegerProperty id;
    private StringProperty nombre;
    private IntegerProperty cantidad;
    private DoubleProperty precio;
    private ObjectProperty<Date> fechaVencimiento;

    public Producto(String nombre, int cantidad, double precio, Date fechaVencimiento) {
        this.nombre = new SimpleStringProperty(nombre);
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.precio = new SimpleDoubleProperty(precio);
        this.fechaVencimiento = new SimpleObjectProperty<>(fechaVencimiento);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad.get();
    }

    public IntegerProperty cantidadProperty() {
        return cantidad;
    }

    public double getPrecio() {
        return precio.get();
    }

    public DoubleProperty precioProperty() {
        return precio;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento.get();
    }

    public ObjectProperty<Date> fechaVencimientoProperty() {
        return fechaVencimiento;
    }

    public void setId(int id) {
        this.id = new SimpleIntegerProperty(id);
    }
}


