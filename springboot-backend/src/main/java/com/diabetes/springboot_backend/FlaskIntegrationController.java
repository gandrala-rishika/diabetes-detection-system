
package com.diabetes.springboot_backend;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlaskIntegrationController {

    private final FlaskIntegrationService flaskService;

    public FlaskIntegrationController(
            FlaskIntegrationService flaskService) {
        this.flaskService = flaskService;
    }

    @GetMapping("/api/integration/flask-health")
    public ResponseEntity<Map<String, Object>> checkFlask() {
        try {
            int flaskStatus = flaskService.checkFlaskConnection();

            if (flaskStatus >= 200 && flaskStatus < 300) {
                return ResponseEntity.ok(Map.of(
                        "springBoot", "UP",
                        "flask", "UP",
                        "flaskHttpStatus", flaskStatus
                ));
            }

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of(
                            "springBoot", "UP",
                            "flask", "UNEXPECTED_RESPONSE",
                            "flaskHttpStatus", flaskStatus
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "springBoot", "UP",
                            "flask", "UNAVAILABLE",
                            "error", "Could not connect to Flask on port 5001"
                    ));
        }
    }
}
