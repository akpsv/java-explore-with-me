package ru.akpsv;

import ru.akpsv.dto.RequestDtoIn;
import ru.akpsv.statsvc.model.Request;

import java.time.LocalDateTime;

public class TestHelper {
    public static Request createRequest(long id){
        return Request.builder()
                .id(id)
                .app("TestApp")
                .uri("http://test.server.ru")
                .ip("192.168.1.1")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static RequestDtoIn createRequestDtoIn(){
        return RequestDtoIn.builder()
                .app("TestApp")
                .uri("http://test.server.ru")
                .ip("192.168.1.1")
                .timestamp("2023-01-01 12:30:00")
                .build();
    }
}
