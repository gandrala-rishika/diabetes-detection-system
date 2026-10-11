package com.diabetes.springboot_backend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.stereotype.Service;

@Service
public class FlaskIntegrationService {

    private static final String FLASK_BASE_URL =
            System.getenv().getOrDefault(
                    "FLASK_BASE_URL",
                    "http://127.0.0.1:5001"
            );

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    public int checkFlaskConnection() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(FLASK_BASE_URL + "/login"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<Void> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.discarding()
        );

        return response.statusCode();
    }

    public HttpResponse<String> forwardPrediction(
            String jsonBody, String cookie) throws Exception {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(FLASK_BASE_URL + "/predict"))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        if (cookie != null && !cookie.isBlank()) {
            builder.header("Cookie", cookie);
        }

        return httpClient.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    public HttpResponse<String> forwardHistory(
            String cookie) throws Exception {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(FLASK_BASE_URL + "/api/history"))
                .timeout(Duration.ofSeconds(10))
                .GET();

        if (cookie != null && !cookie.isBlank()) {
            builder.header("Cookie", cookie);
        }

        return httpClient.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }
}
