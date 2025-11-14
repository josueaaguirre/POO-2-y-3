package com.mycompany.sistemadeautenticacion;


import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Núcleo del sistema: carga/guarda usuarios, cuentas, recibos.
 * Guarda en archivos planos en el directorio de ejecución.
 */
public class SistemaAutenticacion {
    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final Map<String, CuentaBancaria> cuentas = new HashMap<>();
    private final List<Recibo> recibos = new ArrayList<>();

    private final Path usuariosFile = Paths.get("usuarios.txt");
    private final Path cuentasFile = Paths.get("cuentas.txt");
    private final Path recibosFile = Paths.get("recibos.txt");

    private final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public SistemaAutenticacion() {
        cargarDatos();
    }

    /* ----------------- Usuarios ----------------- */
    public synchronized boolean registrarUsuario(String nombre, String username, String password, String role) {
        if (username == null || username.isBlank()) return false;
        if (usuarios.containsKey(username)) return false;
        Usuario u;
        if ("ADMIN".equalsIgnoreCase(role) || "Administrador".equalsIgnoreCase(role)) {
            u = new Administrador(nombre, username, password);
        } else {
            u = new Cliente(nombre, username, password);
        }
        usuarios.put(username, u);
        guardarUsuarios();
        return true;
    }

    public Usuario autenticar(String username, String password) {
        if (username == null) return null;
        Usuario u = usuarios.get(username);
        if (u != null && u.autenticar(password)) return u;
        return null;
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    /* ----------------- Cuentas ----------------- */
    public CuentaBancaria crearCuenta(String tipo, String propietarioUsername) {
        Usuario u = usuarios.get(propietarioUsername);
        if (!(u instanceof Cliente)) return null;
        Cliente cliente = (Cliente) u;
        String numero = String.valueOf(System.currentTimeMillis());
        // asignar admin aleatorio (si hay)
        Administrador admin = null;
        for (Usuario uu : usuarios.values()) {
            if (uu instanceof Administrador) { admin = (Administrador) uu; break; }
        }
        CuentaBancaria c = new CuentaBancaria(numero, tipo, cliente, admin);
        cuentas.put(numero, c);
        cliente.agregarCuentaLocal(c);
        if (admin != null) {
            // no tiene lista en Admin por simplicidad
        }
        guardarCuentas();
        return c;
    }

    public CuentaBancaria getCuenta(String numero) { return cuentas.get(numero); }

    /* ----------------- Recibos ----------------- */
    public void guardarRecibo(Recibo r) {
        recibos.add(r);
        // enlazar a cuenta
        CuentaBancaria c = cuentas.get(r.getNumeroCuenta());
        if (c != null) c.agregarRecibo(r);
        guardarRecibosToFile();
    }

    /* ----------------- IO ----------------- */
    private void cargarDatos() {
        cargarUsuarios();
        cargarCuentas();
        cargarRecibos();
    }

    private void cargarUsuarios() {
        if (!Files.exists(usuariosFile)) return;
        try (BufferedReader br = Files.newBufferedReader(usuariosFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                // formato: nombre|username|password|role
                String[] p = line.split("\\|", -1);
                if (p.length < 4) continue;
                String nombre = p[0], username = p[1], pass = p[2], role = p[3];
                if ("ADMIN".equalsIgnoreCase(role) || "Administrador".equalsIgnoreCase(role)) {
                    usuarios.put(username, new Administrador(nombre, username, pass));
                } else {
                    usuarios.put(username, new Cliente(nombre, username, pass));
                }
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void cargarCuentas() {
        if (!Files.exists(cuentasFile)) return;
        try (BufferedReader br = Files.newBufferedReader(cuentasFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                // formato: numero|saldo|tipo|propietarioUsername|adminUsername
                String[] p = line.split("\\|", -1);
                if (p.length < 5) continue;
                String numero = p[0];
                double saldo = Double.parseDouble(p[1]);
                String tipo = p[2];
                String propietarioUser = p[3];
                String adminUser = p[4];
                Usuario uProp = usuarios.get(propietarioUser);
                Usuario uAdmin = usuarios.get(adminUser);
                if (uProp instanceof Cliente) {
                    Cliente cliente = (Cliente) uProp;
                    Administrador admin = (uAdmin instanceof Administrador) ? (Administrador) uAdmin : null;
                    CuentaBancaria c = new CuentaBancaria(numero, tipo, cliente, admin);
                    c.depositar(saldo); // establece saldo
                    cuentas.put(numero, c);
                    cliente.agregarCuentaLocal(c);
                }
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void cargarRecibos() {
        if (!Files.exists(recibosFile)) return;
        try (BufferedReader br = Files.newBufferedReader(recibosFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                // id|fecha|tipo|monto|descripcion|clienteUser|cuentaNum|adminUser
                String[] p = line.split("\\|", -1);
                if (p.length < 8) continue;
                String id = p[0];
                LocalDateTime fecha = LocalDateTime.parse(p[1], dtf);
                String tipo = p[2];
                double monto = Double.parseDouble(p[3]);
                String descripcion = p[4];
                String clienteUser = p[5];
                String cuentaNum = p[6];
                String adminUser = p[7];
                Recibo r = new Recibo(id, fecha, tipo, monto, descripcion, clienteUser, cuentaNum, adminUser);
                recibos.add(r);
                CuentaBancaria c = cuentas.get(cuentaNum);
                if (c != null) c.agregarRecibo(r);
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void guardarUsuarios() {
        try (BufferedWriter bw = Files.newBufferedWriter(usuariosFile)) {
            for (Usuario u : usuarios.values()) {
                String role = (u instanceof Administrador) ? "ADMIN" : "CLIENTE";
                bw.write(String.join("|", u.getNombre(), u.getNombreUsuario(), u.getContrasena(), role));
                bw.newLine();
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void guardarCuentas() {
        try (BufferedWriter bw = Files.newBufferedWriter(cuentasFile)) {
            for (CuentaBancaria c : cuentas.values()) {
                String propietario = c.getPropietario() != null ? c.getPropietario().getNombreUsuario() : "";
                String admin = c.getAdministradorAsignado() != null ? c.getAdministradorAsignado().getNombreUsuario() : "";
                bw.write(String.join("|", c.getNumeroCuenta(), String.valueOf(c.getSaldo()), c.getTipo(), propietario, admin));
                bw.newLine();
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void guardarRecibosToFile() {
        try (BufferedWriter bw = Files.newBufferedWriter(recibosFile)) {
            for (Recibo r : recibos) {
                String fecha = r.getFecha().format(dtf);
                bw.write(String.join("|", r.getIdRecibo(), fecha, r.getTipoMovimiento(),
                        String.valueOf(r.getMonto()), r.getDescripcion(),
                        r.getClienteUsuario(), r.getNumeroCuenta(), r.getAdministradorUsuario() != null ? r.getAdministradorUsuario() : ""));
                bw.newLine();
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}
