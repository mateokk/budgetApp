package com.example.budget.services;

import com.example.budget.model.Category;
import com.example.budget.model.Transaction;
import com.example.budget.model.User;
import com.example.budget.repositories.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void shouldAddTransaction() {
        Transaction transaction = new Transaction();
        User user = new User();
        Category category = new Category();
        transaction.setDescription("Test transaction");
        transaction.setUser(user);
        transaction.setCategory(category);

        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction savedTransaction = transactionService.addTransaction(transaction, user, category);

        assertEquals(transaction, savedTransaction);
        assertEquals("Test transaction", savedTransaction.getDescription());
        assertEquals(user, savedTransaction.getUser());
        assertEquals(category, savedTransaction.getCategory());
        assertNotNull(savedTransaction);
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    public void shouldGetTransactionsByUser() {
        Transaction transaction = new Transaction();
        User user = new User();
        Category category = new Category();
        transaction.setDescription("Test transaction");
        transaction.setAmount(33.11);
        transaction.setDate(LocalDate.of(2024, 11, 27));
        transaction.setUser(user);
        transaction.setCategory(category);

        when(transactionRepository.findByUser(user)).thenReturn(Collections.singletonList(transaction));

        List<Transaction> transactions = transactionService.getTransactionsByUser(user);

        assertNotNull(transactions);
        assertEquals("Test transaction", transactions.get(0).getDescription());
        assertEquals(1, transactions.size());
        assertEquals(user, transactions.get(0).getUser());
        verify(transactionRepository, times(1)).findByUser(user);
    }

    @Test
    public void shouldCheckIfTransactionExists() {
        Long id = 1L;

        when(transactionRepository.existsById(id)).thenReturn(true);
        boolean result = transactionService.isExists(id);

        assertTrue(result);
        verify(transactionRepository, times(1)).existsById(id);
    }

    @Test
    public void shouldDeleteTransaction() {
        Long id = 1L;

        doNothing().when(transactionRepository).deleteById(id);
        transactionService.deleteTransaction(id);
        verify(transactionRepository, times(1)).deleteById(id);
    }

}
