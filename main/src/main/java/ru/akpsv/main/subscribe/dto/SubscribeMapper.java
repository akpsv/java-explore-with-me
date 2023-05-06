package ru.akpsv.main.subscribe.dto;

import ru.akpsv.main.subscribe.model.Subscribe;

public class SubscribeMapper {
    public static SubscribeDtoOut subscribeDtoOut(Subscribe subscribe){
        return SubscribeDtoOut.builder()
                .subscriberId(subscribe.getId().getPublisherId())
                .publisherId(subscribe.getId().getPublisherId())
                .build();
    }
}
