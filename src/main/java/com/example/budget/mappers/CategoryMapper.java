package com.example.budget.mappers;

import com.example.budget.dto.CategoryDTO;
import com.example.budget.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryDTO categoryDTO);
    CategoryDTO toCategoryDTO(Category category);
}
