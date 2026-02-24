package com.example.pdf_analyzer.service;

import org.springframework.stereotype.Service;

@Service
public class TextCleanerService {

    public String cleanText(String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return "";
        }
        // Remove extra whitespaces, new lines
        String cleaned = rawText.replaceAll("\\s+", " ").trim();
        // Remove non-printable characters
        cleaned = cleaned.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
        return cleaned;
    }
}
