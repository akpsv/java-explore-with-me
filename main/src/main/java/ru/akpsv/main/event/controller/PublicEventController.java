package ru.akpsv.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.service.PublicEventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PublicEventController {
    private final PublicEventService publicEventService;

    /**
     * Public: События. Публичный АПИ для работы с событиями
     */
    @GetMapping("/events")
    public List<EventShortDto> getPublicEventsByParams(@RequestParam Optional<String> text,
                                                       @RequestParam Optional<List<Long>> categories,
                                                       @RequestParam Optional<Boolean> paid,
                                                       @RequestParam Optional<String> rangeStart,
                                                       @RequestParam Optional<String> rangeEnd,
                                                       @RequestParam Optional<Boolean> onlyAvailable,
                                                       @RequestParam Optional<String> sort,
                                                       @RequestParam(defaultValue = "0") Integer from,
                                                       @RequestParam(defaultValue = "10") Integer size,
                                                       HttpServletRequest request) {

        EventParams params = EventParams.builder().text(text).categories(categories).paid(paid).rangeStart(rangeStart)
                .rangeEnd(rangeEnd).onlyAvailable(onlyAvailable).sort(sort).from(from).size(size).build();

        return publicEventService.getEventsByPublicParams(params, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventById(@PathVariable("id") Long eventId, HttpServletRequest request) {
        return publicEventService.registerViewAndGetEventById(eventId, request);
    }
}
