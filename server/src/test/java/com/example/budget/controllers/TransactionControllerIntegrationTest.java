package com.example.budget.controllers;

import com.example.budget.dto.TransactionDTO;
import com.example.budget.dto.UserLoginDTO;
import com.example.budget.dto.UserRegisterDTO;
import com.example.budget.model.Category;
import com.example.budget.model.Transaction;
import com.example.budget.model.User;
import com.example.budget.repositories.CategoryRepository;
import com.example.budget.repositories.TransactionRepository;
import com.example.budget.repositories.UserRepository;
import com.example.budget.services.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class TransactionControllerIntegrationTest {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    public String userLogin() throws Exception {
        UserRegisterDTO userDTO = new UserRegisterDTO("testUser", "test@email", "testPassword");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk());
        UserLoginDTO loginDTO = new UserLoginDTO("testUser", "testPassword");
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn();
        return loginResult.getResponse().getContentAsString();
    }

    public void saveTestTransaction() {
        User user = userRepository.findByUsername("testUser").orElseThrow();
        Category category = new Category(null, "testCategory");
        Category savedCategory = categoryRepository.save(category);
        Transaction transaction = new Transaction();
        transaction.setDescription("Test 1");
        transaction.setCategory(savedCategory);
        transaction.setUser(user);
        transactionRepository.save(transaction);
    }

    @Test
    public void shouldAddTransaction() throws Exception {
        Category category = new Category(null, "Shopping");
        categoryRepository.save(category);
        TransactionDTO transactionDTO = new TransactionDTO("Test Transaction", 999.13, category.getId());

        String token = userLogin();
        mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO))
        ).andExpect(status().isOk()
        ).andExpect(jsonPath("$.description").value("Test Transaction")
        ).andExpect(jsonPath("$.amount").value(999.13)
        ).andExpect(jsonPath("$.categoryId").value(category.getId()));

        List<Transaction> transactions = transactionRepository.findAll();
        assertEquals(1, transactions.size());
        assertEquals("Test Transaction", transactions.get(0).getDescription());
        assertEquals("Shopping", transactions.get(0).getCategory().getName());
    }

    @Test
    public void shouldReturnNotFoundExceptionWhenCategoryDoesNotExist() throws Exception {
        TransactionDTO transactionDTO = new TransactionDTO("Test Transaction", 999.13, 1L);
        String token = userLogin();
        mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO))
        ).andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnAllTransactionsByUser() throws Exception {
        String token = userLogin();
        User user = userRepository.findByUsername("testUser").orElseThrow();
        Category category = new Category(null, "testCategory");
        Category savedCategory = categoryRepository.save(category);
        Transaction transaction1 = new Transaction();
        transaction1.setDescription("Test 1");
        transaction1.setCategory(savedCategory);
        transaction1.setUser(user);
        Transaction transaction2 = new Transaction();
        transaction2.setCategory(savedCategory);
        transaction2.setDescription("Test 2");
        transaction2.setUser(user);
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        mockMvc.perform(get("/transactions")
                .header("Authorization", "Bearer " + token)
        ).andExpect(status().isOk()
        ).andExpect(jsonPath("$.size()").value(2)
        ).andExpect(jsonPath("$[0].description").value("Test 1")
        ).andExpect(jsonPath("$[1].description").value("Test 2"));
    }

    @Test
    public void shouldUpdateTransaction() throws Exception {
        String token = userLogin();
        User user = userRepository.findByUsername("testUser").orElseThrow();
        Category category = new Category(null, "testCategory");
        Category savedCategory = categoryRepository.save(category);
        Transaction transaction = new Transaction();
        transaction.setDescription("Test Transaction");
        transaction.setCategory(savedCategory);
        transaction.setUser(user);
        Transaction savedTransaction = transactionRepository.save(transaction);
        TransactionDTO updatedTransaction = new TransactionDTO("Updated Transaction", 10.0, savedCategory.getId());
        mockMvc.perform(put("/transactions/" + savedTransaction.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTransaction))
        ).andExpect(status().isOk()
        ).andExpect(jsonPath("$.description").value("Updated Transaction")
        ).andExpect(jsonPath("$.amount").value(10.0));
    }

    @Test
    public void shouldDeleteTransaction() throws Exception {
        String token = userLogin();
        User user = userRepository.findByUsername("testUser").orElseThrow();
        Category category = new Category(null, "testCategory");
        Category savedCategory = categoryRepository.save(category);
        Transaction transaction = new Transaction();
        transaction.setDescription("Test Transaction");
        transaction.setCategory(savedCategory);
        transaction.setUser(user);
        Transaction savedTransaction = transactionRepository.save(transaction);
        mockMvc.perform(delete("/transactions/" + savedTransaction.getId())
                .header("Authorization", "Bearer " + token)
                ).andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnUnauthorizedWhenNoAuthorizationProvided() throws Exception {
        mockMvc.perform(get("/transactions")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnNotFoundExceptionWhenUpdatedTransactionDoesNotExist() throws Exception{
        String token = userLogin();
        Category category = new Category(null, "testCategory");
        Category savedCategory = categoryRepository.save(category);
        TransactionDTO updatedTransaction = new TransactionDTO("Updated Transaction", 10.0, savedCategory.getId());
        mockMvc.perform(put("/transactions/" + 99)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTransaction))
        ).andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundExceptionWhenDeletedTransactionDoesNotExist() throws Exception{
        String token = userLogin();
        mockMvc.perform(delete("/transactions/" + 99)
                .header("Authorization", "Bearer " + token)
        ).andExpect(status().isNotFound());
    }
}
