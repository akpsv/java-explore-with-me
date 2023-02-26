package ru.akpsv.main.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "requests",
        uniqueConstraints = @UniqueConstraint(
                name = "UNQ_REQUEST",
                columnNames = {"event_id", "requester_id"}
        )
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Request {
    //Заявка на участие в событии
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;            //Идентификатор заявки (TODO: заполняется бд)

    @CreationTimestamp
    @Column(name = "created")
    private LocalDateTime created;     //example: 2022-09-06T21:10:05.432 Дата и время создания заявки (TODO: заполняется бд)

    @Column(name = "event_id", nullable = false)
    private Long eventId;         //Идентификатор события

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;     //Идентификатор пользователя, отправившего заявку

    @Column(name = "status")
    private RequestStatus status;      //Статус заявки. example: PENDING (может быть перечисление)
}
