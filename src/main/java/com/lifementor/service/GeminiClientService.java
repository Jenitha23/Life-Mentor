package com.lifementor.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiClientService {

    private static final Logger log = LoggerFactory.getLogger(GeminiClientService.class);

    private final RestTemplate restTemplate;

    @Value("${app.ai.gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String apiUrl;

    @Value("${app.ai.gemini.api.key:}")
    private String apiKey;

    @Value("${app.ai.gemini.model:gemini-1.5-flash}")
    private String model;

    @Value("${app.ai.gemini.max-output-tokens:1024}")
    private int maxOutputTokens;

    @Value("${app.ai.gemini.temperature:0.7}")
    private double temperature;

    public GeminiClientService() {
        this.restTemplate = new RestTemplate();
    }

    @PostConstruct
    public void logConfigurationStatus() {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API key is not configured. Set GEMINI_API_KEY or AI_CHAT_API_KEY to enable AI responses.");
        } else {
            log.info("Gemini client configured with model {}", model);
        }
    }

    public GeminiResponse generateContent(String systemInstruction,
                                          List<ChatTurn> history,
                                          String userMessage,
                                          boolean expectJson) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key is not configured");
        }

        String url = String.format("%s/%s:generateContent?key=%s", apiUrl, model, apiKey);
        Map<String, Object> requestBody = new HashMap<>();

        if (systemInstruction != null && !systemInstruction.isBlank()) {
            requestBody.put("systemInstruction", Map.of(
                    "parts", List.of(Map.of("text", systemInstruction))
            ));
        }

        List<Map<String, Object>> contents = new ArrayList<>();
        if (history != null) {
            for (ChatTurn turn : history) {
                if (turn == null || turn.content() == null || turn.content().isBlank()) {
                    continue;
                }

                contents.add(Map.of(
                        "role", normalizeRole(turn.role()),
                        "parts", List.of(Map.of("text", turn.content()))
                ));
            }
        }

        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userMessage))
        ));
        requestBody.put("contents", contents);

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", temperature);
        generationConfig.put("maxOutputTokens", maxOutputTokens);
        if (expectJson) {
            generationConfig.put("responseMimeType", "application/json");
        }
        requestBody.put("generationConfig", generationConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                Map.class
        );

        Map<String, Object> body = response.getBody();
        String text = extractResponseText(body);
        Integer totalTokens = extractTotalTokens(body);

        if (text == null || text.isBlank()) {
            throw new IllegalStateException("Gemini returned an empty response");
        }

        log.debug("Gemini response generated with model {}", model);
        return new GeminiResponse(text, totalTokens, model);
    }

    public String getConfiguredModel() {
        return model;
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return "user";
        }

        if ("ASSISTANT".equalsIgnoreCase(role) || "model".equalsIgnoreCase(role)) {
            return "model";
        }

        return "user";
    }

    private String extractResponseText(Map<String, Object> body) {
        if (body == null) {
            return null;
        }

        Object candidatesObj = body.get("candidates");
        if (!(candidatesObj instanceof List<?> candidates) || candidates.isEmpty()) {
            return null;
        }

        Object firstCandidate = candidates.get(0);
        if (!(firstCandidate instanceof Map<?, ?> candidateMap)) {
            return null;
        }

        Object contentObj = candidateMap.get("content");
        if (!(contentObj instanceof Map<?, ?> contentMap)) {
            return null;
        }

        Object partsObj = contentMap.get("parts");
        if (!(partsObj instanceof List<?> parts) || parts.isEmpty()) {
            return null;
        }

        StringBuilder text = new StringBuilder();
        for (Object partObj : parts) {
            if (partObj instanceof Map<?, ?> partMap) {
                Object piece = partMap.get("text");
                if (piece instanceof String partText) {
                    if (!text.isEmpty()) {
                        text.append('\n');
                    }
                    text.append(partText);
                }
            }
        }

        return text.toString();
    }

    private Integer extractTotalTokens(Map<String, Object> body) {
        if (body == null) {
            return null;
        }

        Object usageObj = body.get("usageMetadata");
        if (!(usageObj instanceof Map<?, ?> usageMap)) {
            return null;
        }

        Object totalTokens = usageMap.get("totalTokenCount");
        if (totalTokens instanceof Number number) {
            return number.intValue();
        }

        return null;
    }

    public record ChatTurn(String role, String content) { }

    public record GeminiResponse(String text, Integer totalTokens, String model) { }
}
