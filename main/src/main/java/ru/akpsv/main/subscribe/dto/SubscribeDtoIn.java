package ru.akpsv.main.subscribe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeDtoIn {
    private Long subscriberId;
    private Long publisherId;
}
