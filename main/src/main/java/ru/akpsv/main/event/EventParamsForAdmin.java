package ru.akpsv.main.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class EventParamsForAdmin {
    private Optional<List<Long>> users;
    private Optional<List<String>> states;
    private Optional<List<Long>> categories;
    private Optional<String> rangeStart;
    private Optional<String> rangeEnd;
    private Integer from;
    private Integer size;
}
