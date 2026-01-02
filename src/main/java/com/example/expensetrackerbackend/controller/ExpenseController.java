package com.example.expensetrackerbackend.controller;

import com.example.expensetrackerbackend.model.Expense;
import com.example.expensetrackerbackend.model.User;
import com.example.expensetrackerbackend.repository.ExpenseRepository;
import com.example.expensetrackerbackend.repository.UserRepository;
import com.example.expensetrackerbackend.util.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getExpenses() {
        try {
            System.out.println("GET /api/expenses - Request received");
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("Authenticated user: " + username);
            User user = userRepository.findByUsername(username);
            
            List<Expense> expenses = expenseRepository.findByUser(user);
            
            List<ExpenseDto> dtos = expenses.stream().map(e -> {
                ExpenseDto dto = new ExpenseDto();
                dto.id = e.getId();
                dto.amount = e.getAmount();
                dto.category = e.getCategory();
                dto.note = e.getNote();
                dto.date = e.getDate().toString();
                return dto;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching expenses");
        }
    }

    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody ExpenseDto expenseDto) {
        try {
            System.out.println("POST /api/expenses - Request received");
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("Authenticated user: " + username);
            
            User user = userRepository.findByUsername(username);
            if (user == null) {
                System.out.println("User not found in database: " + username);
                return ResponseEntity.status(401).body("User not found");
            }

            Expense expense = new Expense();
            expense.setAmount(expenseDto.amount);
            expense.setCategory(expenseDto.category);
            expense.setNote(expenseDto.note);
            expense.setDate(LocalDate.parse(expenseDto.date));
            expense.setUser(user);

            expenseRepository.save(expense);
            System.out.println("Expense saved successfully for user: " + username);
            return ResponseEntity.ok("Expense saved successfully");
        } catch (Exception e) {
            System.err.println("Error saving expense: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error saving expense: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username);
            
            Expense expense = expenseRepository.findById(id).orElse(null);
            if (expense != null && expense.getUser().getId().equals(user.getId())) {
                expenseRepository.delete(expense);
                return ResponseEntity.ok("Expense deleted successfully");
            } else {
                return ResponseEntity.status(403).body("Unauthorized to delete this expense");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting expense");
        }
    }

    public static class ExpenseDto {
        public Long id;
        public Double amount;
        public String category;
        public String note;
        public String date;
    }
}
