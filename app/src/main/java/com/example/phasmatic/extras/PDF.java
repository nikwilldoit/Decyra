package com.example.phasmatic.extras;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.OutputStream;

public class PDF {

    public static boolean exportToPdf(Context context, Uri uri, String content) {
        try {
            OutputStream os = context.getContentResolver().openOutputStream(uri);
            if (os == null) {
                Toast.makeText(context, "Cannot open file", Toast.LENGTH_SHORT).show();
                return false;
            }

            PdfWriter writer = new PdfWriter(os);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("DECYRA").setBold().setFontSize(18));
            document.add(new Paragraph("OI KATALLHLES EPILOGES EINAI").setFontSize(14));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(content != null ? content : "").setFontSize(12));

            document.close();

            Toast.makeText(context, "PDF saved on phone", Toast.LENGTH_LONG).show();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}