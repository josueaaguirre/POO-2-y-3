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

        // ← LÍNEA CLAVE: sincroniza cuentas dentro de cada Cliente
        sincronizarCuentasConClientes();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                Usuario u;

                if (rolT.equalsIgnoreCase("ADMIN")) {
                    u = new Administrador(nombre, user, pass);
                } else {
                    u = new Cliente(nombre, user, pass);
                }

                usuarios.put(user, u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @SuppressWarnings("unchecked")
    private void cargarCuentas() {
        File f = new File(ARCHIVO_CUENTAS);
        if (!f.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            Map<String, CuentaBancaria> map = (Map<String, CuentaBancaria>) in.readObject();
            cuentas.clear();
            cuentas.putAll(map);

            // ✔️ restaurar vínculo con el cliente (si ya viene en el objeto cuenta)
            for (CuentaBancaria c : cuentas.values()) {
                if (c.getPropietario() != null) {
                    c.getPropietario().agregarCuentaLocal(c);
                }
            }

        } catch (Exception e) { e.printStackTrace(); }
    }

    /* =======================
          SINCRONIZACIÓN REAL
       ======================= */

    // Método añadido: sincroniza las cuentas del mapa 'cuentas' con la lista interna de cada Cliente
    public void sincronizarCuentasConClientes() {
        for (CuentaBancaria c : cuentas.values()) {
            Usuario u = c.getPropietario();
            if (u instanceof Cliente) {
                ((Cliente) u).agregarCuentaLocal(c);
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
                try {
                    Recibo r = Recibo.desdeLinea(linea);
                    if (r != null) recibos.add(r);
                } catch (Exception e) {}
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void guardarRecibosToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_RECIBOS))) {
            for (Recibo r : recibos) {
                pw.println(r.toLinea());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
