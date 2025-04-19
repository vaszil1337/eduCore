package com.vaszilvalentin.educore.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * This class provides functionality to fetch the current date and time from an external API (TimeZoneDB) asynchronously.
 * It handles potential errors such as timeouts and API issues by falling back to the system's local time when necessary.
 */
public class NetworkTimeChecker {

    // Timeout duration for the HTTP request to the external API
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
    
    // Formatter to parse the date-time string returned by the TimeZoneDB API
    private static final DateTimeFormatter TIMEZONEDB_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Load dotenv file
    private static Dotenv dotenv = Dotenv.load();
    
    // URL of the TimeZoneDB API that provides the time for the Europe/Budapest timezone
    private static final String TIMEZONEDB_URL = "http://api.timezonedb.com/v2.1/get-time-zone?key="+dotenv.get("TIME_API_KEY")+"&format=json&by=zone&zone=Europe/Budapest";

    /**
     * Asynchronously fetches the current time from the TimeZoneDB API.
     * If the API call fails, it falls back to the system time.
     * 
     * @return a CompletableFuture containing the current time as a LocalDateTime object
     */
    public static CompletableFuture<LocalDateTime> getNetworkTimeAsync() {
        // Initiating asynchronous task using CompletableFuture
        return CompletableFuture.supplyAsync(() -> {
            // Create an HttpClient with a specified connection timeout
            HttpClient client = HttpClient.newBuilder()
                .connectTimeout(REQUEST_TIMEOUT)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

            try {
                // Log message indicating the start of the request
                System.out.println("Fetching time from TimeZoneDB API");
                
                // Create an HTTP GET request to the TimeZoneDB API with necessary headers
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TIMEZONEDB_URL))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Accept", "application/json")
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

                // Send the asynchronous HTTP request and get the response
                CompletableFuture<HttpResponse<String>> future = client.sendAsync(
                    request, 
                    HttpResponse.BodyHandlers.ofString()
                );

                // Wait for the response and apply a timeout to prevent long blocking
                HttpResponse<String> response = future.get(REQUEST_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);

                // If the response status is OK (200), process the response
                if (response.statusCode() == 200) {
                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    
                    // Check for any API errors indicated in the response
                    if (json.has("status") && !json.get("status").getAsString().equals("OK")) {
                        String message = json.has("message") ? json.get("message").getAsString() : "Unknown error";
                        throw new RuntimeException("TimeZoneDB API error: " + message);
                    }
                    
                    // Parse and return the time from the JSON response
                    LocalDateTime time = parseTimeZoneDbResponse(json);
                    if (time != null) {
                        return time;
                    }
                    throw new RuntimeException("Failed to parse TimeZoneDB response");
                } else {
                    // Handle unexpected status codes (non-200)
                    throw new RuntimeException("TimeZoneDB API returned status: " + response.statusCode());
                }
            } catch (TimeoutException e) {
                // Handle timeout exception (request timed out)
                throw new RuntimeException("Timeout while connecting to TimeZoneDB API");
            } catch (InterruptedException e) {
                // Handle interruption exception (task interrupted while waiting for response)
                Thread.currentThread().interrupt();
                throw new RuntimeException("Request interrupted");
            } catch (ExecutionException e) {
                // Handle execution exceptions (issues during request execution)
                throw new RuntimeException("Error communicating with TimeZoneDB API: " + e.getMessage());
            } catch (Exception e) {
                // Handle unexpected exceptions (any other issues)
                throw new RuntimeException("Unexpected error: " + e.getMessage());
            }
        }).exceptionally(ex -> {
            // Log the error and fall back to the system's current time if an error occurs
            System.err.println("Falling back to system time. Error: " + ex.getMessage());
            return LocalDateTime.now(ZoneId.of("Europe/Budapest"));
        });
    }

    /**
     * Parses the response from TimeZoneDB API to extract the current time as LocalDateTime.
     * 
     * @param json the JSON object containing the API response
     * @return the parsed LocalDateTime if the time is available, null otherwise
     */
    private static LocalDateTime parseTimeZoneDbResponse(JsonObject json) {
        try {
            // Check if the response contains the "formatted" field with the time string
            if (json.has("formatted")) {
                String formatted = json.get("formatted").getAsString();
                // Parse the time using the defined DateTimeFormatter
                return LocalDateTime.parse(formatted, TIMEZONEDB_FORMATTER);
            }
            // Return null if the time field is not found in the response
            return null;
        } catch (Exception e) {
            // Handle errors during parsing and log the exception
            System.err.println("Failed to parse TimeZoneDB response: " + e.getMessage());
            return null;
        }
    }
}