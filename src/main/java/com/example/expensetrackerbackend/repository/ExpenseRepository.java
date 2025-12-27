package com.example.expensetrackerbackend.repository;

import com.example.expensetrackerbackend.model.Expense;
import com.example.expensetrackerbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
}
