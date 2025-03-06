package com.example.budget.repositories;

import com.example.budget.model.Transaction;
import com.example.budget.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
List<Transaction> findByUser (User user);
    List<Transaction> findByUserAndCategoryId(User user, Long categoryId);
}
