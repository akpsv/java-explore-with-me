package ru.akpsv.main.request;

import javax.persistence.*;

@Entity
@Table(name = "requests")
public class Request {
    //Заявка на участие в событии
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;            //Идентификатор заявки
    @Column(name = "created")
    private String created;     //example: 2022-09-06T21:10:05.432 Дата и время создания заявки
    @Column(name = "event_id")
    private Long eventId;         //Идентификатор события
    @Column(name = "requester_id")
    private Long requesterId;     //Идентификатор пользователя, отправившего заявку
    @Column(name="status")
    private String status;      //Статус заявки. example: PENDING (может быть перечисление)
}
