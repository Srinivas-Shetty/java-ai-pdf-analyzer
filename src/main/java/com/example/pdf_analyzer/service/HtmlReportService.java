package com.example.pdf_analyzer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class HtmlReportService {

    @Value("${report.output.dir:src/main/resources/static/reports}")
    private String reportOutputDir;

    public String generateReport(String documentId, String summary, String originalFilename) throws IOException {

        // Ensure the directory exists
        File directory = new File(reportOutputDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = documentId + ".html";
        File htmlFile = new File(directory, fileName);

        String htmlContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>PDF Analysis Report</title>\n" +
                "    <style>\n" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 40px; background-color: #f4f7f6; color: #333; line-height: 1.6; }\n"
                +
                "        .container { max-width: 800px; margin: auto; padding: 30px; background: #fff; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }\n"
                +
                "        h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 15px; margin-bottom: 20px; }\n"
                +
                "        h2 { color: #34495e; margin-top: 30px; }\n" +
                "        .meta { background: #ecf0f1; padding: 20px; border-radius: 8px; margin-bottom: 25px; border-left: 4px solid #7f8c8d; }\n"
                +
                "        .summary { background: #e8f4fd; padding: 25px; border-left: 4px solid #3498db; border-radius: 8px; font-size: 1.1em; }\n"
                +
                "        .qa-section { background: #fff; border: 1px solid #ddd; padding: 25px; border-radius: 8px; margin-top: 30px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }\n"
                +
                "        .input-group { display: flex; gap: 10px; margin-bottom: 15px; }\n" +
                "        input[type=\"text\"] { flex-grow: 1; padding: 12px 15px; border: 1px solid #ccc; border-radius: 6px; font-size: 1em; transition: border-color 0.3s; }\n"
                +
                "        input[type=\"text\"]:focus { border-color: #3498db; outline: none; }\n" +
                "        button { padding: 12px 25px; background: #3498db; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 1em; font-weight: bold; transition: background-color 0.3s; }\n"
                +
                "        button:hover { background: #2980b9; }\n" +
                "        button:disabled { background: #95a5a6; cursor: not-allowed; }\n" +
                "        #answer-box { display: none; margin-top: 20px; padding: 20px; background: #fdfefe; border-left: 4px solid #2ecc71; border-radius: 8px; }\n"
                +
                "        .loading-text { color: #7f8c8d; font-style: italic; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>PDF Analysis Report</h1>\n" +
                "        <div class=\"meta\">\n" +
                "            <p><strong>Original File:</strong> " + originalFilename + "</p>\n" +
                "            <p><strong>Document ID:</strong> <span id=\"doc-id\">" + documentId + "</span></p>\n" +
                "        </div>\n" +
                "        \n" +
                "        <h2>AI Summary</h2>\n" +
                "        <div class=\"summary\">\n" +
                "            <p>" + summary.replace("\n", "<br>") + "</p>\n" +
                "        </div>\n" +
                "        \n" +
                "        <h2>Ask Questions</h2>\n" +
                "        <div class=\"qa-section\">\n" +
                "            <p>Have specific questions about this document? Ask the AI below:</p>\n" +
                "            <div class=\"input-group\">\n" +
                "                <input type=\"text\" id=\"question-input\" placeholder=\"e.g., What are the main conclusions?\" onkeypress=\"handleKeyPress(event)\">\n"
                +
                "                <button id=\"ask-button\" onclick=\"askQuestion()\">Ask AI</button>\n" +
                "            </div>\n" +
                "            <div id=\"answer-box\">\n" +
                "                <strong>Answer:</strong> <span id=\"answer-text\"></span>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    \n" +
                "    <script>\n" +
                "        function handleKeyPress(event) {\n" +
                "            if (event.key === 'Enter') {\n" +
                "                askQuestion();\n" +
                "            }\n" +
                "        }\n" +
                "        \n" +
                "        async function askQuestion() {\n" +
                "            const questionInput = document.getElementById('question-input');\n" +
                "            const question = questionInput.value.trim();\n" +
                "            const documentId = document.getElementById('doc-id').textContent;\n" +
                "            const answerBox = document.getElementById('answer-box');\n" +
                "            const answerText = document.getElementById('answer-text');\n" +
                "            const askButton = document.getElementById('ask-button');\n" +
                "            \n" +
                "            if (!question) return;\n" +
                "            \n" +
                "            // UI Loading state\n" +
                "            askButton.disabled = true;\n" +
                "            askButton.textContent = 'Thinking...';\n" +
                "            answerBox.style.display = 'block';\n" +
                "            answerText.innerHTML = '<span class=\"loading-text\">Analyzing document for an answer...</span>';\n"
                +
                "            \n" +
                "            try {\n" +
                "                // IMPORTANT: Use absolute path or adjust based on your deployment context.\n" +
                "                // Since this HTML is served from /reports/..., we need to point to the base API\n" +
                "                const response = await fetch('/api/pdf/question', {\n" +
                "                    method: 'POST',\n" +
                "                    headers: {\n" +
                "                        'Content-Type': 'application/json',\n" +
                "                    },\n" +
                "                    body: JSON.stringify({\n" +
                "                        documentId: documentId,\n" +
                "                        question: question\n" +
                "                    })\n" +
                "                });\n" +
                "                \n" +
                "                if (!response.ok) {\n" +
                "                    const errText = await response.text();\n" +
                "                    throw new Error(errText || 'Failed to get answer');\n" +
                "                }\n" +
                "                \n" +
                "                const data = await response.json();\n" +
                "                // Format formatting to maintain readability of newlines from AI output\n" +
                "                answerText.innerHTML = data.answer.replace(/\\n/g, '<br>');\n" +
                "            } catch (error) {\n" +
                "                console.error('Error asking question:', error);\n" +
                "                answerText.innerHTML = '<span style=\"color: #e74c3c;\">Error: ' + error.message + '</span>';\n"
                +
                "            } finally {\n" +
                "                askButton.disabled = false;\n" +
                "                askButton.textContent = 'Ask AI';\n" +
                "            }\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";

        try (FileWriter writer = new FileWriter(htmlFile)) {
            writer.write(htmlContent);
        }

        // Return the relative URL path for the client
        return "/reports/" + fileName;
    }
}
