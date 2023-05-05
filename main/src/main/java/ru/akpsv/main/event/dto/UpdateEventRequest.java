package ru.akpsv.main.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.akpsv.main.event.model.Location;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {
    @Max(2000)
    @Min(20)
    protected String annotation; //Новая аннотация
    protected Long category; //Новая категория
    @Max(7000)
    @Min(20)
    protected String description; //Новое описание (maxLength: 7000 minLength: 20)
    protected String eventDate; //Новые дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
    protected Location location;
    protected Boolean paid; //Новое значение флага о платности мероприятия
    protected Long participantLimit; //Новый лимит пользователей
    protected Boolean requestModeration; //Нужна ли пре-модерация заявок на участие
    protected String stateAction; //Новое состояние события 	(Enum:[ PUBLISH_EVENT, REJECT_EVENT ])
    @Max(120)
    @Min(3)
    protected String title; //Новый заголовок
}
