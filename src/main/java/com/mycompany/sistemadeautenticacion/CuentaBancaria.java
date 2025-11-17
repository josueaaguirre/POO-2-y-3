package com.mycompany.sistemadeautenticacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CuentaBancaria implements Serializable {

    private static final long serialVersionUID = 1L;

    private String numeroCuenta;
    private double saldo;
    private String tipo;
    private Cliente propietario;
    private Administrador administradorAsignado;
    private final List<Recibo> recibos = new ArrayList<>();

    public CuentaBancaria(String numeroCuenta, String tipo, Cliente propietario, Administrador administradorAsignado) {
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
        this.propietario = propietario;
        this.administradorAsignado = administradorAsignado;
        this.saldo = 0.0;
    }

    // getters
    public String getNumeroCuenta() { return numeroCuenta; }
    public double getSaldo() { return saldo; }
    public String getTipo() { return tipo; }
    public Cliente getPropietario() { return propietario; }
    public Administrador getAdministradorAsignado() { return administradorAsignado; }

    // 👉 MÉTODO NUEVO — NO modifica nada existente
    public String getResumenCuenta() {
        return numeroCuenta + " | " + tipo + " | Saldo: " + saldo;
    }

    // operaciones
    public void depositar(double monto) {
        if (monto <= 0) throw new IllegalArgumentException("Monto debe ser > 0");
        saldo += monto;
    }

    public boolean retirar(double monto) {
        if (monto <= 0) return false;
        if (saldo >= monto) {
            saldo -= monto;
            return true;
        }
        return false;
    }

    public void agregarRecibo(Recibo r) {
        if (r != null) recibos.add(r);
    }

    public List<Recibo> getRecibos() { return Collections.unmodifiableList(recibos); }

    @Override
    public String toString() {
        return String.format("%s | %s | Saldo: %.2f", numeroCuenta, tipo, saldo);
    }

    public String toLinea() {
        String prop = propietario != null ? propietario.getNombreUsuario() : "";
        String admin = administradorAsignado != null ? administradorAsignado.getNombreUsuario() : "";
        return numeroCuenta + ";" + tipo + ";" + saldo + ";" + prop + ";" + admin;
    }

    public static CuentaBancaria desdeLinea(String linea, SistemaAutenticacion sistema) {
        try {
            String[] p = linea.split(";");
            String numero = p[0];
            String tipo = p[1];
            double saldo = Double.parseDouble(p[2]);

            CuentaBancaria c = new CuentaBancaria(numero, tipo, null, null);
            c.saldo = saldo;
            return c;
        } catch (Exception e) {
            return null;
        }
    }
}
