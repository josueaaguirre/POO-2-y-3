package com.mycompany.sistemadeautenticacion;

import java.io.*;
import java.util.*;

public class SistemaAutenticacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final Map<String, CuentaBancaria> cuentas = new HashMap<>();
    private final List<Recibo> recibos = new ArrayList<>();

    private static final String ARCHIVO_USUARIOS_TXT = "usuarios.txt";
    private static final String ARCHIVO_USUARIOS = "usuarios.dat";
    private static final String ARCHIVO_CUENTAS = "cuentas.dat";
    private static final String ARCHIVO_RECIBOS = "recibos.txt";

    public SistemaAutenticacion() {
        cargarUsuariosDesdeTXT();
        cargarCuentas();
        cargarRecibos();
        sincronizarCuentasConClientes();
        restaurarCuentasEnClientes();
    }

    /* =======================
          USUARIOS
       ======================= */

    public void registrarUsuario(Usuario u) {
        usuarios.put(u.getNombreUsuario(), u);
        guardarUsuariosEnTXT();
    }

    public boolean registrarUsuario(String nombre, String nombreUsuario, String contrasena, String rol) {
        if (usuarios.containsKey(nombreUsuario)) return false;

        Usuario u;
        if ("CLIENTE".equalsIgnoreCase(rol)) {
            u = new Cliente(nombre, nombreUsuario, contrasena);
        } else if ("ADMIN".equalsIgnoreCase(rol)) {
            u = new Administrador(nombre, nombreUsuario, contrasena);
        } else {
            return false;
        }

        registrarUsuario(u);
        return true;
    }

    public Usuario autenticar(String user, String pass) {
        Usuario u = usuarios.get(user);
        if (u != null && u.getContrasena().equals(pass)) return u;
        return null;
    }

    /* =======================
          TXT: GUARDAR / CARGAR
       ======================= */

    private void guardarUsuariosEnTXT() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_USUARIOS_TXT))) {
            for (Usuario u : usuarios.values()) {
                pw.println(
                        u.getNombre() + "|" +
                        u.getNombreUsuario() + "|" +
                        u.getContrasena() + "|" +
                        u.getRol().getNombre()
                );
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarUsuariosDesdeTXT() {
        File f = new File(ARCHIVO_USUARIOS_TXT);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split("\\|");
                if (p.length != 4) continue;

                String nombre = p[0];
                String user = p[1];
                String pass = p[2];
                String rolT = p[3];

                Usuario u = rolT.equalsIgnoreCase("ADMIN")
                        ? new Administrador(nombre, user, pass)
                        : new Cliente(nombre, user, pass);

                usuarios.put(user, u);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /* =======================
          CUENTAS
       ======================= */

    public void registrarCuenta(CuentaBancaria c) {
        cuentas.put(c.getNumeroCuenta(), c);
        guardarCuentas();
    }

    public Optional<CuentaBancaria> buscarCuenta(String numero) {
        return Optional.ofNullable(cuentas.get(numero));
    }

    public CuentaBancaria crearCuenta(String numeroCuenta, String tipo, Cliente propietario, Administrador admin) {
        CuentaBancaria c = new CuentaBancaria(numeroCuenta, tipo, propietario, admin);
        registrarCuenta(c);
        if (propietario != null) propietario.agregarCuentaLocal(c);
        return c;
    }

    public CuentaBancaria crearCuenta(String numeroCuenta, String tipo) {
        CuentaBancaria c = new CuentaBancaria(numeroCuenta, tipo, null, null);
        registrarCuenta(c);
        return c;
    }

    public String generarNumeroCuentaUnico() {
        String num;
        do {
            num = String.valueOf(10000000 + new Random().nextInt(90000000));
        } while (cuentas.containsKey(num));
        return num;
    }

    public CuentaBancaria getCuenta(String numeroCuenta) {
        return cuentas.get(numeroCuenta);
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    private void guardarCuentas() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CUENTAS))) {
            out.writeObject(cuentas);
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** 🔥 SOLO CAMBIO NECESARIO 🔥 **/
    @SuppressWarnings("unchecked")
    private void cargarCuentas() {
        File f = new File(ARCHIVO_CUENTAS);
        if (!f.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {

            Map<String, CuentaBancaria> map = (Map<String, CuentaBancaria>) in.readObject();
            cuentas.clear();
            cuentas.putAll(map);

            for (CuentaBancaria c : cuentas.values()) {

                if (c.getPropietario() != null) {

                    Usuario u = usuarios.get(c.getPropietario().getNombreUsuario());

                    if (u instanceof Cliente) {
                        Cliente cli = (Cliente) u;
                        c.setPropietario(cli);
                        cli.agregarCuentaLocal(c);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* =======================
          SINCRONIZACIÓN
       ======================= */
    public void sincronizarCuentasConClientes() {
        for (CuentaBancaria c : cuentas.values()) {
            if (c.getPropietario() instanceof Cliente) {
                Cliente cli = (Cliente) c.getPropietario();
                cli.agregarCuentaLocal(c);
            }
        }
    }

    /* =======================
          RESTAURAR EN CLIENTES
       ======================= */
    private void restaurarCuentasEnClientes() {

        for (Usuario u : usuarios.values()) {

            if (u instanceof Cliente) {
                Cliente cli = (Cliente) u;

                List<CuentaBancaria> propias = new ArrayList<>();

                for (CuentaBancaria c : cuentas.values()) {
                    if (c.getPropietario() == cli) {
                        propias.add(c);
                    }
                }

                for (CuentaBancaria c : propias) {
                    cli.agregarCuentaLocal(c);
                }
            }
        }
    }

    /* =======================
          RECIBOS
       ======================= */

    public void guardarRecibo(Recibo r) {
        recibos.add(r);

        CuentaBancaria c = cuentas.get(r.getNumeroCuenta());
        if (c != null) c.agregarRecibo(r);

        guardarRecibosToFile();
        GeneradorReciboPDF.generar(r);
    }

    public List<Recibo> listarRecibos() {
        return Collections.unmodifiableList(recibos);
    }

    private void cargarRecibos() {
        File f = new File(ARCHIVO_RECIBOS);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                Recibo r = Recibo.desdeLinea(linea);
                if (r != null) recibos.add(r);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void guardarRecibosToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_RECIBOS))) {
            for (Recibo r : recibos) pw.println(r.toLinea());
        } catch (Exception e) { e.printStackTrace(); }
    }

    /* =======================
          TRANSFERENCIAS
       ======================= */

    public boolean transferir(String desde, String hacia, double monto) {
        CuentaBancaria c1 = cuentas.get(desde);
        CuentaBancaria c2 = cuentas.get(hacia);

        if (c1 == null || c2 == null) return false;
        if (monto <= 0) return false;
        if (c1.getSaldo() < monto) return false;

        c1.retirar(monto);
        c2.depositar(monto);

        String prop1 = c1.getPropietario() != null ? c1.getPropietario().getNombreUsuario() : "";
        String prop2 = c2.getPropietario() != null ? c2.getPropietario().getNombreUsuario() : "";

        Recibo r1 = Recibo.crear("TRANSFERENCIA ENVIADA", monto, desde, prop1);
        Recibo r2 = Recibo.crear("TRANSFERENCIA RECIBIDA", monto, hacia, prop2);

        guardarRecibo(r1);
        guardarRecibo(r2);

        guardarCuentas();
        return true;
    }

    /* =======================
          PDF
       ======================= */

    public void generarPDFporCliente(Administrador admin) {
        for (Usuario u : usuarios.values()) {
            if (u instanceof Cliente) {
                Cliente cli = (Cliente) u;
                GeneradorReciboPDF.generarPDFCliente(cli);
            }
        }
    }

    public void generarPDFglobal() {
        GeneradorReciboPDF.generarPDFGlobal(recibos);
    }
    
/* =======================
      OBTENER TODOS LOS RECIBOS
   ======================= */
public List<Recibo> getTodosLosRecibos() {
    return new ArrayList<>(recibos);
}

    public CuentaBancaria buscarCuentaGlobal(String cuentaDestino) {
        return cuentas.get(cuentaDestino);
    }
}
