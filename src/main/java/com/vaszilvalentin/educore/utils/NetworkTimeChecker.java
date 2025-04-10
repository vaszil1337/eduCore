package com.vaszilvalentin.educore.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;

public class NetworkTimeChecker {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
    private static final String[] TIME_API_URLS = {
        "https://timeapi.io/api/Time/current/zone?timeZone=UTC",
        "https://worldtimeapi.org/api/timezone/Etc/UTC",
        "https://time.akamai.com/"
    };

    public static CompletableFuture<LocalDateTime> getNetworkTimeAsync() {
        return CompletableFuture.supplyAsync(() -> {
            HttpClient client = HttpClient.newHttpClient();
            
            // Try multiple endpoints in case one fails
            for (String apiUrl : TIME_API_URLS) {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(apiUrl))
                            .timeout(REQUEST_TIMEOUT)
                            .header("Accept", "application/json")
                            .GET()
                            .build();

                    HttpResponse<String> response = client.send(
                        request, 
                        HttpResponse.BodyHandlers.ofString()
                    );

                    if (response.statusCode() == 200) {
                        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                        return parseTimeResponse(json, apiUrl);
                    }
                } catch (IOException e) {
                    // Try next endpoint if this one fails
                    continue;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    // Continue to next endpoint
                    continue;
                }
            }
            
            showError("Could not retrieve time from any server. Using local time as fallback.");
            return LocalDateTime.now(); // fallback
        });
    }

    private static LocalDateTime parseTimeResponse(JsonObject json, String apiUrl) {
        try {
            if (apiUrl.contains("timeapi.io")) {
                // Parse timeapi.io response
                int year = json.get("year").getAsInt();
                int month = json.get("month").getAsInt();
                int day = json.get("day").getAsInt();
                int hour = json.get("hour").getAsInt();
                int minute = json.get("minute").getAsInt();
                int second = json.get("seconds").getAsInt();
                return LocalDateTime.of(year, month, day, hour, minute, second);
            } else if (apiUrl.contains("worldtimeapi.org")) {
                // Parse worldtimeapi.org response
                String datetime = json.get("datetime").getAsString();
                return LocalDateTime.parse(datetime.substring(0, 19));
            } else if (apiUrl.contains("akamai.com")) {
                // Parse Akamai response (plain text timestamp)
                long timestamp = Long.parseLong(json.getAsString());
                return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
            }
        } catch (Exception e) {
            showError("Error parsing time response: " + e.getMessage());
        }
        return LocalDateTime.now();
    }

    private static void showError(String message) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(
                null,
                message,
                "Network Time Error",
                JOptionPane.ERROR_MESSAGE
            )
        );
    }
}