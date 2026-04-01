package ru.practicum.ewm.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHit;
import ru.practicum.ewm.dto.ViewStats;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class StatisticClient {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String application;

    private final String serviceUrl;

    private final ObjectMapper objectMapper;

    private final HttpClient httpClient;

    public StatisticClient(
            @Value("${spring.application.name}") String application,
            @Value("${services.statistics-service.url:http://localhost:9090}") String serviceUrl,
            ObjectMapper objectMapper
    ) {
        this.application = application;
        this.serviceUrl = serviceUrl;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    public void endpointHit(HttpServletRequest request) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(application);
        endpointHit.setUri(request.getRequestURI());
        endpointHit.setIp(request.getRemoteAddr());
        endpointHit.setTimestamp(LocalDateTime.now());

        try {
            String body = objectMapper.writeValueAsString(endpointHit);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(serviceUrl + "/hit"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200 && response.statusCode() != 201) {
                System.err.println("Ошибка при отправке статистики: " + response.statusCode());
            }

        } catch (Exception e) {
            System.err.println("Не удалось отправить статистику: " + e.getMessage());
        }
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        try {
            String startParam = URLEncoder.encode(start.format(DATE_TIME_FORMATTER), StandardCharsets.UTF_8);
            String endParam = URLEncoder.encode(end.format(DATE_TIME_FORMATTER), StandardCharsets.UTF_8);

            StringBuilder uriBuilder = new StringBuilder(serviceUrl + "/stats")
                    .append("?start=").append(startParam)
                    .append("&end=").append(endParam)
                    .append("&unique=").append(unique);

            if (uris != null && !uris.isEmpty()) {
                for (String uri : uris) {
                    uriBuilder.append("&uris=").append(URLEncoder.encode(uri, StandardCharsets.UTF_8));
                }
            }

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(uriBuilder.toString()))
                    .GET()
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                return objectMapper.readValue(
                        response.body(),
                        new TypeReference<>() {
                        }
                );
            } else {
                System.err.println("Ошибка при получении статистики: " + response.statusCode());
                return Collections.emptyList();
            }

        } catch (Exception e) {
            System.err.println("Не удалось получить статистику: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}