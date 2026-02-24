package com.example.pdf_analyzer.dto;

public class QuestionResponse {
    private String answer;

    public QuestionResponse() {
    }

    public QuestionResponse(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
