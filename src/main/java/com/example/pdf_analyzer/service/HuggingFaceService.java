package com.example.pdf_analyzer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HuggingFaceService {

    @Value("${huggingface.api.key}")
    private String apiKey;

    // ✅ Updated Hugging Face router endpoints
    private static final String SUMMARIZATION_MODEL_URL = "https://router.huggingface.co/hf-inference/models/facebook/bart-large-cnn";

    private static final String QA_MODEL_URL = "https://router.huggingface.co/hf-inference/models/deepset/roberta-base-squad2";

    private final RestTemplate restTemplate = new RestTemplate();

    public String summarize(String text) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 🔥 THIS LINE FIXES YOUR ERROR
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, String> body = new HashMap<>();
        body.put("inputs", text);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        int retries = 3;

        while (retries-- > 0) {
            try {
                List<Map<String, String>> response = restTemplate.postForObject(
                        SUMMARIZATION_MODEL_URL,
                        request,
                        List.class);

                if (response != null && !response.isEmpty()
                        && response.get(0).containsKey("summary_text")) {
                    return response.get(0).get("summary_text");
                }

            } catch (org.springframework.web.client.HttpStatusCodeException e) {

                // Handle 503 Model Loading or 504 Gateway Timeout
                if (e.getStatusCode().value() == 503 || e.getStatusCode().value() == 504) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {
                    }
                    continue;
                }

                String errorBody = e.getResponseBodyAsString();
                if (errorBody.trim().startsWith("<")) {
                    return "Hugging Face API Error: The server returned an HTML error page. (Status "
                            + e.getStatusCode().value() + ")";
                }

                return "Hugging Face API Error: " + errorBody;

            } catch (Exception e) {
                return "Summarization failed: " + e.getMessage();
            }
        }

        return "Model still loading. Please try again.";
    }

    public String answerQuestion(String context, String question, double[] scoreHolder) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("question", question);
        inputs.put("context", context);

        Map<String, Object> body = new HashMap<>();
        body.put("inputs", inputs);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        int retries = 3;

        while (retries-- > 0) {
            try {
                Map<String, Object> response = restTemplate.postForObject(QA_MODEL_URL, request, Map.class);

                if (response != null && response.containsKey("answer")) {
                    // Extract confidence score if available
                    if (scoreHolder != null && response.containsKey("score")) {
                        Object scoreObj = response.get("score");
                        if (scoreObj instanceof Number) {
                            scoreHolder[0] = ((Number) scoreObj).doubleValue();
                        }
                    }
                    return (String) response.get("answer");
                }

            } catch (org.springframework.web.client.HttpStatusCodeException e) {
                // Handle 503 Model Loading or 504 Gateway Timeout
                if (e.getStatusCode().value() == 503 || e.getStatusCode().value() == 504) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {
                    }
                    continue;
                }

                String errorBody = e.getResponseBodyAsString();
                if (errorBody.trim().startsWith("<")) {
                    return "Hugging Face API Error: The server returned an HTML error page. (Status "
                            + e.getStatusCode().value() + ")";
                }

                return "Hugging Face API Error: " + errorBody;

            } catch (Exception e) {
                return "Question answering failed: " + e.getMessage();
            }
        }

        return "Model still loading or gateway timed out. Please try again.";
    }
}
