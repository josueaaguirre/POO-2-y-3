package com.mycompany.sistemadeautenticacion;

import com.mycompany.sistemadeautenticacion.SistemaAutenticacion;
import javax.swing.*;
import java.awt.*;

public class VentanaLogin extends JFrame {
    private final SistemaAutenticacion sistema;

    public VentanaLogin(SistemaAutenticacion sistema) {
        this.sistema = sistema;
        setTitle("Nequi - Iniciar / Registrar");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {

        // PANEL PRINCIPAL — azul agua marina
        JPanel p = new JPanel();
        p.setBackground(new Color(0, 204, 204));  // AQUA MARINA
        p.setLayout(null);

        JLabel lblTitle = new JLabel("Bienvenido - Registro / Ingreso");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(40, 10, 340, 30);
        lblTitle.setFont(lblTitle.getFont().deriveFont(16f));
        p.add(lblTitle);

        // BOTONES — rojo suave
        JButton btnRegistro = new JButton("Registrar");
        btnRegistro.setBounds(40, 60, 150, 30);
        styleButtonRed(btnRegistro);
        btnRegistro.addActionListener(e -> abrirRegistro());
        p.add(btnRegistro);

        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(220, 60, 150, 30);
        styleButtonRed(btnLogin);
        btnLogin.addActionListener(e -> abrirLogin());
        p.add(btnLogin);

        add(p);
    }

    // *** NUEVO estilo: botón rojo suave ***
    private void styleButtonRed(JButton b) {
        b.setBackground(new Color(255, 102, 102)); // rojo suave
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
    }

    private void abrirRegistro() {
        JTextField nombreField = new JTextField();
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        String[] roles = {"CLIENTE", "ADMIN"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        Object[] inputs = {
                "Nombre real:", nombreField,
                "Username:", userField,
                "Contraseña:", passField,
                "Rol:", roleBox
        };

        int res = JOptionPane.showConfirmDialog(this, inputs,
                "Registrar usuario", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            String nombre = nombreField.getText();
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            String role = (String) roleBox.getSelectedItem();
            boolean ok = sistema.registrarUsuario(nombre, user, pass, role);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Registrado con éxito. Iniciando sesión...");
                Usuario u = sistema.autenticar(user, pass);
                abrirVentanaPorRol(u);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error: usuario ya existe o datos inválidos.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirLogin() {
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        Object[] inputs = {"Username:", userField, "Contraseña:", passField};

        int res = JOptionPane.showConfirmDialog(this, inputs,
                "Iniciar Sesión", JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            Usuario u = sistema.autenticar(user, pass);
            if (u != null) {
                JOptionPane.showMessageDialog(this,
                        "Bienvenido, " + u.getNombre());
                abrirVentanaPorRol(u);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Credenciales incorrectas.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirVentanaPorRol(Usuario u) {
        if (u == null) return;

        if (u instanceof Administrador) {
            VentanaAdministrador va =
                    new VentanaAdministrador(sistema, (Administrador) u);
            va.setVisible(true);
        } else if (u instanceof Cliente) {
            VentanaCliente vc =
                    new VentanaCliente(sistema, (Cliente) u);
            vc.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Rol no soportado.");
        }
    }
}
