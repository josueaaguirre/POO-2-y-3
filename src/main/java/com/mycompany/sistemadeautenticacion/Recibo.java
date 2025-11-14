package com.mycompany.sistemadeautenticacion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Recibo {
    private String id;
    private LocalDateTime fecha;
    private String tipoMovimiento;
    private double monto;
    private String descripcion;
    private String clienteUsuario; // username
    private String numeroCuenta;
    private String administradorUsuario; // username

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
}
