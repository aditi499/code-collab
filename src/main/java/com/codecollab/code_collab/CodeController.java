package com.codecollab.code_collab;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CodeController {

    private final String PISTON_URL = "https://emkc.org/api/v2/piston/execute";

    @PostMapping("/run")
    public ResponseEntity<String> runCode(@RequestBody CodeRequest req) {

        try {

            if (req == null || req.code == null || req.language == null) {
                return ResponseEntity.ok("No code or language provided");
            }

            RestTemplate restTemplate = new RestTemplate();

            // ---------------- PAYLOAD ----------------
            Map<String, Object> payload = new HashMap<>();

            payload.put("language", mapLanguage(req.language));
            payload.put("version", "*");

            Map<String, Object> file = new HashMap<>();
            file.put("name", getFileName(req.language));
            file.put("content", req.code);

            payload.put("files", new Object[]{file});

            // ---------------- REQUEST ----------------
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(PISTON_URL, entity, Map.class);

            Map body = response.getBody();

            if (body == null) {
                return ResponseEntity.ok("No response from Piston API");
            }

            Map run = (Map) body.get("run");

            if (run == null) {
                return ResponseEntity.ok("Execution failed");
            }

            String stdout = run.get("stdout") != null ? run.get("stdout").toString() : "";
            String stderr = run.get("stderr") != null ? run.get("stderr").toString() : "";

            if (!stderr.isEmpty()) return ResponseEntity.ok(stderr);

            if (!stdout.isEmpty()) return ResponseEntity.ok(stdout);

            return ResponseEntity.ok("No output");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server Error: " + e.getMessage());
        }
    }

    // ---------------- LANGUAGE MAPPING ----------------
    private String mapLanguage(String lang) {

        if (lang == null) return "java";

        return switch (lang.toLowerCase()) {
            case "cpp", "c++" -> "cpp";
            case "python" -> "python3";
            case "java" -> "java";
            default -> "java";
        };
    }

    // ---------------- FILE NAME ----------------
    private String getFileName(String lang) {

        if (lang == null) return "Main.java";

        return switch (lang.toLowerCase()) {
            case "cpp", "c++" -> "main.cpp";
            case "python" -> "main.py";
            default -> "Main.java";
        };
    }
}