package com.example.pdf_analyzer.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PdfAnalysisService {

    private final HuggingFaceService huggingFaceService;
    private final TextChunkingService chunkingService;

    public PdfAnalysisService(HuggingFaceService huggingFaceService, TextChunkingService chunkingService) {
        this.huggingFaceService = huggingFaceService;
        this.chunkingService = chunkingService;
    }

    public String generateSummary(String fullText) {
        List<String> chunks = chunkingService.chunkText(fullText);
        StringBuilder finalSummary = new StringBuilder();

        // Summarize all chunks and concatenate the results
        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("Summarizing chunk " + (i + 1) + " of " + chunks.size());
            String chunkSummary = huggingFaceService.summarize(chunks.get(i));
            if (chunkSummary != null && !chunkSummary.isEmpty()) {
                finalSummary.append(chunkSummary).append(" ");
            }
        }

        return finalSummary.toString().trim();
    }

    public String answerQuestion(String fullText, String question) {
        List<String> chunks = chunkingService.chunkText(fullText);

        // Ask question on ALL chunks and pick the best-scoring answer
        String bestAnswer = null;
        double bestScore = -1.0;

        for (int i = 0; i < chunks.size(); i++) {
            System.out.println("Searching chunk " + (i + 1) + " of " + chunks.size() + " for answer...");
            double[] scoreHolder = new double[1];
            String answer = huggingFaceService.answerQuestion(chunks.get(i), question, scoreHolder);
            if (answer != null && !answer.trim().isEmpty()
                    && !answer.contains("failed") && !answer.contains("Error")
                    && !answer.equals("Could not generate answer.")) {
                if (scoreHolder[0] > bestScore) {
                    bestScore = scoreHolder[0];
                    bestAnswer = answer;
                }
            }
        }

        if (bestAnswer != null) {
            return bestAnswer;
        }
        return "Sorry, I couldn't find an answer to that question in the document.";
    }
}
