package com.mycompany.sistemadeautenticacion;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;
import java.time.LocalDateTime;

public class VentanaCliente extends JFrame {
    private final SistemaAutenticacion sistema;
    private final Cliente cliente;
    private final DefaultListModel<String> cuentasModel = new DefaultListModel<>();

    public VentanaCliente(SistemaAutenticacion sistema, Cliente cliente) {
        this.sistema = sistema;
        this.cliente = cliente;
        setTitle("Banco Tu Amigo - Cliente: " + cliente.getNombre());
        ImageIcon icono = new ImageIcon(getClass().getResource("/logo.png"));
        setIconImage(icono.getImage());
        setSize(600, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();

        // 🟢 SOLUCIÓN REAL: sincronizar antes de cargar
        sistema.sincronizarCuentasConClientes();

        cargarCuentas();
    }

    private void initUI() {

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0, 204, 204)); 

        JLabel header = new JLabel("Cuenta de " + cliente.getNombre());
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));
        p.add(header, BorderLayout.NORTH);

        JList<String> lista = new JList<>(cuentasModel);
        lista.setBackground(new Color(0, 170, 170));
        lista.setForeground(Color.WHITE);
        lista.setSelectionBackground(new Color(255, 102, 102));
        lista.setSelectionForeground(Color.WHITE);
        JScrollPane sp = new JScrollPane(lista);
        sp.getViewport().setBackground(new Color(0, 170, 170));
        p.add(sp, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        botones.setBackground(new Color(0, 204, 204));

        JButton btnAbrir = new JButton("Abrir cuenta");
        JButton btnDepositar = new JButton("Depositar");
        JButton btnRetirar = new JButton("Retirar");
        JButton btnTransferir = new JButton("Transferir");
        JButton btnRecibo = new JButton("Generar Recibo PDF");
        JButton btnCerrar = new JButton("Cerrar sesión");

        for (JButton b : new JButton[]{btnAbrir, btnDepositar, btnRetirar, btnTransferir, btnRecibo, btnCerrar}) {
            b.setBackground(new Color(255, 102, 102));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            botones.add(b);
        }

        // ============================================================
        // BOTÓN ABRIR CUENTA (sin cambios)
        // ============================================================
        btnAbrir.addActionListener(e -> {
            String tipo = JOptionPane.showInputDialog(this, "Tipo de cuenta (ej: Ahorros):", "Ahorros");
            if (tipo != null && !tipo.isBlank()) {

                String numeroGenerado = UUID.randomUUID().toString().substring(0, 8);

                CuentaBancaria c = sistema.crearCuenta(
                        numeroGenerado,
                        tipo,
                        cliente,
                        null
                );

                if (c != null) {
                    JOptionPane.showMessageDialog(this, "Cuenta creada: " + c.getNumeroCuenta());

                    // sincronizar después de crear
                    sistema.sincronizarCuentasConClientes();

                    cargarCuentas();
                } else {
                    JOptionPane.showMessageDialog(this, "Error creando cuenta.");
                }
            }
        });

        // ===================================================================
        // (todo el resto igual… SIN UN SOLO CAMBIO MÁS)
        // ===================================================================

        btnDepositar.addActionListener(e -> {
            String numero = JOptionPane.showInputDialog(this, "Número de cuenta:");
            String m = JOptionPane.showInputDialog(this, "Monto a depositar:");
            try {
                double monto = Double.parseDouble(m);
                CuentaBancaria cuenta = sistema.getCuenta(numero);

                if (cuenta != null && cuenta.getPropietario().getNombreUsuario().equals(cliente.getNombreUsuario())) {
                    cuenta.depositar(monto);

                    Recibo r = new Recibo(UUID.randomUUID().toString(), LocalDateTime.now(), "Depósito", monto,
                            "Depósito realizado", cliente.getNombreUsuario(), numero,
                            cuenta.getAdministradorAsignado() != null ? cuenta.getAdministradorAsignado().getNombreUsuario() : "");
                    sistema.guardarRecibo(r);
                    GeneradorReciboPDF.generar(r);

                    JOptionPane.showMessageDialog(this, "Depósito OK");

                    sistema.sincronizarCuentasConClientes();
                    cargarCuentas();
                } else {
                    JOptionPane.showMessageDialog(this, "Cuenta no válida o no te pertenece.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Monto inválido.");
            }
        });

        btnRetirar.addActionListener(e -> {
            String numero = JOptionPane.showInputDialog(this, "Número de cuenta:");
            String m = JOptionPane.showInputDialog(this, "Monto a retirar:");
            try {
                double monto = Double.parseDouble(m);
                CuentaBancaria cuenta = sistema.getCuenta(numero);

                if (cuenta != null && cuenta.getPropietario().getNombreUsuario().equals(cliente.getNombreUsuario())) {
                    if (cuenta.retirar(monto)) {

                        Recibo r = new Recibo(UUID.randomUUID().toString(), LocalDateTime.now(), "Retiro", monto,
                                "Retiro realizado", cliente.getNombreUsuario(), numero,
                                cuenta.getAdministradorAsignado() != null ? cuenta.getAdministradorAsignado().getNombreUsuario() : "");
                        sistema.guardarRecibo(r);
                        GeneradorReciboPDF.generar(r);

                        JOptionPane.showMessageDialog(this, "Retiro OK");

                        sistema.sincronizarCuentasConClientes();
                        cargarCuentas();
                    } else {
                        JOptionPane.showMessageDialog(this, "Saldo insuficiente.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Cuenta no válida o no te pertenece.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Monto inválido.");
            }
        });

        btnTransferir.addActionListener(e -> {
            String origen = JOptionPane.showInputDialog(this, "Cuenta origen:");
            String destino = JOptionPane.showInputDialog(this, "Cuenta destino:");
            String m = JOptionPane.showInputDialog(this, "Monto a transferir:");
            try {
                double monto = Double.parseDouble(m);
                CuentaBancaria cOrigen = sistema.getCuenta(origen);
                CuentaBancaria cDestino = sistema.getCuenta(destino);

                if (cOrigen == null || cDestino == null) {
                    JOptionPane.showMessageDialog(this, "Cuenta no encontrada.");
                    return;
                }
                if (!cOrigen.getPropietario().getNombreUsuario().equals(cliente.getNombreUsuario())) {
                    JOptionPane.showMessageDialog(this, "La cuenta origen no es tuya.");
                    return;
                }

                if (cOrigen.retirar(monto)) {
                    cDestino.depositar(monto);

                    Recibo r1 = new Recibo(UUID.randomUUID().toString(), LocalDateTime.now(), "TransferenciaSalida", monto,
                            "Transferencia enviada", cliente.getNombreUsuario(), origen,
                            cOrigen.getAdministradorAsignado() != null ? cOrigen.getAdministradorAsignado().getNombreUsuario() : "");

                    Recibo r2 = new Recibo(UUID.randomUUID().toString(), LocalDateTime.now(), "TransferenciaEntrada", monto,
                            "Transferencia recibida", cDestino.getPropietario().getNombreUsuario(), destino,
                            cDestino.getAdministradorAsignado() != null ? cDestino.getAdministradorAsignado().getNombreUsuario() : "");

                    sistema.guardarRecibo(r1);
                    sistema.guardarRecibo(r2);
                    GeneradorReciboPDF.generar(r1);
                    GeneradorReciboPDF.generar(r2);

                    JOptionPane.showMessageDialog(this, "Transferencia realizada.");

                    sistema.sincronizarCuentasConClientes();
                    cargarCuentas();
                } else {
                    JOptionPane.showMessageDialog(this, "Saldo insuficiente.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnRecibo.addActionListener(e -> {
            String numero = JOptionPane.showInputDialog(this, "Número de cuenta para generar resumen:");
            CuentaBancaria c = sistema.getCuenta(numero);

            if (c != null && c.getPropietario().getNombreUsuario().equals(cliente.getNombreUsuario())) {
                Recibo r = new Recibo(UUID.randomUUID().toString(), LocalDateTime.now(), "Resumen", c.getSaldo(),
                        "Resumen de cuenta", cliente.getNombreUsuario(), numero,
                        c.getAdministradorAsignado() != null ? c.getAdministradorAsignado().getNombreUsuario() : "");
                sistema.guardarRecibo(r);
                GeneradorReciboPDF.generar(r);

                JOptionPane.showMessageDialog(this, "Recibo PDF generado.");
            } else {
                JOptionPane.showMessageDialog(this, "Cuenta no válida.");
            }
        });

        btnCerrar.addActionListener(e -> {
            dispose();
            VentanaLogin login = new VentanaLogin(sistema);
            login.setVisible(true);
        });

        p.add(botones, BorderLayout.SOUTH);
        add(p);
    }

    private void cargarCuentas() {
        cuentasModel.clear();
        for (CuentaBancaria c : cliente.listarCuentas()) {
            cuentasModel.addElement(
                    c.getNumeroCuenta() + " - " + c.getTipo() + " - S/ " + String.format("%.2f", c.getSaldo())
            );
        }
    }
}
