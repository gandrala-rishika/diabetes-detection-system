package com.diabetes.springboot_backend;

import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HistoryController {

    private final FlaskIntegrationService flaskService;
private final ObjectMapper objectMapper = new ObjectMapper();

public HistoryController(FlaskIntegrationService flaskService) {
    this.flaskService = flaskService;
}

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getHistory(
            @RequestHeader(value = "Cookie", required = false)
            String cookie) {

        try {
            HttpResponse<String> response =
                    flaskService.forwardHistory(cookie);

            int status = response.statusCode();

            if (status == 401) {
                return error(HttpStatus.UNAUTHORIZED,
                        "Not logged in");
            }

            if (status < 200 || status >= 300) {
                return error(HttpStatus.BAD_GATEWAY,
                        "Flask history service returned HTTP " + status);
            }

            Map<String, Object> result = objectMapper.readValue(
                    response.body(),
                    new TypeReference<Map<String, Object>>() {}
            );

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return error(HttpStatus.SERVICE_UNAVAILABLE,
                    "Could not retrieve prediction history from Flask");
        }
    }

    private ResponseEntity<Map<String, Object>> error(
            HttpStatus status, String message) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", message);

        return ResponseEntity.status(status).body(body);
    }
}
