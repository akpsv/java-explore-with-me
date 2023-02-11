package ru.akpsv.main.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.akpsv.main.category.model.Category;
import ru.akpsv.main.user.dto.UserShortDto;

import javax.persistence.*;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;                //Идентификатор
    @Column(name = "annotation")
    private String annotation;      //Краткое описание
    @Column(name = "category")
    private Category category;
    private Long confirmedRequests; //Количество одобренных заявок на участие в данном событии
    private String createdOn;       //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    private String description;     //Полное описание события
    private String eventDate;       //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    private UserShortDto initiator; //Пользователь (краткая информация)
    private Location location;      //Широта и долгота места проведения события
    private Boolean paid;           //Нужно ли оплачивать участие
    private Integer participantLimit;   //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    private String publishedOn;     //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    private Boolean requestModeration;  //Нужна ли пре-модерация заявок на участие
    private String state;           //Список состояний жизненного цикла события Enum: [ PENDING, PUBLISHED, CANCELED ]
    private String title;           //Заголовок
    private Long views;             //Количество просмотрев события

}
