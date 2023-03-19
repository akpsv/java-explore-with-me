package ru.akpsv.main.event.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @NotNull
    @Column(name = "initiator_id")
    private Long initiatorId; //Пользователь (краткая информация) TODO:(Заполняется eventMapper.toEvent())

    @NotNull
    @Column(name = "category_id")
    private Long categoryId;


    @CreationTimestamp
    @Column(name = "created_on")
    private LocalDateTime createdOn;       //Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss") TODO:(Заполняется базой при сохранении)

    @NotNull
    @FutureOrPresent
    @Column(name = "event_date")
    private LocalDateTime eventDate;       //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @NotNull
    private Location location;

    @Column(name = "paid")
    private Boolean paid;           //Нужно ли оплачивать участие

    @Column(name = "participant_limit")
    private Long participantLimit;//Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    @Column(name = "confirmed_requests")
    private Long confirmedRequests; //Количество одобренных заявок на участие в данном событии TODO:(Заполняется в процессе работы)

    @Column(name = "available_to_participants")
    @Builder.Default
    private Boolean availableToParicipants = true; //доступность для участия

    @Column(name = "published_on")
    private LocalDateTime publishedOn;     //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss") TODO:(Заполняестя во время работы)

    @Column(name = "request_moderation")
    private Boolean requestModeration;  //Нужна ли пре-модерация заявок на участие

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EventState state = EventState.PENDING;           //Список состояний жизненного цикла события Enum: [ PENDING, PUBLISHED, CANCELED ] TODO:(Заполняется во время создания)

    @NotBlank
    @Column(name = "title")
    private String title;           //Заголовок

    @Column(name = "views")
    @Builder.Default
    private Long views = 0L;             //Количество просмотрев события TODO:(Заполняется во время работы)
}
