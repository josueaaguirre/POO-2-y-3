package com.mycompany.sistemadeautenticacion;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class GeneradorReportePDF {

    private static final String DIR = "reportes";
    private static final String FILE = "ReporteMovimientos.pdf";

    public static void generarReporteGeneral(List<Recibo> lista) {
        if (lista == null || lista.isEmpty()) return;

        try {
            File dir = new File(DIR);
            if (!dir.exists()) dir.mkdirs();

            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, new FileOutputStream(DIR + File.separator + FILE));
            doc.open();

            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 11);

            Paragraph titulo = new Paragraph("REPORTE GLOBAL DE MOVIMIENTOS\n\n", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            PdfPTable tabla = new PdfPTable(7);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2, 3, 3, 3, 3, 2, 3});

            agregarCelda(tabla, "ID", headerFont);
            agregarCelda(tabla, "Fecha", headerFont);
            agregarCelda(tabla, "Cliente", headerFont);
            agregarCelda(tabla, "Cuenta", headerFont);
            agregarCelda(tabla, "Tipo", headerFont);
            agregarCelda(tabla, "Monto", headerFont);
            agregarCelda(tabla, "Descripción", headerFont);

            for (Recibo r : lista) {
                agregarCelda(tabla, r.getIdRecibo(), normalFont);
                agregarCelda(tabla, r.getFechaFormateada(), normalFont);
                agregarCelda(tabla, r.getClienteUsuario(), normalFont);
                agregarCelda(tabla, r.getNumeroCuenta(), normalFont);
                agregarCelda(tabla, r.getTipoMovimiento(), normalFont);
                agregarCelda(tabla, String.format("%.2f", r.getMonto()), normalFont);
                agregarCelda(tabla, r.getDescripcion(), normalFont);
            }

            doc.add(tabla);
            doc.close();

            System.out.println("PDF GLOBAL generado en /reportes/" + FILE);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void agregarCelda(PdfPTable tabla, String texto, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(texto != null ? texto : "", f));
        c.setHorizontalAlignment(Element.ALIGN_LEFT);
        tabla.addCell(c);
    }
}
