package com.example.pdf_analyzer.controller;

import com.example.pdf_analyzer.dto.PdfUploadResponse;
import com.example.pdf_analyzer.dto.QuestionRequest;
import com.example.pdf_analyzer.dto.QuestionResponse;
import com.example.pdf_analyzer.service.PdfAnalysisService;
import com.example.pdf_analyzer.service.PdfTextExtractionService;
import com.example.pdf_analyzer.service.TextCleanerService;
import com.example.pdf_analyzer.store.InMemoryPdfStore;
import com.example.pdf_analyzer.util.IdGenerator;
import com.example.pdf_analyzer.service.HtmlReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfTextExtractionService textExtractionService;
    private final TextCleanerService textCleanerService;
    private final PdfAnalysisService pdfAnalysisService;
    private final InMemoryPdfStore pdfStore;
    private final HtmlReportService htmlReportService;

    public PdfController(PdfTextExtractionService textExtractionService,
            TextCleanerService textCleanerService,
            PdfAnalysisService pdfAnalysisService,
            InMemoryPdfStore pdfStore,
            HtmlReportService htmlReportService) {
        this.textExtractionService = textExtractionService;
        this.textCleanerService = textCleanerService;
        this.pdfAnalysisService = pdfAnalysisService;
        this.pdfStore = pdfStore;
        this.htmlReportService = htmlReportService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || file.getContentType() == null
                || !file.getContentType().equalsIgnoreCase("application/pdf")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid file type or empty file. Please upload a PDF.");
        }

        try {
            // 1. Extract text
            String rawText = textExtractionService.extractTextFromPdf(file);
            System.out.println("Extracted raw text length: " + (rawText != null ? rawText.length() : 0));

            // 2. Clean text
            String cleanedText = textCleanerService.cleanText(rawText);
            System.out.println("Cleaned text length: " + (cleanedText != null ? cleanedText.length() : 0));

            if (cleanedText == null || cleanedText.isEmpty()) {
                System.out.println(
                        "Extraction failed. The PDF might be an image/scanned document with no selectable text.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        "Could not extract any text from the PDF. Ensure it contains selectable text and is not just scanned images.");
            }

            // 3. Generate ID and Store
            String documentId = IdGenerator.generateUniqueId();
            pdfStore.storeText(documentId, cleanedText);

            // 4. Generate AI Summary
            String summary = pdfAnalysisService.generateSummary(cleanedText);

            // 4.5 Generate HTML Report
            String reportUrl = htmlReportService.generateReport(documentId, summary, file.getOriginalFilename());

            // 5. Return Response
            return ResponseEntity.ok(new PdfUploadResponse(documentId, summary, reportUrl));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing PDF: " + e.getMessage());
        }
    }

    @PostMapping("/question")
    public ResponseEntity<?> askQuestion(@RequestBody QuestionRequest request) {
        if (request.getDocumentId() == null || request.getDocumentId().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing documentId.");
        }
        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing question.");
        }

        if (!pdfStore.exists(request.getDocumentId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found.");
        }

        try {
            String textContext = pdfStore.getText(request.getDocumentId());
            String answer = pdfAnalysisService.answerQuestion(textContext, request.getQuestion());
            return ResponseEntity.ok(new QuestionResponse(answer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error answering question: " + e.getMessage());
        }
    }
}
