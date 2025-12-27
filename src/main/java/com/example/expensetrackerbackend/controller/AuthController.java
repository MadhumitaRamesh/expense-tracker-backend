package com.example.expensetrackerbackend.controller;

import com.example.expensetrackerbackend.model.User;
import com.example.expensetrackerbackend.repository.UserRepository;
import com.example.expensetrackerbackend.security.JwtService;
import com.example.expensetrackerbackend.util.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
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
        try {
            // Decrypt username and password
            String username = AESUtil.decrypt(userDto.username);
            String password = AESUtil.decrypt(userDto.password);
            
            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                return ResponseEntity.badRequest().body(AESUtil.encrypt("username and password required"));
            }

            if (userRepository.findByUsername(username) != null) {
                return ResponseEntity.status(400).body(AESUtil.encrypt("Username already exists"));
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);

            return ResponseEntity.ok(AESUtil.encrypt("Registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Decryption error");
        }
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        try {
            // Decrypt username and password
            String username = AESUtil.decrypt(userDto.username);
            String password = AESUtil.decrypt(userDto.password);
            
            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
                return ResponseEntity.badRequest().body(AESUtil.encrypt("username and password required"));
            }

            User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(401).body(AESUtil.encrypt("User does not exist"));
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.status(401).body(AESUtil.encrypt("Incorrect password"));
            }

            String token = jwtService.generateToken(user.getUsername());

            // Encrypt response data
            Map<String, Object> body = new HashMap<>();
            body.put("token", AESUtil.encrypt(token));
            body.put("username", AESUtil.encrypt(user.getUsername()));

            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Decryption error");
        }
    }

    // DTO
    public static class UserDto {
        public String username;
        public String password;
    }
}

