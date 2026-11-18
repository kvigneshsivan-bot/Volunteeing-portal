package com.maatram.controller;

import com.maatram.dto.BroadcastRequest;
import com.maatram.dto.BroadcastResult;
import com.maatram.entity.Role;
import com.maatram.entity.User;
import com.maatram.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final UserRepository users;

    @PostMapping("/broadcast")
    public BroadcastResult broadcast(@Valid @RequestBody BroadcastRequest request) throws Exception {
        String sid = System.getenv("TWILIO_ACCOUNT_SID");
        String token = System.getenv("TWILIO_AUTH_TOKEN");
        String from = System.getenv("TWILIO_FROM_NUMBER");
        if (sid == null || sid.isBlank() || token == null || token.isBlank() || from == null || from.isBlank()) {
            throw new IllegalStateException("SMS is not configured. Set TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN, and TWILIO_FROM_NUMBER.");
        }

        List<User> recipients = users.findByRole(Role.VOLUNTEER).stream()
                .filter(user -> user.getMobileNumber() != null && !user.getMobileNumber().isBlank())
                .toList();
        int sent = 0;
        int failed = 0;
        HttpClient client = HttpClient.newHttpClient();
        String basicAuth = Base64.getEncoder().encodeToString((sid + ":" + token).getBytes(StandardCharsets.UTF_8));

        for (User recipient : recipients) {
            String phone = recipient.getMobileNumber().replaceAll("[^0-9]", "");
            if (phone.length() == 10) phone = "91" + phone;
            if (!phone.startsWith("+")) phone = "+" + phone;
            String form = "To=" + URLEncoder.encode(phone, StandardCharsets.UTF_8)
                    + "&From=" + URLEncoder.encode(from, StandardCharsets.UTF_8)
                    + "&Body=" + URLEncoder.encode(request.message().trim(), StandardCharsets.UTF_8);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.twilio.com/2010-04-01/Accounts/" + sid + "/Messages.json"))
                    .header("Authorization", "Basic " + basicAuth)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form))
                    .build();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) sent++;
            else failed++;
        }
        return new BroadcastResult(sent, failed, recipients.size(),
                failed == 0 ? "All SMS messages queued" : "SMS broadcast partially failed");
    }
}
