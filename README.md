# AI PDF Analyzer API

This is a Spring Boot REST API for analyzing PDF documents using Hugging Face AI models.
It extracts text from uploading PDFs, cleans and chunks it, and uses the Hugging Face Inference API to provide summaries and answer questions based on the PDF content.

## Prerequisites
- Java 17
- Maven
- Hugging Face API Key (Configured by default using the provided key, or set via `HUGGINGFACE_API_KEY` environment variable)

## How to Run

1. Open a terminal in the project root directory (`d:/pdf-analyzer`).
2. Run the application using the included Maven wrapper:
   ```bash
   # On Windows
   mvnw.cmd spring-boot:run

   # On Linux/macOS
   ./mvnw spring-boot:run
   ```
3. The server will start on `http://localhost:8080`.

## API Endpoints & Sample Postman Requests

### 1. Upload & Analyze PDF
**URL:** `POST http://localhost:8080/api/pdf/upload`
**Content-Type:** `multipart/form-data`

**Instructions for Postman:**
- Select `POST` request to `http://localhost:8080/api/pdf/upload`
- Go to the `Body` tab -> Select `form-data`.
- In the `KEY` column, type `file` and change the type from `Text` to `File` (by hovering over the right side of the key field).
- In the `VALUE` column, click `Select Files` and choose a `.pdf` file.
- Click `Send`.

**Sample Response:**
```json
{
  "documentId": "e14b1cc8-9a67-4632-84bc-5b8cb46de4b9",
  "summary": "This document covers the basics of AI and machine learning..."
}
```

### 2. Ask a Question
**URL:** `POST http://localhost:8080/api/pdf/question`
**Content-Type:** `application/json`

**Instructions for Postman:**
- Select `POST` request to `http://localhost:8080/api/pdf/question`
- Go to `Body` tab -> Select `raw` -> Select `JSON` from the dropdown.
- Enter the JSON payload below using the `documentId` returned from the upload request.
- Click `Send`.

**Request Body:**
```json
{
  "documentId": "e14b1cc8-9a67-4632-84bc-5b8cb46de4b9",
  "question": "What is the main topic of the document?"
}
```

**Sample Response:**
```json
{
  "answer": "The main topic is AI and machine learning."
}
```

## Architecture & Features
- **Stateless APIs:** With an in-memory `ConcurrentHashMap` for temporarily storing extracted PDF text.
- **AI Integration:** Uses Hugging Face inference endpoints (`facebook/bart-large-cnn` and `deepset/roberta-base-squad2`).
- **Text Chunking:** Splits text into safe-sized parts before running through AI models to prevent exceeding Hugging Face token limits.
- **REST Conventions:** Follows best practices for API paths and JSON DTOs.
