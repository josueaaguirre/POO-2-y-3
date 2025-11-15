package com.mycompany.sistemadeautenticacion;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GeneradorReciboPDF {

    private static final String DIR = "recibos";

    public static void generar(Recibo r) {
        if (r == null) return;

        try {
            File dir = new File(DIR);
            if (!dir.exists()) dir.mkdirs();

            String safeName = r.getClienteUsuario().replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = DIR + File.separator + "recibo_" + safeName + "_" + r.getIdRecibo() + ".pdf";

            // ---------------------
            // DOCUMENTO
            // ---------------------
            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, new FileOutputStream(fileName));
            doc.open();

            // ---------------------
            // FUENTES
            // ---------------------
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

            // ---------------------
            // LOGO (ESTILO A - Azul Cliente)
            // Generamos un logo azul con una figura simple (sin imagen externa)
            // ---------------------
            PdfPTable encabezado = new PdfPTable(2);
            encabezado.setWidthPercentage(100);
            encabezado.setWidths(new float[]{1, 4});

            // Cuadrado azul simulando el logo
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setPadding(8);

            PdfContentByte canvas = PdfWriter.getInstance(doc, new FileOutputStream(fileName)).getDirectContent();
            logoCell.addElement(getLogoGenerado(0x3366FF));  // azul

            encabezado.addCell(logoCell);

            PdfPCell titulo = new PdfPCell(new Phrase("RECIBO DE OPERACIÓN", tituloFont));
            titulo.setHorizontalAlignment(Element.ALIGN_LEFT);
            titulo.setVerticalAlignment(Element.ALIGN_MIDDLE);
            titulo.setBorder(Rectangle.NO_BORDER);
            encabezado.addCell(titulo);

            doc.add(encabezado);

            doc.add(new Paragraph("\n")); // espacio

            // ---------------------
            // Línea divisor
            // ---------------------
            LineSeparator ls = new LineSeparator();
            doc.add(ls);
            doc.add(new Paragraph("\n"));

            // ---------------------
            // TABLA DE DATOS
            // ---------------------
            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(10);
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
            doc.add(ls);

            doc.add(new Paragraph("\nFirma del Cliente ____________________________", normalFont));
            doc.add(new Paragraph("\nFirma del Administrador ______________________", normalFont));

            doc.close();
            System.out.println("PDF generado: " + fileName);

        } catch (Exception ex) {
            System.err.println("Error generando PDF: " + ex.getMessage());
        }
    }

    // --------------------------
    // Crea un pequeño logo rectangular de color
    // --------------------------
    private static PdfPTable getLogoGenerado(int colorHex) {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);

        PdfPCell c = new PdfPCell();
        c.setFixedHeight(40);
        c.setBackgroundColor(new BaseColor(colorHex));
        c.setBorder(Rectangle.NO_BORDER);

        // Texto blanco centrado dentro del logo
        Paragraph p = new Paragraph("CLIENTE", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE));
        p.setAlignment(Element.ALIGN_CENTER);
        c.addElement(p);

        t.addCell(c);
        return t;
    }

    // --------------------------
    // Añade una fila clave-valor a la tabla
    // --------------------------
    private static void agregarFila(PdfPTable tabla, String etiqueta, String valor, Font labelFont, Font normalFont) {
        PdfPCell c1 = new PdfPCell(new Phrase(etiqueta, labelFont));
        c1.setBorder(Rectangle.NO_BORDER);
        tabla.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase(valor, normalFont));
        c2.setBorder(Rectangle.NO_BORDER);
        tabla.addCell(c2);
    }
}
