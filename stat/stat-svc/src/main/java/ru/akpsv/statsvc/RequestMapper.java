package ru.akpsv.statsvc;

import ru.akpsv.dto.RequestDtoIn;
import ru.akpsv.statsvc.model.Request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RequestMapper {
    public static Request toRequest(RequestDtoIn requestDtoIn) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Request.builder()
                .app(requestDtoIn.getApp())
                .uri(requestDtoIn.getUri())
                .ip(requestDtoIn.getIp())
                .timestamp(LocalDateTime.parse(requestDtoIn.getTimestamp(), dateTimeFormatter))
                .build();
    }
}
