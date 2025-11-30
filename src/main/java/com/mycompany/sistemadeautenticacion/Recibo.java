package com.mycompany.sistemadeautenticacion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Recibo {

    // 👉 MÉTODO QUE FALTABA (el que pide SistemaAutenticacion)
    public static Recibo crear(String tipoMovimiento, double monto, String numeroCuenta, String clienteUsuario) {
        String id = "R" + System.currentTimeMillis();
        LocalDateTime fecha = LocalDateTime.now();
        String descripcion = tipoMovimiento + " por " + monto;

        return new Recibo(
                id,
                fecha,
                tipoMovimiento,
                monto,
                descripcion,
                clienteUsuario,
                numeroCuenta,
                "" // administrador se puede completar después si lo necesitas
        );
    }

    static Recibo crearTransferencia(CuentaBancaria origen, CuentaBancaria destino, double monto, String nombreUsuario, SistemaAutenticacion sistema) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private String id;
    private LocalDateTime fecha;
    private String tipoMovimiento;
    private double monto;
    private String descripcion;
    private String clienteUsuario; 
    private String numeroCuenta;
    private String administradorUsuario; 

    public Recibo(String id, LocalDateTime fecha, String tipoMovimiento, double monto, String descripcion,
                  String clienteUsuario, String numeroCuenta, String administradorUsuario) {
        this.id = id;
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.monto = monto;
        this.descripcion = descripcion;
        this.clienteUsuario = clienteUsuario;
        this.numeroCuenta = numeroCuenta;
        this.administradorUsuario = administradorUsuario;
    }

    // getters
    public String getIdRecibo() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public String getTipoMovimiento() { return tipoMovimiento; }
    public double getMonto() { return monto; }
    public String getDescripcion() { return descripcion; }
    public String getClienteUsuario() { return clienteUsuario; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public String getAdministradorUsuario() { return administradorUsuario; }

    public String getFechaFormateada() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fecha.format(f);
    }

    @Override
    public String toString() {
        return String.format("Recibo %s | %s | %s | %.2f", id, getFechaFormateada(), tipoMovimiento, monto);
    }

    public String toLinea() {
        return id + ";" +
               fecha.toString() + ";" +
               tipoMovimiento + ";" +
               monto + ";" +
               descripcion + ";" +
               clienteUsuario + ";" +
               numeroCuenta + ";" +
               administradorUsuario;
    }

    public static Recibo desdeLinea(String linea) {
        try {
            String[] p = linea.split(";");
            if (p.length != 8) return null;

            return new Recibo(
                p[0],
                LocalDateTime.parse(p[1]),
                p[2],
                Double.parseDouble(p[3]),
                p[4],
                p[5],
                p[6],
                p[7]
            );
        } catch (Exception e) {
            return null;
        }
    }
}
