package ru.akpsv.statsvc.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Содержит информацию об отдельном запросе
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
    @Column(name = "requestor_app", nullable = false)
    private String app;
    //URI для которого был осуществлен запрос
    @Column(name = "request_uri", length = 1024, nullable = false)
    private String uri;
    //IP-адрес пользователя, осуществившего запрос
    @Column(name = "requestor_ip", nullable = false)
    private String ip;
    //Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "request_time", nullable = false)
    private LocalDateTime timestamp;
}
