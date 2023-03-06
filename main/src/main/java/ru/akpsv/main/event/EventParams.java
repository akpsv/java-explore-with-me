package ru.akpsv.main.event;

import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EventParams {
    private Optional<List<Long>> users = Optional.empty();
    private Optional<List<String>> states = Optional.empty();
    private Optional<String> text = Optional.empty();
    private Optional<Boolean> paid = Optional.empty();
    private Optional<List<Long>> categories = Optional.empty();
    private Optional<String> rangeStart = Optional.empty();
    private Optional<String> rangeEnd = Optional.empty();
    private Optional<Boolean> onlyAvailable = Optional.empty();
    private Optional<String> sort = Optional.empty();
    private Integer from;
    private Integer size;
}
