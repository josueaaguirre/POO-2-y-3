package com.mycompany.sistemadeautenticacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Cliente extends Usuario implements Serializable {
    private final List<CuentaBancaria> cuentas = new ArrayList<>();

    public Cliente(String nombre, String nombreUsuario, String contrasena) {
        super(nombre, nombreUsuario, contrasena, new Rol("CLIENTE", "Cliente"));
    }

    /* cuentas */
    protected void agregarCuentaLocal(CuentaBancaria c) {
        if (c != null && !cuentas.contains(c)) cuentas.add(c);
    }

    protected void removerCuentaLocal(CuentaBancaria c) {
        cuentas.remove(c);
    }

    public List<CuentaBancaria> listarCuentas() {
        return Collections.unmodifiableList(cuentas);
    }

    public Optional<CuentaBancaria> conseguirCuentaPorNumero(String numero) {
        return cuentas.stream().filter(cc -> cc.getNumeroCuenta().equals(numero)).findFirst();
    }

    // 👉 MÉTODO NUEVO (cambio solicitado, sin modificar nada más)
    public List<String> listarNumerosDeCuenta() {
        List<String> numeros = new ArrayList<>();
        for (CuentaBancaria c : cuentas) {
            numeros.add(c.getNumeroCuenta());
        }
        return numeros;
    }

    // operaciones sencillas delegadas a CuentaBancaria
    public boolean depositar(String numeroCuenta, double monto) throws Exception {
        CuentaBancaria c = conseguirCuentaPorNumero(numeroCuenta).orElseThrow(() -> new Exception("Cuenta no encontrada"));
        c.depositar(monto);
        return true;
    }

    public boolean retirar(String numeroCuenta, double monto) throws Exception {
        CuentaBancaria c = conseguirCuentaPorNumero(numeroCuenta).orElseThrow(() -> new Exception("Cuenta no encontrada"));
        return c.retirar(monto);
    }
}
