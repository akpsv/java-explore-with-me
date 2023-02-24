package ru.akpsv.main.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * Подборка событий
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NewCompilationDto {
    private Set<Long> events; //Список идентификаторов событий входящих в подборку
    private Boolean pinned; //Закреплена ли подборка на главной странице сайта
    @NotBlank
    private String title; //Заголовок подборки
}
