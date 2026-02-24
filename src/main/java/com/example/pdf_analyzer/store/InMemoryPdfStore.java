package com.example.pdf_analyzer.store;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryPdfStore {
    // Key: documentId, Value: extracted PDF text
    private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

    public void storeText(String documentId, String text) {
        store.put(documentId, text);
    }

    public String getText(String documentId) {
        return store.get(documentId);
    }

    public boolean exists(String documentId) {
        return store.containsKey(documentId);
    }
}
