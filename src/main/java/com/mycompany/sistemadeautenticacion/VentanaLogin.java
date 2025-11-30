package com.mycompany.sistemadeautenticacion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class VentanaLogin extends JFrame {

    private final SistemaAutenticacion sistema;

    public VentanaLogin(SistemaAutenticacion sistema) {
        this.sistema = sistema;

        setTitle("Banco Tu Amigo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        ImageIcon icono = new ImageIcon(getClass().getResource("/logo.png"));
        setIconImage(icono.getImage());

        initUI();
    }

    private void initUI() {

        JPanel p = new JPanel();
        p.setBackground(new Color(0, 180, 200));
        p.setLayout(null);

        JLabel titulo = new JLabel("Bienvenido a Banco Tu Amigo");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setBounds(70, 20, 320, 30);
        p.add(titulo);

        JButton btnRegistro = crearBoton("Registrar", 60);
        btnRegistro.addActionListener(e -> abrirVentanaRegistro());
        p.add(btnRegistro);

        JButton btnLogin = crearBoton("Iniciar Sesión", 120);
        btnLogin.addActionListener(e -> abrirVentanaLogin());
        p.add(btnLogin);

        // ===========================================
        // ✔ CORRECCIÓN: PDF GLOBAL
        // ===========================================
        JButton btnPDFGlobal = crearBoton("PDF Global", 180);
        btnPDFGlobal.addActionListener(e -> {
            try {
                // ✔ Se envía la lista de recibos, NO el sistema
                GeneradorReciboPDF.generarPDFGlobal(
                        sistema.getTodosLosRecibos()
                );
                JOptionPane.showMessageDialog(this, "PDF global generado con éxito.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generando PDF global: " + ex.getMessage());
            }
        });
        p.add(btnPDFGlobal);
        // ===========================================

        add(p);
    }

    private JButton crearBoton(String texto, int y) {
        JButton b = new JButton(texto);
        b.setBounds(125, y, 200, 40);
        b.setBackground(new Color(255, 90, 90));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 16));
        b.setBorder(BorderFactory.createEmptyBorder());
        return b;
    }

    private void abrirVentanaRegistro() {

        JFrame r = new JFrame("Registrar Usuario");
        r.setSize(460, 380);
        r.setLocationRelativeTo(null);
        r.setResizable(false);
        r.setLayout(null);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(240, 240, 240));
        panel.setBounds(0, 0, 460, 380);

        JLabel img = new JLabel();
        img.setBounds(20, 20, 100, 100);
        panel.add(img);

        JTextField nombre = crearCampo(150, 20);
        JTextField user = crearCampo(150, 80);
        JPasswordField pass = new JPasswordField();
        pass.setBounds(150, 140, 250, 30);

        JComboBox<String> rol = new JComboBox<>(new String[]{"CLIENTE", "ADMIN"});
        rol.setBounds(150, 200, 250, 30);

        cargarImagenUsuario(rol, img);
        rol.addActionListener(e -> cargarImagenUsuario(rol, img));

        panel.add(label("Nombre real", 150, 0));
        panel.add(nombre);

        panel.add(label("Username", 150, 60));
        panel.add(user);

        panel.add(label("Contraseña", 150, 120));
        panel.add(pass);

        panel.add(label("Rol", 150, 180));
        panel.add(rol);

        JLabel mensaje = new JLabel("");
        mensaje.setForeground(Color.RED);
        mensaje.setBounds(150, 240, 250, 20);
        panel.add(mensaje);

        JButton btn = crearBotonFormulario("Registrar", 150, 270);
        panel.add(btn);

        btn.addActionListener(e -> {
            String n = nombre.getText().trim();
            String u = user.getText().trim();
            String p = new String(pass.getPassword()).trim();
            String ro = (String) rol.getSelectedItem();

            if (n.isEmpty() || u.isEmpty() || p.isEmpty()) {
                mensaje.setText("Todos los campos son obligatorios.");
                return;
            }

            if (u.contains(" ")) {
                mensaje.setText("El username no puede tener espacios.");
                return;
            }

            if (p.length() < 4) {
                mensaje.setText("Contraseña demasiado corta.");
                return;
            }

            boolean ok = sistema.registrarUsuario(n, u, p, ro);
            if (!ok) {
                mensaje.setText("Usuario ya existe.");
                return;
            }

            Usuario obj = sistema.autenticar(u, p);
            r.dispose();
            abrirVentanaPorRol(obj);
            dispose();
        });

        r.add(panel);
        r.setVisible(true);
    }

    private void abrirVentanaLogin() {

        JFrame r = new JFrame("Iniciar Sesión");
        r.setSize(400, 280);
        r.setLocationRelativeTo(null);
        r.setResizable(false);
        r.setLayout(null);

        JPanel p = new JPanel(null);
        p.setBackground(new Color(245, 245, 245));
        p.setBounds(0, 0, 400, 280);

        JTextField user = crearCampo(100, 40);
        JPasswordField pass = new JPasswordField();
        pass.setBounds(100, 110, 200, 30);

        JLabel mensaje = new JLabel("");
        mensaje.setForeground(Color.RED);
        mensaje.setBounds(100, 150, 200, 20);

        p.add(label("Usuario", 100, 20));
        p.add(user);

        p.add(label("Contraseña", 100, 90));
        p.add(pass);

        JButton btn = crearBotonFormulario("Entrar", 100, 180);
        p.add(btn);

        btn.addActionListener(e -> {
            String u = user.getText().trim();
            String pa = new String(pass.getPassword());

            Usuario obj = sistema.autenticar(u, pa);
            if (obj == null) {
                mensaje.setText("Usuario o contraseña incorrectos.");
                return;
            }

            r.dispose();
            abrirVentanaPorRol(obj);
            dispose();
        });

        r.add(p);
        r.setVisible(true);
    }

    private JLabel label(String t, int x, int y) {
        JLabel l = new JLabel(t);
        l.setBounds(x, y, 200, 20);
        return l;
    }

    private JTextField crearCampo(int x, int y) {
        JTextField t = new JTextField();
        t.setBounds(x, y, 250, 30);
        return t;
    }

    private JButton crearBotonFormulario(String txt, int x, int y) {
        JButton b = new JButton(txt);
        b.setBounds(x, y, 200, 35);
        b.setBackground(new Color(255, 100, 100));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder());
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 15));
        return b;
    }

    private void cargarImagenUsuario(JComboBox<String> rol, JLabel img) {
        try {
            String archivo = rol.getSelectedItem().equals("CLIENTE")
                    ? "/cliente.png"
                    : "/admin.png";

            ImageIcon icono = new ImageIcon(getClass().getResource(archivo));
            Image esc = icono.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            img.setIcon(new ImageIcon(esc));
        } catch (Exception e) {
            img.setText("Sin imagen");
        }
    }

    private void abrirVentanaPorRol(Usuario u) {
        if (u instanceof Administrador) {
            new VentanaAdministrador(sistema, (Administrador) u).setVisible(true);
        } else if (u instanceof Cliente) {
            new VentanaCliente(sistema, (Cliente) u).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Rol desconocido.");
        }
    }
}
