package ru.akpsv.main.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.category.dto.CategoryMapper;
import ru.akpsv.main.category.dto.NewCategoryDto;
import ru.akpsv.main.category.model.Category;
import ru.akpsv.main.error.ViolationOfRestrictionsException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    @PersistenceContext
    private final EntityManager em;

    @Override
    public CategoryDto create(NewCategoryDto newCategory) {
        return Optional.ofNullable(CategoryMapper.toCategory(newCategory))
                .map(categoryRepository::save)
                .map(CategoryMapper::toCategoryDto)
                .orElseThrow(() -> new NoSuchElementException("New category not saved"));
    }

    @Override
    public CategoryDto updateCategoryById(Long catId, CategoryDto categoryDto) {
        return Stream.of(categoryDto.toBuilder().id(catId).build())
                .map(CategoryMapper::toCategory)
                .map(categoryRepository::save)
                .map(CategoryMapper::toCategoryDto)
                .findFirst()
                .orElseThrow(() -> new ViolationOfRestrictionsException("Integrity constraint has been violated"));
    }

    @Override
    public List<CategoryDto> get(Integer from, Integer size) {
        return categoryRepository.get(em, from, size).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return categoryRepository.findById(catId)
                .map(CategoryMapper::toCategoryDto)
                .orElseThrow(() -> new NoSuchElementException("Category with id=" + catId + " not exist"));
    }

    @Override
    public void deleteCategoryById(Long catId) {
        categoryRepository.deleteById(catId);
    }
}
