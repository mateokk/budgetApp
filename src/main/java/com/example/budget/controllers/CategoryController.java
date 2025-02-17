package com.example.budget.controllers;

import com.example.budget.dto.CategoryDTO;
import com.example.budget.mappers.CategoryMapper;
import com.example.budget.model.Category;
import com.example.budget.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @PostMapping
    ResponseEntity<CategoryDTO> addCategory(@RequestBody CategoryDTO categoryDTO) {
        Category category = categoryMapper.toCategory(categoryDTO);
        Category savedCategory = categoryService.addCategory(category);
        return ResponseEntity.ok(categoryMapper.toCategoryDTO(savedCategory));
    }

    @GetMapping
    ResponseEntity<List<CategoryDTO>> getCategories() {
        List<CategoryDTO> categoryDTOs = categoryService.getAllCategories()
                .stream()
                .map(categoryMapper::toCategoryDTO)
                .toList();
        return ResponseEntity.ok(categoryDTOs);
    }

}
