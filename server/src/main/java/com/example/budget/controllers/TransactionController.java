package com.example.budget.controllers;

import com.example.budget.mappers.TransactionMapper;
import com.example.budget.model.Category;
import com.example.budget.model.Transaction;
import com.example.budget.dto.TransactionDTO;
import com.example.budget.model.User;
import com.example.budget.services.CategoryService;
import com.example.budget.services.TransactionService;
import com.example.budget.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {


    private final TransactionService transactionService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TransactionMapper transactionMapper;

    public TransactionController(TransactionService transactionService, UserService userService, CategoryService categoryService, TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.transactionMapper = transactionMapper;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> addTransaction(@RequestBody TransactionDTO transactionDTO, Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Category category = categoryService.findById(transactionDTO.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        Transaction transaction = transactionMapper.toTransaction(transactionDTO);
        Transaction savedTransaction = transactionService.addTransaction(transaction, user, category);

        return ResponseEntity.ok(transactionMapper.toTransactionDTO(savedTransaction));
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getTransactions(
            @RequestParam(required = false) Long categoryId,
            Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Transaction> transactions;
        if(categoryId != null) {
            if(categoryService.isExists(categoryId)) {
                transactions = transactionService.getTransactionsByUserAndCategory(user, categoryId);
            } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        } else {
            transactions = transactionService.getTransactionsByUser(user);
        }
        List<TransactionDTO> transactionDTOs = transactions
                .stream()
                .map(transactionMapper::toTransactionDTO)
                .toList();
        return ResponseEntity.ok(transactionDTOs);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id,
                                                            @RequestBody TransactionDTO transactionDTO, Principal principal) {
        if (!transactionService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Category category = categoryService.findById(transactionDTO.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        transactionDTO.setId(id);
        Transaction transaction = transactionMapper.toTransaction(transactionDTO);
        Transaction savedTransaction = transactionService.addTransaction(transaction, user, category);
        return ResponseEntity.ok(transactionMapper.toTransactionDTO(savedTransaction));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        if (!transactionService.isExists(id)) {
            return ResponseEntity.notFound().build();
        }
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
