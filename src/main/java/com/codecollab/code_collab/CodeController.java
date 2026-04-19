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
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> payload = new HashMap<>();

            String language = mapLanguage(req.language);

            payload.put("language", language);
            payload.put("version", "*");

            Map<String, String> file = new HashMap<>();
            file.put("content", req.code);

            payload.put("files", new Object[]{file});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(PISTON_URL, entity, Map.class);

            Map body = response.getBody();

            if (body == null) {
                return ResponseEntity.ok("No output");
            }

            Object run = body.get("run");

            if (run instanceof Map) {
                Map runMap = (Map) run;

                String output = (String) runMap.get("stdout");
                String error = (String) runMap.get("stderr");

                if (error != null && !error.isEmpty()) {
                    return ResponseEntity.ok(error);
                }

                return ResponseEntity.ok(output != null ? output : "No output");
            }

            return ResponseEntity.ok("Invalid response from execution API");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server Error: " + e.getMessage());
        }
    }

    // ---------------- LANGUAGE MAPPING ----------------
    private String mapLanguage(String lang) {

        if (lang == null) return "java";

        return switch (lang.toLowerCase()) {
            case "cpp" -> "cpp";
            case "python" -> "python3";
            case "java" -> "java";
            default -> "java";
        };
    }
}