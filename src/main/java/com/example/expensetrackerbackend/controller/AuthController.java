package com.example.expensetrackerbackend.controller;

import com.example.expensetrackerbackend.model.User;
import com.example.expensetrackerbackend.repository.UserRepository;
import com.example.expensetrackerbackend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        if (userDto.username == null || userDto.password == null) {
            return ResponseEntity.badRequest().body("username and password required");
        }

        if (userRepository.findByUsername(userDto.username) != null) {
            return ResponseEntity.status(400).body("Username already exists");
        }

        User user = new User();
        user.setUsername(userDto.username);
        user.setPassword(passwordEncoder.encode(userDto.password));
        userRepository.save(user);

        return ResponseEntity.ok("Registered successfully");
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        if (userDto.username == null || userDto.password == null) {
            return ResponseEntity.badRequest().body("username and password required");
        }

        User user = userRepository.findByUsername(userDto.username);
        if (user == null) {
            return ResponseEntity.status(401).body("User does not exist");
        }

        if (!passwordEncoder.matches(userDto.password, user.getPassword())) {
            return ResponseEntity.status(401).body("Incorrect password");
        }

        String token = jwtService.generateToken(user.getUsername());

        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("username", user.getUsername());

        return ResponseEntity.ok(body); // 200 with JSON: { token: "...", username: "..." }
    }

    // DTO
    public static class UserDto {
        public String username;
        public String password;
    }
}

