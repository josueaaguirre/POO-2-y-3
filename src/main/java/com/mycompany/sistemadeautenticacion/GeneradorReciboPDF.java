package com.mycompany.sistemadeautenticacion;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class GeneradorReciboPDF {

    private static final String DIR = "recibos";
    private static final String LOGO_PATH = "src/main/resources/logo.png";

    /* ===========================================================
       PDF INDIVIDUAL (RECIBO)
       =========================================================== */
    public static void generar(Recibo r) {
        if (r == null) return;

        try {
            File dir = new File(DIR);
            if (!dir.exists()) dir.mkdirs();

            String safeName = r.getClienteUsuario() != null
                    ? r.getClienteUsuario().replaceAll("[^a-zA-Z0-9_-]", "_")
                    : "usuario";

            String fileName = DIR + File.separator + "recibo_" + safeName + "_" + r.getIdRecibo() + ".pdf";

            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileName));
            doc.open();

            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

            // ENCABEZADO
            PdfPTable encabezado = new PdfPTable(2);
            encabezado.setWidthPercentage(100);
            encabezado.setWidths(new float[]{1, 4});

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);

            try {
                Image logo = Image.getInstance(LOGO_PATH);
                logo.scaleToFit(70, 70);
                logoCell.addElement(logo);
            } catch (Exception e) {
                logoCell.addElement(getLogoGenerado(0x3366FF));
            }

            encabezado.addCell(logoCell);

            PdfPCell titulo = new PdfPCell(new Phrase("RECIBO DE OPERACIÓN", tituloFont));
            titulo.setHorizontalAlignment(Element.ALIGN_LEFT);
            titulo.setBorder(Rectangle.NO_BORDER);
            encabezado.addCell(titulo);

            doc.add(encabezado);
            doc.add(new Paragraph("\n"));

            LineSeparator ls = new LineSeparator();
            doc.add(new Chunk(ls));
            doc.add(new Paragraph("\n"));

            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2, 5});

            agregarFila(tabla, "ID de Recibo:", r.getIdRecibo(), labelFont, normalFont);
            agregarFila(tabla, "Fecha:", r.getFechaFormateada(), labelFont, normalFont);
            agregarFila(tabla, "Cliente (usuario):", r.getClienteUsuario(), labelFont, normalFont);
            agregarFila(tabla, "Número de Cuenta:", r.getNumeroCuenta(), labelFont, normalFont);
            agregarFila(tabla, "Administrador:", r.getAdministradorUsuario() != null ? r.getAdministradorUsuario() : "N/A", labelFont, normalFont);
            agregarFila(tabla, "Tipo de Movimiento:", r.getTipoMovimiento(), labelFont, normalFont);
            agregarFila(tabla, "Monto:", String.format("%.2f", r.getMonto()), labelFont, normalFont);
            agregarFila(tabla, "Descripción:", r.getDescripcion(), labelFont, normalFont);

            doc.add(tabla);

            doc.add(new Paragraph("\n\n"));
            doc.add(new Chunk(ls));

            doc.add(new Paragraph("\nFirma del Cliente ____________________________"));
            doc.add(new Paragraph("\nFirma del Administrador ______________________"));

            doc.close();
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* ===========================================================
       PDF GLOBAL
       =========================================================== */
    public static void generarPDFGlobal(List<Recibo> recibos) {
        try {
            File dir = new File(DIR);
            if (!dir.exists()) dir.mkdirs();

            String fileName = DIR + File.separator + "recibos_globales.pdf";

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, new FileOutputStream(fileName));
            doc.open();

            PdfPTable encabezado = new PdfPTable(2);
            encabezado.setWidthPercentage(100);
            encabezado.setWidths(new float[]{1, 4});

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);

            try {
                Image logo = Image.getInstance(LOGO_PATH);
                logo.scaleToFit(70, 70);
                logoCell.addElement(logo);
            } catch (Exception e) {
                logoCell.addElement(getLogoGenerado(0x3366FF));
            }

            encabezado.addCell(logoCell);

            Font titulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            PdfPCell tituloCell = new PdfPCell(new Phrase("REPORTE GLOBAL DE MOVIMIENTOS", titulo));
            tituloCell.setBorder(Rectangle.NO_BORDER);

            encabezado.addCell(tituloCell);
            doc.add(encabezado);
            doc.add(new Paragraph("\n"));

            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2, 2, 2, 2, 4});

            tabla.addCell("Fecha");
            tabla.addCell("Cliente");
            tabla.addCell("Cuenta");
            tabla.addCell("Tipo");
            tabla.addCell("Monto");

            for (Recibo r : recibos) {
                tabla.addCell(r.getFechaFormateada());
                tabla.addCell(r.getClienteUsuario());
                tabla.addCell(r.getNumeroCuenta());
                tabla.addCell(r.getTipoMovimiento());
                tabla.addCell(String.valueOf(r.getMonto()));
            }

            doc.add(tabla);
            doc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ===========================================================
       PDF POR CLIENTE
       =========================================================== */
    public static void generarPDFCliente(Cliente cli) {
        try {
            File dir = new File(DIR);
            if (!dir.exists()) dir.mkdirs();

            String safeName = cli.getNombreUsuario().replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = DIR + File.separator + "recibos_cliente_" + safeName + ".pdf";

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, new FileOutputStream(fileName));
            doc.open();

            // ENCABEZADO
            PdfPTable encabezado = new PdfPTable(2);
            encabezado.setWidthPercentage(100);
            encabezado.setWidths(new float[]{1, 4});

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);

            try {
                Image logo = Image.getInstance(LOGO_PATH);
                logo.scaleToFit(70, 70);
                logoCell.addElement(logo);
            } catch (Exception e) {
                logoCell.addElement(getLogoGenerado(0x3366FF));
            }

            encabezado.addCell(logoCell);

            Font titulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            PdfPCell tituloCell = new PdfPCell(new Phrase("REPORTE DE MOVIMIENTOS DEL CLIENTE", titulo));
            tituloCell.setBorder(Rectangle.NO_BORDER);
            encabezado.addCell(tituloCell);

            doc.add(encabezado);
            doc.add(new Paragraph("Usuario: " + cli.getNombreUsuario()));
            doc.add(new Paragraph("\n"));

            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2, 2, 2, 2, 4});

            tabla.addCell("Fecha");
            tabla.addCell("Cuenta");
            tabla.addCell("Tipo");
            tabla.addCell("Monto");
            tabla.addCell("Descripción");

            for (CuentaBancaria c : cli.getCuentas()) {
                for (Recibo r : c.getHistorial()) {
                    tabla.addCell(r.getFechaFormateada());
                    tabla.addCell(r.getNumeroCuenta());
                    tabla.addCell(r.getTipoMovimiento());
                    tabla.addCell(String.valueOf(r.getMonto()));
                    tabla.addCell(r.getDescripcion());
                }
            }

            doc.add(tabla);
            doc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ===========================================================
       PDF POR ADMINISTRADOR (NUEVO)
       =========================================================== */
    public static void generarPDFAdministrador(Administrador admin, List<Recibo> recibos) {
        try {
            File dir = new File(DIR);
            if (!dir.exists()) dir.mkdirs();

            String safeName = admin.getNombreUsuario().replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = DIR + File.separator + "recibos_administrador_" + safeName + ".pdf";

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, new FileOutputStream(fileName));
            doc.open();

            // ENCABEZADO
            PdfPTable encabezado = new PdfPTable(2);
            encabezado.setWidthPercentage(100);
            encabezado.setWidths(new float[]{1, 4});

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);

            try {
                Image logo = Image.getInstance(LOGO_PATH);
                logo.scaleToFit(70, 70);
                logoCell.addElement(logo);
            } catch (Exception e) {
                logoCell.addElement(getLogoGenerado(0x3366FF));
            }

            encabezado.addCell(logoCell);

            Font titulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            PdfPCell tituloCell = new PdfPCell(new Phrase("REPORTE DE MOVIMIENTOS GESTIONADOS POR EL ADMINISTRADOR", titulo));
            tituloCell.setBorder(Rectangle.NO_BORDER);
            encabezado.addCell(tituloCell);

            doc.add(encabezado);
            doc.add(new Paragraph("Administrador: " + admin.getNombreUsuario()));
            doc.add(new Paragraph("\n"));

            PdfPTable tabla = new PdfPTable(5);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2, 2, 2, 2, 4});

            tabla.addCell("Fecha");
            tabla.addCell("Cliente");
            tabla.addCell("Cuenta");
            tabla.addCell("Tipo");
            tabla.addCell("Monto");

            for (Recibo r : recibos) {
                if (admin.getNombreUsuario().equals(r.getAdministradorUsuario())) {
                    tabla.addCell(r.getFechaFormateada());
                    tabla.addCell(r.getClienteUsuario());
                    tabla.addCell(r.getNumeroCuenta());
                    tabla.addCell(r.getTipoMovimiento());
                    tabla.addCell(String.valueOf(r.getMonto()));
                }
            }

            doc.add(tabla);
            doc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ===========================================================
       UTILIDADES
       =========================================================== */
    private static PdfPTable getLogoGenerado(int colorHex) {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);

        PdfPCell c = new PdfPCell();
        c.setFixedHeight(40);
        c.setBackgroundColor(new BaseColor(colorHex));
        c.setBorder(Rectangle.NO_BORDER);

        Paragraph p = new Paragraph("CLIENTE", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE));
        p.setAlignment(Element.ALIGN_CENTER);
        c.addElement(p);

        t.addCell(c);
        return t;
    }

    private static void agregarFila(PdfPTable tabla, String etiqueta, String valor, Font labelFont, Font normalFont) {
        PdfPCell c1 = new PdfPCell(new Phrase(etiqueta, labelFont));
        c1.setBorder(Rectangle.NO_BORDER);
        tabla.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase(valor != null ? valor : "", normalFont));
        c2.setBorder(Rectangle.NO_BORDER);
        tabla.addCell(c2);
    }
}
