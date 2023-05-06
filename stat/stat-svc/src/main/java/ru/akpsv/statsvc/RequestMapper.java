package ru.akpsv.statsvc;

import ru.akpsv.statdto.EndpointHit;
import ru.akpsv.statsvc.model.Request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RequestMapper {
    public static Request toRequest(EndpointHit endpointHit) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Request.builder()
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(LocalDateTime.parse(endpointHit.getTimestamp(), dateTimeFormatter))
                .build();
    }
}
