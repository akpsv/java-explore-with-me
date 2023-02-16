package ru.akpsv.main.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.category.dto.CategoryMapper;
import ru.akpsv.main.category.dto.NewCategoryDto;
import ru.akpsv.main.category.model.Category;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;
    @PersistenceContext
    EntityManager em;
    @Override
    public Optional<CategoryDto> create(NewCategoryDto newCategory) {
        Category category = CategoryMapper.toCategory(newCategory);
        Category savedCategory = categoryRepository.save(category);
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(savedCategory);
        return Optional.of(categoryDto);
    }

    @Override
    public Optional<CategoryDto> updateCategoryById(Long catId, CategoryDto categoryDto) {
        categoryDto = categoryDto.toBuilder().id(catId).build();
        Category category = CategoryMapper.toCategory(categoryDto);
        Category updatedCategory = categoryRepository.save(category);
        CategoryDto updatedCategoryDto = CategoryMapper.toCategoryDto(updatedCategory);
        return Optional.of(updatedCategoryDto);
    }

    @Override
    public List<Category> get(Integer from, Integer size) {
        return categoryRepository.get(em, from, size);
    }

    @Override
    public Category getCategoryById(Long catId) {
        return categoryRepository.findById(catId).get();
    }

    @Override
    public void deleteCategoryById(Long catId) {
        categoryRepository.deleteById(catId);
    }
}
