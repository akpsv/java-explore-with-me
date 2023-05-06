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
    @Builder.Default
    private Optional<List<Long>> users = Optional.empty();
    @Builder.Default
    private Optional<List<String>> states = Optional.empty();
    @Builder.Default
    private Optional<String> text = Optional.empty();
    @Builder.Default
    private Optional<Boolean> paid = Optional.empty();
    @Builder.Default
    private Optional<List<Long>> categories = Optional.empty();
    @Builder.Default
    private Optional<String> rangeStart = Optional.empty();
    @Builder.Default
    private Optional<String> rangeEnd = Optional.empty();
    @Builder.Default
    private Optional<Boolean> onlyAvailable = Optional.empty();
    @Builder.Default
    private Optional<String> sort = Optional.empty();

    private Integer from;
    private Integer size;
}
