package com.example.expensetrackerbackend.controller;

import com.example.expensetrackerbackend.model.User;
import com.example.expensetrackerbackend.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired private UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/api/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (userRepository.findByUsername(username) != null) {
            return ResponseEntity.badRequest().body("Username exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
        return ResponseEntity.ok("Registered");
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        User user = userRepository.findByUsername(username);
        if (user == null || !encoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body("Wrong username or password");
        }

        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, "anysecretkey")
                .compact();

        return ResponseEntity.ok(Map.of("token", token));
    }
}