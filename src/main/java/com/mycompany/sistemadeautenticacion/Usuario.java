package com.mycompany.sistemadeautenticacion;


import java.io.Serializable;

public class Usuario implements Serializable {
    private String nombre;          // nombre real
    private String nombreUsuario;   // username/login
    private String contrasena;
    private Rol rol;

    public Usuario(String nombre, String nombreUsuario, String contrasena, Rol rol) {
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // getters
    public String getNombre() { return nombre; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getContrasena() { return contrasena; }
    public Rol getRol() { return rol; }

    public boolean autenticar(String pass) {
        return contrasena != null && contrasena.equals(pass);
    }

    @Override
    public String toString() {
        return nombre + " (" + nombreUsuario + ") - " + rol.getNombre();
    }
}
