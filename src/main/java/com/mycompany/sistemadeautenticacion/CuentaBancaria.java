package com.mycompany.sistemadeautenticacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CuentaBancaria implements Serializable {
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
}
