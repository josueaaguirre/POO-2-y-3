package com.mycompany.sistemadeautenticacion;


import javax.swing.SwingUtilities;

public class VentanaPrincipal {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaLogin login = new VentanaLogin(new SistemaAutenticacion());
            login.setVisible(true);
        });
    }
}
