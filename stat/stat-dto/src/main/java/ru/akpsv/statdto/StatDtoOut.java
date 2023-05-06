package ru.akpsv.statdto;

import lombok.*;

@Getter
@Setter
@ToString
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
