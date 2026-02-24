package com.example.pdf_analyzer.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PdfTextExtractionService {

    public String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            if (document.isEncrypted()) {
                throw new IOException("Cannot extract text from an encrypted PDF.");
            }
            PDFTextStripper textStripper = new PDFTextStripper();
            return textStripper.getText(document);
        }
    }
}
