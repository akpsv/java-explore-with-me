package ru.akpsv.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RequestDtoIn {
    //Идентификатор сервиса для которого записывается информация
    private String app;
    //URI для которого был осуществлен запрос
    private String uri;
    //IP-адрес пользователя, осуществившего запрос
    private String ip;
    //Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
    private String timestamp;
}
