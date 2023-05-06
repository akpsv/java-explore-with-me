package ru.akpsv.main.subscribe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
public class SubscribeDtoOut {
    protected Long subscriberId;
    protected Long publisherId;
}
