package com.mycompany.sistemadeautenticacion;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaAdministrador extends JFrame {
    private final SistemaAutenticacion sistema;
    private final Administrador admin;
    private final DefaultListModel<String> usuariosModel = new DefaultListModel<>();

    public VentanaAdministrador(SistemaAutenticacion sistema, Administrador admin) {
        this.sistema = sistema;
        this.admin = admin;
        setTitle("Banco Tu Amigo - Administrador: " + admin.getNombre());
        ImageIcon icono = new ImageIcon(getClass().getResource("/logo.png"));
        setIconImage(icono.getImage());
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        cargarUsuarios();
    }

    private void initUI() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0, 204, 204));

        JLabel header = new JLabel("Panel Administrador - " + admin.getNombre());
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(header, BorderLayout.NORTH);

        // LISTA DE USUARIOS
        JList<String> lista = new JList<>(usuariosModel);
        lista.setBackground(new Color(28, 28, 28));
        lista.setForeground(Color.WHITE);
        p.add(new JScrollPane(lista), BorderLayout.CENTER);

        // PANEL INFERIOR DE BOTONES (sin WrapLayout)
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botones.setBackground(new Color(0, 204, 204));

        JButton btnCrearCuenta = new JButton("Crear cuenta");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnGenerarPDF = new JButton("PDF Clientes");
        JButton btnCerrar = new JButton("Cerrar sesión");

        // Botones más pequeños
        for (JButton b : new JButton[]{btnCrearCuenta, btnRefrescar, btnGenerarPDF, btnCerrar}) {
            b.setPreferredSize(new Dimension(130, 30)); // más pequeños
            b.setBackground(new Color(255, 102, 102));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            botones.add(b);
        }

        // ACCIONES
        btnCrearCuenta.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(this, "Username del cliente:");
            String tipo = JOptionPane.showInputDialog(this, "Tipo cuenta (Ahorros):", "Ahorros");
            if (username != null && tipo != null) {
                CuentaBancaria c = sistema.crearCuenta(tipo, username);
                if (c != null) JOptionPane.showMessageDialog(this, "Cuenta creada: " + c.getNumeroCuenta());
                else JOptionPane.showMessageDialog(this, "Error creando la cuenta.");
                cargarUsuarios();
            }
        });

        btnRefrescar.addActionListener(e -> cargarUsuarios());

        btnGenerarPDF.addActionListener(e -> {
            try {
                admin.generarRecibosDeClientes(sistema);
                JOptionPane.showMessageDialog(this, "PDFs generados.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnCerrar.addActionListener(e -> {
            dispose();
            new VentanaLogin(sistema).setVisible(true);
        });

        p.add(botones, BorderLayout.SOUTH);
        add(p);
    }

    private void cargarUsuarios() {
        usuariosModel.clear();
        List<Usuario> lista = sistema.listarUsuarios();
        for (Usuario u : lista) {
            usuariosModel.addElement(u.toString());
        }
    }
}
