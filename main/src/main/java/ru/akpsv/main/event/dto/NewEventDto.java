package ru.akpsv.main.event.dto;

import lombok.Builder;
import lombok.Getter;
import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder(toBuilder = true)
public class NewEventDto {
    @NotBlank
    private String annotation;      //Краткое описание
    @NotNull
    private Long category;          //id категории к которой относится событие
    @NotBlank
    private String description;     //Полное описание события
    @NotBlank
    private String eventDate;       //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private Location location;      //Широта и долгота места проведения события
    private Boolean paid;           //Нужно ли оплачивать участие
    private Integer participantLimit;   //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    private Boolean requestModeration;  //Нужна ли пре-модерация заявок на участие
    @NotBlank
    private String title;           //Заголовок
}
