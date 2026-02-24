package com.example.pdf_analyzer.dto;

public class QuestionRequest {
    private String documentId;
    private String question;

    public QuestionRequest() {
    }

    public QuestionRequest(String documentId, String question) {
        this.documentId = documentId;
        this.question = question;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
