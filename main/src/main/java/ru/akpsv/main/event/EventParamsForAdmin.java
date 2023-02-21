package ru.akpsv.main.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventParamsForAdmin {
    private Long[] users;
    private String[] states;
    private Long[] categories;
    private String rangeStart;
    private String rangeEnd;
    private Integer from;
    private Integer size;
}
