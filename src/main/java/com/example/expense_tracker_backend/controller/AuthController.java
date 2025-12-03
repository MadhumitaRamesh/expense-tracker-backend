package com.example.expensetrackerbackend.controller;

import com.example.expensetrackerbackend.model.User;
import com.example.expensetrackerbackend.repository.UserRepository;
import com.example.expensetrackerbackend.util.Decrypt;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Allow frontend
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final ObjectMapper mapper = new ObjectMapper();

    // REGISTER
    @PostMapping("/api/register")
    public ResponseEntity<String> register(@RequestBody String encryptedData) {
        try {
            String json = Decrypt.decrypt(encryptedData); // Decrypt what frontend sent
            Map<String, String> data = mapper.readValue(json, Map.class);

            String username = data.get("username");
            String rawPassword = data.get("password");

            if (userRepository.findByUsername(username) != null) {
                return ResponseEntity.badRequest().body("Username already taken");
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(encoder.encode(rawPassword)); // Save hashed password
            userRepository.save(user);

            return ResponseEntity.ok("Registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // LOGIN
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody String encryptedData) {
        try {
            String json = Decrypt.decrypt(encryptedData);
            Map<String, String> data = mapper.readValue(json, Map.class);

            String username = data.get("username");
            String rawPassword = data.get("password");

            User user = userRepository.findByUsername(username);
            if (user == null || !encoder.matches(rawPassword, user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Wrong username or password"));
            }

            // Create JWT token (boss's authorisation mechanism)
            String token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                    .signWith(SignatureAlgorithm.HS512, "mysecretkey123")
                    .compact();

            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Login failed"));
        }
    }
}