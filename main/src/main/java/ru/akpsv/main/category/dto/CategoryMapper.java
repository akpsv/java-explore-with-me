package ru.akpsv.main.category.dto;

import ru.akpsv.main.category.model.Category;

public class CategoryMapper {
    public static Category toCategory(NewCategoryDto newCategory) {
        return Category.builder()
                .name(newCategory.getName())
                .build();
    }

    public static Category toCategory(CategoryDto categroDto) {
        return Category.builder()
                .id(categroDto.getId())
                .name(categroDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
