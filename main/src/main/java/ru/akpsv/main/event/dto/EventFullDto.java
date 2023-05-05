package ru.akpsv.main.event.dto;

import lombok.*;
import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.event.model.Location;
import ru.akpsv.main.user.dto.UserShortDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EventFullDto {
    private String annotation; //Краткое описание
    private CategoryDto category;
    private Long confirmedRequests; //Количество одобренных заявок на участие в данном событии
    private String createdOn; //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    private String description; //Полное описание события
    private String eventDate; //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    private Long id; //Идентификатор
    private UserShortDto initiator; //Пользователь (краткая информация)
    private Location location; //Широта и долгота места проведения события
    private Boolean paid; //Нужно ли оплачивать участие
    private Long participantLimit; //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    private String publishedOn; //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    private Boolean requestModeration;  //Нужна ли пре-модерация заявок на участие
    private String state; //Список состояний жизненного цикла события Enum: [ PENDING, PUBLISHED, CANCELED ]
    private String title; //Заголовок
    private Long views; //Количество просмотрев события
}
