package ru.akpsv.main.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.category.dto.NewCategoryDto;

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
        return categoryService.create(newCategory);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto update(@Valid @RequestBody CategoryDto categoryDto, @PathVariable Long catId) {
        return categoryService.updateCategoryById(catId, categoryDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable Long catId) {
        categoryService.deleteCategoryById(catId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> get(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.get(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        return categoryService.getCategoryById(catId);
    }
}
