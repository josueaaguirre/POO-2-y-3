package com.mycompany.sistemadeautenticacion;

import java.io.Serializable;

public class Administrador extends Usuario implements Serializable {

    public Administrador(String nombre, String nombreUsuario, String contrasena) {
        super(nombre, nombreUsuario, contrasena, new Rol("ADMIN", "Administrador"));
    }

    /** Genera un PDF por cada cliente del sistema */
    public void generarRecibosDeClientes(SistemaAutenticacion sistema) {
        for (Usuario u : sistema.listarUsuarios()) {
            if (u instanceof Cliente) {
                GeneradorReciboPDF.generarPDFCliente((Cliente) u);
            }
        }
    }

    /** NUEVO → Generar reporte PDF solo del administrador */
    public void generarRecibosDelAdministrador(SistemaAutenticacion sistema) {
        GeneradorReciboPDF.generarPDFAdministrador(this, sistema.listarRecibos());
    }

    void generarRecibosDeClientes() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
