package com.mycompany.sistemadeautenticacion;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

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

            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(fileName));
            doc.open();
            doc.add(new Paragraph("=== RECIBO DE OPERACIÓN ==="));
            doc.add(new Paragraph("ID: " + r.getIdRecibo()));
            doc.add(new Paragraph("Fecha: " + r.getFechaFormateada()));
            doc.add(new Paragraph("Cliente (username): " + r.getClienteUsuario()));
            doc.add(new Paragraph("Cuenta: " + r.getNumeroCuenta()));
            doc.add(new Paragraph("Administrador: " + (r.getAdministradorUsuario() != null ? r.getAdministradorUsuario() : "N/A")));
            doc.add(new Paragraph("Tipo: " + r.getTipoMovimiento()));
            doc.add(new Paragraph(String.format("Monto: %.2f", r.getMonto())));
            doc.add(new Paragraph("Descripción: " + r.getDescripcion()));
            doc.add(new Paragraph("============================="));
            doc.close();
            System.out.println("PDF generado: " + fileName);
        } catch (DocumentException | IOException ex) {
            System.err.println("Error generando PDF: " + ex.getMessage());
        }
    }
}
