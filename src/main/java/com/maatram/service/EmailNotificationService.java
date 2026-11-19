package com.maatram.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maatram.dto.ContributionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {
    private static final String RESEND_ENDPOINT = "https://api.resend.com/emails";

    private final ObjectMapper objectMapper;

    public void notifyAdmin(ContributionResponse contribution) {
        String apiKey = System.getenv("RESEND_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Email notification is not configured. Set RESEND_API_KEY.");
        }

        String adminEmail = System.getenv().getOrDefault("ADMIN_EMAIL", "k.vigneshsivan@gmail.com");
        String from = System.getenv().getOrDefault("MAIL_FROM", "onboarding@resend.dev");
        String subject = "New volunteer contribution: " + contribution.eventName();
        String html = """
                <h2>New volunteer contribution</h2>
                <p><strong>Volunteer:</strong> %s</p>
                <p><strong>Activity:</strong> %s</p>
                <p><strong>Date:</strong> %s</p>
                <p><strong>Description:</strong> %s</p>
                <p>Open the admin dashboard to review the proof photos and approve the contribution.</p>
                """.formatted(
                escape(contribution.volunteerName()),
                escape(contribution.eventName()),
                contribution.activityDate(),
                escape(contribution.description()));

        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "from", from,
                    "to", new String[]{adminEmail},
                    "subject", subject,
                    "html", html));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RESEND_ENDPOINT))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Email notification failed (" + response.statusCode() + ").");
            }
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Email notification payload could not be created.", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Email notification request failed.", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Email notification was interrupted.", exception);
        }
    }

    private String escape(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
