package com.mycompany.sistemadeautenticacion;

import java.io.Serializable;

public class Administrador extends Usuario implements Serializable {

    public Administrador(String nombre, String nombreUsuario, String contrasena) {
        super(nombre, nombreUsuario, contrasena, new Rol("ADMIN", "Administrador"));
    }

    // opcional: métodos administrativos
}
