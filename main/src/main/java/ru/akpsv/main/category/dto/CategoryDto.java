package ru.akpsv.main.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder(toBuilder = true)
public class CategoryDto {
    private int id;
    private String name;
}
