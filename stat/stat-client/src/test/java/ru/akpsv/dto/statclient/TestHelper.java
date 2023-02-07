package ru.akpsv.dto.statclient;

import ru.akpsv.dto.RequestDtoIn;
import ru.akpsv.dto.StatDtoOut;

public class TestHelper {
//    public static Request createRequest(long id, String uri, String ip){
//        return Request.builder()
//                .id(id)
//                .app("TestApp")
//                .uri(uri)
//                .ip(ip)
//                .timestamp(LocalDateTime.now())
//                .build();
//    }
    public static RequestDtoIn createRequestDtoIn(){
        return RequestDtoIn.builder()
                .app("TestApp")
                .uri("http://test.server.ru/endpoint")
                .ip("192.168.1.1")
                .timestamp("2023-01-01 12:30:00")
                .build();
    }

    public static StatDtoOut createStatDtoOut(String uri,long hits){
        return StatDtoOut.builder()
                .app("TestApp")
                .uri(uri)
                .hits(hits)
                .build();
    }
}
