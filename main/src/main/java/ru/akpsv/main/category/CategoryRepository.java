package ru.akpsv.main.category;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.akpsv.main.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
