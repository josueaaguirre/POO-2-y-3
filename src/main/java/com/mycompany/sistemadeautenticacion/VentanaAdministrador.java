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
        setTitle("Nequi - Administrador: " + admin.getNombre());
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        cargarUsuarios();
    }

    private void initUI() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20,20,20));
        JLabel header = new JLabel("Panel Administrador - " + admin.getNombre());
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        p.add(header, BorderLayout.NORTH);

        JList<String> lista = new JList<>(usuariosModel);
        lista.setBackground(new Color(28,28,28));
        lista.setForeground(Color.WHITE);
        p.add(new JScrollPane(lista), BorderLayout.CENTER);

        JPanel botones = new JPanel();
        botones.setBackground(new Color(20,20,20));
        JButton btnCrearCuenta = new JButton("Crear cuenta para cliente");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnCerrar = new JButton("Cerrar sesión");
        for (JButton b : new JButton[]{btnCrearCuenta, btnRefrescar, btnCerrar}) {
            b.setBackground(new Color(40,40,40));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            botones.add(b);
        }

        btnCrearCuenta.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(this, "Username del cliente:");
            String tipo = JOptionPane.showInputDialog(this, "Tipo cuenta (Ahorros):", "Ahorros");
            if (username != null && tipo != null) {
                CuentaBancaria c = sistema.crearCuenta(tipo, username);
                if (c != null) JOptionPane.showMessageDialog(this, "Cuenta creada: " + c.getNumeroCuenta());
                else JOptionPane.showMessageDialog(this, "No se pudo crear la cuenta (usuario no existe o no es cliente).");
                cargarUsuarios();
            }
        });

        btnRefrescar.addActionListener(e -> cargarUsuarios());

        btnCerrar.addActionListener(e -> {
            dispose();
            VentanaLogin v = new VentanaLogin(sistema);
            v.setVisible(true);
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
