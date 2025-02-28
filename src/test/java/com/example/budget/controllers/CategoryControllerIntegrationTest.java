package com.example.budget.controllers;

import com.example.budget.dto.CategoryDTO;
import com.example.budget.model.Category;
import com.example.budget.repositories.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CategoryControllerIntegrationTest {

    @Autowired
    private  CategoryRepository categoryRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldCreateCategory() throws Exception {
        Category category = new Category();
        category.setName("Shopping");

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Shopping"));
        List<Category> categories = categoryRepository.findAll();
        Assertions.assertEquals(1, categories.size());
        Assertions.assertEquals("Shopping", categories.get(0).getName());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldGetAllCategories() throws Exception {

        Category category1 = new Category(null, "Food");
        Category category2 = new Category(null, "Transport");
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Food"))
                .andExpect(jsonPath("$[1].name").value("Transport"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldUpdateCategoryWhenCategoryExists() throws Exception {
        Category category = new Category();
        category.setName("Food");
        categoryRepository.save(category);
        CategoryDTO categoryDTO = new CategoryDTO(category.getId(), "Barber");

        mockMvc.perform(put("/categories/" + category.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDTO))
        ).andExpect(status().isOk()
        ).andExpect(jsonPath("$.name").value("Barber"));

        Category updatedCategory = categoryRepository.findById(category.getId()).orElseThrow();
        Assertions.assertEquals("Barber", updatedCategory.getName());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldThrowExceptionWhenUpdatedCategoryNotExist() throws Exception{

        Category category = new Category();
        mockMvc.perform(put("/categories/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(category))
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldThrowExceptionWhenDeletedCategoryNotExist() throws Exception{

        mockMvc.perform(delete("/categories/99")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldDeleteCategoryWhenCategoryExists() throws Exception{

        Category category = new Category();
        category.setName("Food");
        Category savedCategory = categoryRepository.save(category);

        mockMvc.perform(delete("/categories/" + savedCategory.getId())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

}