package ru.akpsv.main.compilation.dto;

import lombok.*;
import ru.akpsv.main.event.dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Подборка событий
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CompilationDto {
    private List<EventShortDto> events; //Список событий входящих в подборку
    @NotNull
    private Long id; //Идентификатор
    @NotNull
    private Boolean pinned; //Закреплена ли подборка на главной странице сайта
    @NotBlank
    private String title; //Заголовок подборки

}
