package com.codecollab.code_collab;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
public class CodeController {

    private static final String JUDGE0_URL =
            "https://judge0-ce.p.rapidapi.com/submissions?base64_encoded=false&wait=true";

    private static final String API_KEY = "PUT_YOUR_RAPIDAPI_KEY_HERE";

    @PostMapping("/run")
    public String runCode(@RequestBody CodeRequest req) {

        try {
            Map<String, Object> body = new HashMap<>();

            body.put("source_code", req.code);
            body.put("language_id", getLanguageId(req.language));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-RapidAPI-Key", API_KEY);
            headers.set("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.exchange(
                    JUDGE0_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map result = response.getBody();

            if (result == null) return "No response from Judge0";

            if (result.get("stdout") != null)
                return result.get("stdout").toString();

            if (result.get("stderr") != null)
                return result.get("stderr").toString();

            return result.toString();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private int getLanguageId(String lang) {

        return switch (lang) {
            case "java" -> 62;
            case "python" -> 71;
            case "cpp" -> 54;
            default -> throw new RuntimeException("Unsupported language");
        };
    }
}