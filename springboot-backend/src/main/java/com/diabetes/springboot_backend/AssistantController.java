package com.diabetes.springboot_backend;

import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final OpenAIService openAIService;
    private final FlaskIntegrationService flaskService;

    public AssistantController(
            OpenAIService openAIService,
            FlaskIntegrationService flaskService) {
        this.openAIService = openAIService;
        this.flaskService = flaskService;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Cookie", required = false)
            String cookie) {

        try {
            // Verify the user's Flask login session.
            HttpResponse<String> historyResponse =
                    flaskService.forwardHistory(cookie);

            if (historyResponse.statusCode() == 401) {
                return error(
                        HttpStatus.UNAUTHORIZED,
                        "Please log in before using the assistant."
                );
            }

            if (historyResponse.statusCode() != 200) {
                System.err.println(
                        "Flask history returned HTTP status: "
                        + historyResponse.statusCode()
                );

                return error(
                        HttpStatus.BAD_GATEWAY,
                        "Could not verify the user session."
                );
            }

            // Validate the question.
            String question = request.get("question");

            if (question == null || question.isBlank()) {
                return error(
                        HttpStatus.BAD_REQUEST,
                        "Question is required."
                );
            }

            if (question.length() > 2000) {
                return error(
                        HttpStatus.BAD_REQUEST,
                        "Question must not exceed 2000 characters."
                );
            }

            // Send the question to the AI service.
            String answer = openAIService.ask(question);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            result.put("answer", answer);

            return ResponseEntity.ok(result);

        } catch (IllegalStateException e) {
            // Log the cause without exposing credentials.
            System.err.println(
                    "Assistant configuration/API error: "
                    + e.getMessage()
            );

            return error(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "The AI service is not configured or unavailable."
            );

        } catch (Exception e) {
            // Log the error type and message for debugging.
            System.err.println(
                    "Assistant error type: "
                    + e.getClass().getName()
            );

            System.err.println(
                    "Assistant error message: "
                    + e.getMessage()
            );

            return error(
                    HttpStatus.BAD_GATEWAY,
                    "The assistant could not process your question."
            );
        }
    }

    private ResponseEntity<Map<String, Object>> error(
            HttpStatus status,
            String message) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", false);
        result.put("error", message);

        return ResponseEntity
                .status(status)
                .body(result);
    }
}
