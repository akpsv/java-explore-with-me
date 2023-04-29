package ru.akpsv.main.event.dto;

import lombok.Builder;
import lombok.Getter;
import ru.akpsv.main.event.model.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder(toBuilder = true)
public class NewEventDto {
    @NotBlank
    private final String annotation;      //Краткое описание
    @NotNull
    private final Long category;          //id категории к которой относится событие
    @NotBlank
    private final String description;     //Полное описание события
    @NotBlank
    private final String eventDate;       //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private final Location location;      //Широта и долгота места проведения события
    private final Boolean paid;           //Нужно ли оплачивать участие
    private final Long participantLimit;   //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    private final Boolean requestModeration;  //Нужна ли пре-модерация заявок на участие
    @NotBlank
    private final String title;           //Заголовок
}
