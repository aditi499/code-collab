package com.codecollab.code_collab;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
public class CodeController {

    private static final String JUDGE0_URL =
            "https://judge0-ce.p.rapidapi.com/submissions?base64_encoded=false&wait=true";

    // 🔑 PUT YOUR REAL RAPIDAPI KEY HERE
    private static final String API_KEY = "YOUR_REAL_RAPIDAPI_KEY";

    @PostMapping("/run")
    public String runCode(@RequestBody CodeRequest req) {

        try {
            if (req == null || req.code == null) {
                return "No code provided";
            }

            // ---------------- REQUEST BODY ----------------
            Map<String, Object> body = new HashMap<>();
            body.put("source_code", req.code);
            body.put("language_id", getLanguageId(req.language));
            body.put("stdin", "");

            // ---------------- HEADERS ----------------
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-RapidAPI-Key", API_KEY);
            headers.set("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();

            // ---------------- CALL JUDGE0 ----------------
            ResponseEntity<Map> response = restTemplate.exchange(
                    JUDGE0_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map result = response.getBody();

            // ---------------- SAFETY CHECK ----------------
            if (result == null) {
                return "No response from Judge0";
            }

            // ---------------- OUTPUT HANDLING ----------------

            Object compileError = result.get("compile_output");
            if (compileError != null) {
                return "Compilation Error:\n" + compileError;
            }

            Object stderr = result.get("stderr");
            if (stderr != null) {
                return "Runtime Error:\n" + stderr;
            }

            Object stdout = result.get("stdout");
            if (stdout != null) {
                return stdout.toString();
            }

            Object message = result.get("message");
            if (message != null) {
                return message.toString();
            }

            return result.toString();

        } catch (Exception e) {
            return "Server Error: " + e.getMessage();
        }
    }

    // ---------------- LANGUAGE MAPPING ----------------
    private int getLanguageId(String lang) {

        if (lang == null) return 71;

        return switch (lang.toLowerCase()) {
            case "java" -> 62;
            case "python" -> 71;
            case "cpp" -> 54;
            default -> 71;
        };
    }
}