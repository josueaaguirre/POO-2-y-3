package com.mycompany.sistemadeautenticacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Cliente extends Usuario implements Serializable {
    
    private List<CuentaBancaria> cuentas = new ArrayList<>();

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

    public List<CuentaBancaria> getCuentas() {
        return listarCuentas();
    }

    // 👉 NUEVO: NECESARIO PARA RESTAURAR LAS CUENTAS CORRECTAS AL INICIAR SESIÓN
    public void setCuentas(List<CuentaBancaria> cuentasRestauradas) {
        this.cuentas = cuentasRestauradas;
    }

    public Optional<CuentaBancaria> conseguirCuentaPorNumero(String numero) {
        return cuentas.stream().filter(cc -> cc.getNumeroCuenta().equals(numero)).findFirst();
    }

    public List<String> listarNumerosDeCuenta() {
        List<String> numeros = new ArrayList<>();
        for (CuentaBancaria c : cuentas) numeros.add(c.getNumeroCuenta());
        return numeros;
    }

    // operaciones
    public boolean depositar(String numeroCuenta, double monto) throws Exception {
        CuentaBancaria c = conseguirCuentaPorNumero(numeroCuenta)
                .orElseThrow(() -> new Exception("Cuenta no encontrada"));
        c.depositar(monto);
        return true;
    }

    public boolean retirar(String numeroCuenta, double monto) throws Exception {
        CuentaBancaria c = conseguirCuentaPorNumero(numeroCuenta)
                .orElseThrow(() -> new Exception("Cuenta no encontrada"));
        return c.retirar(monto);
    }

    // transferencias
    public boolean transferirA(String miCuenta, String cuentaDestino, double monto, SistemaAutenticacion sistema) throws Exception {

        CuentaBancaria origen = conseguirCuentaPorNumero(miCuenta)
                .orElseThrow(() -> new Exception("Cuenta origen no encontrada"));

        CuentaBancaria destino = sistema.buscarCuentaGlobal(cuentaDestino);
        if (destino == null) throw new Exception("Cuenta destino no encontrada");

        boolean ok = origen.transferirA(destino, monto);
        if (!ok) throw new Exception("Saldo insuficiente");

        Recibo r1 = Recibo.crearTransferencia(origen, destino, monto, this.getNombreUsuario(), sistema);
        Recibo r2 = Recibo.crearTransferencia(destino, origen, monto, this.getNombreUsuario(), sistema);

        origen.agregarRecibo(r1);
        destino.agregarRecibo(r2);

        return true;
    }
}
