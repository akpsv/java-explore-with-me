package ru.akpsv.main.category;

import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategory);

    void deleteCategoryById(Long catId);

    CategoryDto updateCategoryById(Long catId, CategoryDto categoryDto);

    List<CategoryDto> get(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);
}
