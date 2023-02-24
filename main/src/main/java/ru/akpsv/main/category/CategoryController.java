package ru.akpsv.main.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.category.dto.NewCategoryDto;
import ru.akpsv.main.category.model.Category;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody NewCategoryDto newCategory) {
        return categoryService.create(newCategory).get();
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto update(@Valid @RequestBody CategoryDto categoryDto, @PathVariable Long catId) {
        return categoryService.updateCategoryById(catId, categoryDto).get();
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable Long catId) {
        categoryService.deleteCategoryById(catId);
    }

    @GetMapping("/categories")
    public List<Category> get(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.get(from, size);
    }
    @GetMapping("/categories/{catId}")
    public Category getCategoryById(@PathVariable Long catId) {
        return categoryService.getCategoryById(catId);
    }
}
