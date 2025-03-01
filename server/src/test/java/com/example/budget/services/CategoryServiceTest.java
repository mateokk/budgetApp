package com.example.budget.services;

import com.example.budget.model.Category;
import com.example.budget.repositories.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void shouldAddCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Shopping");

        when(categoryRepository.save(category)).thenReturn(category);
        Category savedCategory = categoryService.addCategory(category);

        assertNotNull(savedCategory);
        assertEquals(category, savedCategory);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    public void shouldGetAllCategories() {
        Category category = new Category();
        category.setId(2L);
        category.setName("Barber");


        when(categoryRepository.findAll()).thenReturn(Collections.singletonList(category));
        List<Category> categories = categoryService.getAllCategories();

        assertNotNull(categories);
        assertEquals(1, categories.size());
        assertEquals("Barber", categories.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    public void shouldFindCategoryById() {
        Long id = 1L;
        Category category = new Category();
        category.setId(id);
        category.setName("Shopping");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(result.get(), category);
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    public void shouldCheckIfCategoryExists() {
        Long id = 1L;

        when(categoryRepository.existsById(id)).thenReturn(true);

        boolean isExists = categoryService.isExists(id);

        assertTrue(isExists);
        verify(categoryRepository, times(1)).existsById(id);
    }

    @Test
    void shouldDeleteCategory() {
        Long id = 1L;

        doNothing().when(categoryRepository).deleteById(id);
        categoryService.deleteCategory(id);

        verify(categoryRepository, times(1)).deleteById(id);
    }


}