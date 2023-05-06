package ru.akpsv.main.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NewCategoryDto {
    @NotBlank
    private String name;
}
