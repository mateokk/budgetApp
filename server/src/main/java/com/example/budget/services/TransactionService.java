package com.example.budget.services;

import com.example.budget.model.Category;
import com.example.budget.model.Transaction;
import com.example.budget.model.User;
import com.example.budget.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;


    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction addTransaction(Transaction transaction, User user, Category category) {
        transaction.setUser(user);
        transaction.setCategory(category);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUser(User user) {
        return transactionRepository.findByUser(user);
    }

    public boolean isExists(Long id) {
        return transactionRepository.existsById(id);
    }
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getTransactionsByUserAndCategory(User user, Long categoryId) {
        return transactionRepository.findByUserAndCategoryId(user, categoryId);
    }
}
