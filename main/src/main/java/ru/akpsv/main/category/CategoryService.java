package ru.akpsv.main.category;

import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.category.dto.NewCategoryDto;
import ru.akpsv.main.category.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategory);

    void deleteCategoryById(Long catId);

    CategoryDto updateCategoryById(Long catId, CategoryDto categoryDto);

    List<Category> get(Integer from, Integer size);

    Category getCategoryById(Long catId);
}
