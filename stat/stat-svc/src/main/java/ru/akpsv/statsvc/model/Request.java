package ru.akpsv.statsvc.model;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

/**
Содержит информацию об отдельном запросе
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "requests")
public class Request {
    //Идентификатор записи
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    //Идентификатор сервиса для которого записывается информация
    @Column(name = "requestor_app")
    private String app;
    //URI для которого был осуществлен запрос
    @Column(name = "request_uri")
    private String uri;
    //IP-адрес пользователя, осуществившего запрос
    @Column(name = "requestor_ip")
    private String ip;
    //Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "request_time")
    private LocalDateTime timestamp;
}