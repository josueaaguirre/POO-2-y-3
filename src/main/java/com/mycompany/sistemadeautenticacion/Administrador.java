package com.mycompany.sistemadeautenticacion;


public class Administrador extends Usuario {

    public Administrador(String nombre, String nombreUsuario, String contrasena) {
        super(nombre, nombreUsuario, contrasena, new Rol("ADMIN", "Administrador"));
    }

    // opcional: métodos administrativos (se usa desde SistemaAutenticacion)
}
