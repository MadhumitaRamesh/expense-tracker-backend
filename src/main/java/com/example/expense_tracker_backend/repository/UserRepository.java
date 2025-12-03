package com.example.expensetrackerbackend.repository;

import com.example.expensetrackerbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}