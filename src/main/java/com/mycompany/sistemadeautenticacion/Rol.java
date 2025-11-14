package com.mycompany.sistemadeautenticacion;


public class Rol {
    private String nombre;
    private String descripcion;

    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Rol(String nombre) { this(nombre, ""); }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return nombre + (descripcion != null && !descripcion.isBlank() ? " - " + descripcion : "");
    }
}
