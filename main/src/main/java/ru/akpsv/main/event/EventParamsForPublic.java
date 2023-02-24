package ru.akpsv.main.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EventParamsForPublic {
    private Optional<String> text;
    private Optional<List<Long>> categories;
    private Optional<Boolean> paid;
    private Optional<String> rangeStart;
    private Optional<String> rangeEnd;
    private Optional<Boolean> onlyAvailable;
    private Optional<String> sort;
    private Integer from;
    private Integer size;
}
