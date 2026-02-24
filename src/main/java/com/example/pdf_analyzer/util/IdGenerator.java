package com.example.pdf_analyzer.util;

import java.util.UUID;

public class IdGenerator {

    private IdGenerator() {
        // Utility class
    }

    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}
