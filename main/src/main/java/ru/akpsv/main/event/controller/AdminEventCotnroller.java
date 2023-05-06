package ru.akpsv.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.UpdateEventAdminRequest;
import ru.akpsv.main.event.service.AdminEventService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminEventCotnroller {
    private final AdminEventService adminEventService;

    /**
     * Admin: События. АПИ для работы с событиями
     */
    @GetMapping("/admin/events")
    public List<EventFullDto> getAdminEventsByParams(@RequestParam Optional<List<Long>> users,
                                                     @RequestParam Optional<List<String>> states,
                                                     @RequestParam Optional<List<Long>> categories,
                                                     @RequestParam Optional<String> rangeStart,
                                                     @RequestParam Optional<String> rangeEnd,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {

        return adminEventService.getEventsByAdminParams(
                EventParams.builder()
                        .users(users)
                        .states(states)
                        .categories(categories)
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .from(from)
                        .size(size)
                        .build()
        );
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEvent(@RequestBody UpdateEventAdminRequest updatingRequest, @PathVariable Long eventId) {
        return adminEventService.updateEventByAdmin(updatingRequest, eventId);
    }
}
