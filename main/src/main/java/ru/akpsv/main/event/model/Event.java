package ru.akpsv.main.event.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;                //Идентификатор. TODO:(Заполняется базой)
    @NotBlank
    @Column(name = "annotation")
    private String annotation;      //Краткое описание
    @Column(name = "description")
    private String description;     //Полное описание события
    @NotBlank
    @Column(name = "initiator_id")
    private Long initiatorId; //Пользователь (краткая информация) TODO:(Заполняется eventMapper.toEvent())

    @NotBlank
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "confirmed_requests")
    private Long confirmedRequests; //Количество одобренных заявок на участие в данном событии TODO:(Заполняется в процессе работы)

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "created_on")
    private String createdOn;       //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss") TODO:(Заполняется базой при сохранении)

    @NotBlank
    @Column(name = "event_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime eventDate;       //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @NotBlank
    @Column(name = "location_longitude")
    private Double locationLongitude;
    @NotBlank
    @Column(name = "location_latitude")
    private Double locationLatitude;

    @NotBlank
    @Column(name = "paid")
    private Boolean paid;           //Нужно ли оплачивать участие

    @Column(name = "participant_limit")
    private Integer participantLimit;//Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "published_on")
    private LocalDateTime publishedOn;     //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss") TODO:(Заполняестя во время работы)

    @Column(name = "request_moderation")
    private Boolean requestModeration;  //Нужна ли пре-модерация заявок на участие

    @Enumerated(EnumType.STRING)
    private EventState state = EventState.PENDING;           //Список состояний жизненного цикла события Enum: [ PENDING, PUBLISHED, CANCELED ] TODO:(Заполняется во время создания)

    @NotBlank
    @Column(name = "title")
    private String title;           //Заголовок

    @Column(name = "views")
    private Long views;             //Количество просмотрев события TODO:(Заполняется во время работы)
}
