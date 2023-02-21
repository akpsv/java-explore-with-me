package ru.akpsv.main.event;

import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EventParamsForPublic {
    private String text;
    private Long[] categories;
    private Boolean paid;
    private String rangeStart;
    private String rangeEnd;
    private Boolean  onlyAvailable;
    private String sort;
    private Integer from;
    private Integer size;
}
