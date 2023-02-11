package ru.akpsv.main.event.dto;

import lombok.Builder;
import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.event.model.Location;

@Builder(toBuilder = true)
public class NewEventDto {
    private String annotation;      //Краткое описание
    private CategoryDto category;
    private String description;     //Полное описание события
    private String eventDate;       //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    private Location location;      //Широта и долгота места проведения события
    private Boolean paid;           //Нужно ли оплачивать участие
    private Integer participantLimit;   //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    private Boolean requestModeration;  //Нужна ли пре-модерация заявок на участие
    private String title;           //Заголовок
}
