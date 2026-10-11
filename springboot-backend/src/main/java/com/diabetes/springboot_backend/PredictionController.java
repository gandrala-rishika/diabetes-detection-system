package com.diabetes.springboot_backend;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class PredictionController {

    private static final Set<String> REQUIRED_FEATURES = Set.of(
        "Pregnancies",
        "Glucose",
        "BloodPressure",
        "SkinThickness",
        "Insulin",
        "BMI",
        "DiabetesPedigreeFunction",
        "Age"
    );

    private final FlaskIntegrationService flaskService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PredictionController(FlaskIntegrationService flaskService) {
        this.flaskService = flaskService;
    }

    @PostMapping(
        value = "/predict",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> predict(
            @RequestBody Map<String, Object> input,
            @RequestHeader(value = "Cookie", required = false)
            String cookie) {

        if (input == null || !input.keySet().containsAll(REQUIRED_FEATURES)) {
            return error(
                HttpStatus.BAD_REQUEST,
                "Missing one or more required diabetes features."
            );
        }

        // Validate numeric values and reasonable input ranges.
        Map<String, double[]> ranges = Map.of(
            "Pregnancies", new double[]{0, 20},
            "Glucose", new double[]{0, 300},
            "BloodPressure", new double[]{0, 200},
            "SkinThickness", new double[]{0, 100},
            "Insulin", new double[]{0, 1000},
            "BMI", new double[]{0, 100},
            "DiabetesPedigreeFunction", new double[]{0, 3},
            "Age", new double[]{1, 120}
        );

        for (String feature : REQUIRED_FEATURES) {
            Object value = input.get(feature);

            if (!(value instanceof Number)) {
                return error(
                    HttpStatus.BAD_REQUEST,
                    "Feature '" + feature + "' must be numeric."
                );
            }

            double number = ((Number) value).doubleValue();

            if (!Double.isFinite(number)) {
                return error(
                    HttpStatus.BAD_REQUEST,
                    "Feature '" + feature + "' must be finite."
                );
            }

            double[] range = ranges.get(feature);

            if (number < range[0] || number > range[1]) {
                return error(
                    HttpStatus.BAD_REQUEST,
                    "Feature '" + feature + "' must be between "
                        + range[0] + " and " + range[1] + "."
                );
            }

            if ("Pregnancies".equals(feature)
                    && number != Math.floor(number)) {
                return error(
                    HttpStatus.BAD_REQUEST,
                    "Pregnancies must be a whole number."
                );
            }

            if ("Age".equals(feature)
                    && number != Math.floor(number)) {
                return error(
                    HttpStatus.BAD_REQUEST,
                    "Age must be a whole number."
                );
            }
        }

        try {
            String jsonBody = objectMapper.writeValueAsString(input);

            var flaskResponse = flaskService.forwardPrediction(
                jsonBody,
                cookie
            );

            int flaskStatus = flaskResponse.statusCode();

            Map<String, Object> result = objectMapper.readValue(
                flaskResponse.body(),
                new TypeReference<Map<String, Object>>() {}
            );

            if (flaskStatus == 401) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(result);
            }

            if (flaskStatus >= 400) {
                return error(
                    HttpStatus.BAD_GATEWAY,
                    "Flask returned HTTP " + flaskStatus
                );
            }

            return ResponseEntity.ok(result);

        } catch (JsonProcessingException e) {
            return error(
                HttpStatus.BAD_GATEWAY,
                "Could not process the prediction response."
            );
        } catch (Exception e) {
            return error(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Flask prediction service is unavailable."
            );
        }
    }

    private ResponseEntity<Map<String, Object>> error(
            HttpStatus status,
            String message) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", message);

        return ResponseEntity.status(status).body(body);
    }
}
