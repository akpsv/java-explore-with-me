package ru.akpsv.main.event.dto;

import lombok.*;
import ru.akpsv.main.event.model.Location;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateEventAdminRequest {
    //Данные для изменения информации о событии.
    //Если поле в запросе не указано (равно null) - значит изменение этих данных не треубется.
    @Max(2000)
    @Min(20)
    private String annotation;  //Новая аннотация
    private Long category;  //Новая категория
    @Max(7000)
    @Min(20)
    private String description; //Новое описание (maxLength: 7000 minLength: 20)
    private String eventDate;   //Новые дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
    private Location location;
    private Boolean paid;       //Новое значение флага о платности мероприятия
    private Integer participantLimit;   //Новый лимит пользователей
    private Boolean requestModeration;  //Нужна ли пре-модерация заявок на участие
    private String stateAction;   //  Новое состояние события 	(Enum:[ PUBLISH_EVENT, REJECT_EVENT ])
    @Max(120)
    @Min(3)
    private String title;   //Новый заголовок
}
