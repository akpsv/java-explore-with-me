package ru.akpsv.main.subscribe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
public class SubscribeDtoOut {
    protected Long subscribeId;
    protected UserDtoOut subscriber;
    protected Long publisherId;
    protected UserDtoOut publisher;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserDtoOut {
        private String name;
        private String email;
    }
}
