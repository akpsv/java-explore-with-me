package ru.akpsv.main.event;

import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventParamsForAdmin {
    private Optional<List<Long>> users = Optional.empty();
    private Optional<List<String>> states = Optional.empty();
    private Optional<List<Long>> categories = Optional.empty();
    private Optional<String> rangeStart = Optional.empty();
    private Optional<String> rangeEnd = Optional.empty();
    private Integer from;
    private Integer size;
}
