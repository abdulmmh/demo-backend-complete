package com.nirapod.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;

import com.nirapod.dao.TinDAO;
import com.nirapod.model.Tin;
import com.nirapod.model.Taxpayer;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TinCertificateService {

    @Autowired private TinDAO tinDAO;

    /**
     * New signature — accepts only the TIN id.
     * All taxpayer data is fetched via the FK relationship (no raw Taxpayer arg needed).
     */
    public void generateCertificate(Long tinId, HttpServletResponse response) throws Exception {

        Tin tin = tinDAO.findById(tinId)
            .orElseThrow(() -> new IllegalArgumentException("TIN not found: " + tinId));

        // Name and all taxpayer details come from the FK — never from a stale stored copy
        Taxpayer taxpayer = tin.getTaxpayer();
        String name = "Company".equalsIgnoreCase(
                taxpayer.getTaxpayerType() != null
                    ? taxpayer.getTaxpayerType().getTypeName() : "")
            ? taxpayer.getCompanyName()
            : taxpayer.getFullName();

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        PdfContentByte canvas = writer.getDirectContent();

        Rectangle rect = new Rectangle(30, 30, 565, 812);
        rect.setBorder(Rectangle.BOX);
        rect.setBorderWidth(1.5f);
        canvas.rectangle(rect);

        try {
            Image watermark = Image.getInstance(
                new ClassPathResource("images/watermark.png").getURL());
            watermark.scaleToFit(250, 250);
            float x = (PageSize.A4.getWidth()  - watermark.getScaledWidth())  / 2;
            float y = (PageSize.A4.getHeight() - watermark.getScaledHeight()) / 2;
            watermark.setAbsolutePosition(x, y);
            canvas.saveState();
            PdfGState state = new PdfGState();
            state.setFillOpacity(0.15f);
            canvas.setGState(state);
            canvas.addImage(watermark);
            canvas.restoreState();
        } catch (Exception ignored) {}

        Font titleFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font boldFont     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font normalFont   = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font smallFont    = FontFactory.getFont(FontFactory.HELVETICA, 8);

        try {
            Image logo = Image.getInstance(
                new ClassPathResource("images/govt_logo.png").getURL());
            logo.scaleToFit(60, 60);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception ignored) {}

        Paragraph p1 = new Paragraph(
            "Government of the People's Republic of Bangladesh", titleFont);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);

        Paragraph p2 = new Paragraph("National Board of Revenue", titleFont);
        p2.setAlignment(Element.ALIGN_CENTER);
        document.add(p2);

        document.add(Chunk.NEWLINE);

        Paragraph p3 = new Paragraph(
            "Taxpayer's Identification Number (TIN) Certificate", subTitleFont);
        p3.setAlignment(Element.ALIGN_CENTER);
        document.add(p3);

        Paragraph tinTitle = new Paragraph("TIN : " + tin.getTinNumber(), titleFont);
        tinTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(tinTitle);

        document.add(new Paragraph(
            "________________________________________________________________________"));
        document.add(Chunk.NEWLINE);

        Paragraph certify = new Paragraph();
        certify.add(new Chunk("This is to Certify that ", normalFont));
        certify.add(new Chunk(name, boldFont));
        certify.add(new Chunk(
            " is a Registered Taxpayer of National Board of Revenue under the jurisdiction of ",
            normalFont));
        certify.add(new Chunk(
            "Taxes Circle-" + tin.getTaxCircle() + ", Taxes Zone-" + tin.getTaxZone() + ".",
            boldFont));
        certify.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(certify);

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Taxpayer's Particulars :", boldFont));
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("1) Name : "           + name,                          normalFont));
        document.add(new Paragraph("2) Father's Name : "  + taxpayer.getFathersName(),      normalFont));
        document.add(new Paragraph("3) Mother's Name : "  + taxpayer.getMothersName(),      normalFont));
        document.add(new Paragraph("4.a) Current Address : " + tin.getAddress(),            normalFont));
        document.add(new Paragraph("b) Permanent Address : " + tin.getAddress(),            normalFont));
        document.add(new Paragraph("5) Previous TIN : Not Applicable",                      boldFont));
        document.add(new Paragraph("6) Status : " +
            (taxpayer.getTaxpayerType() != null ? taxpayer.getTaxpayerType().getTypeName() : "N/A"),
            boldFont));

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Date : " + LocalDate.now(), normalFont));
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        BufferedImage qrBuffered = generateQR("TIN: " + tin.getTinNumber() + "\nName: " + name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrBuffered, "png", baos);
        Image qrImage = Image.getInstance(baos.toByteArray());
        qrImage.scaleToFit(100, 100);

        PdfPCell qrCell = new PdfPCell(qrImage, false);
        qrCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell noteCell = new PdfPCell();
        noteCell.setBorder(Rectangle.NO_BORDER);
        noteCell.addElement(new Paragraph("Please Note:", boldFont));
        noteCell.addElement(new Paragraph("1. A Taxpayer is liable to file Return under section 75.", smallFont));
        noteCell.addElement(new Paragraph("2. Failure may result in penalty.", smallFont));

        PdfPCell signCell = new PdfPCell();
        signCell.setBorder(Rectangle.NO_BORDER);
        signCell.addElement(new Paragraph("Deputy Commissioner of Taxes", boldFont));
        signCell.addElement(new Paragraph("Taxes Circle-" + tin.getTaxCircle(), smallFont));
        signCell.addElement(new Paragraph("Taxes Zone-" + tin.getTaxZone() + ", Dhaka", smallFont));

        PdfPTable footerTable = new PdfPTable(3);
        footerTable.setWidthPercentage(100);
        footerTable.setWidths(new float[]{2f, 1f, 1f});
        footerTable.addCell(noteCell);
        footerTable.addCell(signCell);
        footerTable.addCell(qrCell);
        document.add(footerTable);

        document.add(Chunk.NEWLINE);
        Paragraph sysNote = new Paragraph(
            "N.B: This is a system generated certificate and requires no manual signature.",
            smallFont);
        sysNote.setAlignment(Element.ALIGN_CENTER);
        document.add(sysNote);

        document.close();
    }

    private BufferedImage generateQR(String text) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 150, 150);
        BufferedImage image = new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 150; x++)
            for (int y = 0; y < 150; y++)
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
        return image;
    }
}
