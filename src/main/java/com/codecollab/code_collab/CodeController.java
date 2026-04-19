package com.codecollab.code_collab;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CodeController {

    // 🔴 REPLACE THESE WITH YOUR REAL JDoodle KEYS
    private final String CLIENT_ID = "8476132b8635e3c9d2c4be34e1fe24dd";
    private final String CLIENT_SECRET = "3a4d40d7199a032b69428a0af528e5ea54a37146cc0fc18be534a71543e81500";

    private final String JD_URL = "https://api.jdoodle.com/v1/execute";

    @PostMapping("/run")
    public ResponseEntity<String> runCode(@RequestBody CodeRequest req) {

        try {

            if (req == null || req.code == null || req.language == null) {
                return ResponseEntity.ok("No code or language provided");
            }

            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> payload = new HashMap<>();

            payload.put("clientId", CLIENT_ID);
            payload.put("clientSecret", CLIENT_SECRET);
            payload.put("script", req.code);

            // ---------------- LANGUAGE ----------------
            String lang = mapLanguage(req.language);

            payload.put("language", lang);

            // ---------------- VERSION INDEX FIX ----------------
            if (lang.equals("java")) {
                payload.put("versionIndex", "4");
            } else if (lang.equals("python3")) {
                payload.put("versionIndex", "4");
            } else {
                payload.put("versionIndex", "0");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(JD_URL, entity, Map.class);

            Map body = response.getBody();

            if (body == null) {
                return ResponseEntity.ok("No response from JDoodle");
            }

            String output = body.get("output") != null
                    ? body.get("output").toString()
                    : "No output";

            return ResponseEntity.ok(output);

        } catch (Exception e) {
            return ResponseEntity.ok("Execution Error: " + e.getMessage());
        }
    }

    // ---------------- LANGUAGE MAPPING ----------------
    private String mapLanguage(String lang) {

        if (lang == null) return "java";

        return switch (lang.toLowerCase()) {
            case "cpp", "c++" -> "cpp17";
            case "python" -> "python3";
            case "java" -> "java";
            default -> "java";
        };
    }
}