package ru.akpsv.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StatDtoOut {
    //Название сервиса
    private String app;
    //URI сервиса
    private String uri;
    //Количество просмотров
    private long hits;
}
