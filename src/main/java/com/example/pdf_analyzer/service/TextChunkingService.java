package com.example.pdf_analyzer.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunkingService {

    // Chunking by approximate character count to fit AI models
    // Max Hugging Face token limit varies. Certain models (like BART) have maximum
    // lengths
    // under 1024 tokens. Using an 800 character chunk safely stays under these
    // limits.
    private static final int MAX_CHUNK_SIZE = 800;

    public List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return chunks;
        }
        System.out.println("Text: " + text);
        int length = text.length();
        for (int i = 0; i < length; i += MAX_CHUNK_SIZE) {
            chunks.add(text.substring(i, Math.min(length, i + MAX_CHUNK_SIZE)));
        }
        return chunks;
    }
}
