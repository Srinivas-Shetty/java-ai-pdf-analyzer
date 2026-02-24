package com.example.pdf_analyzer.dto;

public class PdfUploadResponse {
    private String documentId;
    private String summary;
    private String reportUrl;

    public PdfUploadResponse() {
    }

    public PdfUploadResponse(String documentId, String summary) {
        this.documentId = documentId;
        this.summary = summary;
    }

    public PdfUploadResponse(String documentId, String summary, String reportUrl) {
        this.documentId = documentId;
        this.summary = summary;
        this.reportUrl = reportUrl;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }
}
