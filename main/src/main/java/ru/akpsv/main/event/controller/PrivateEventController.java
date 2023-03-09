package ru.akpsv.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.event.EventRequestStatusUpdateRequest;
import ru.akpsv.main.event.EventRequestStatusUpdateResult;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.dto.NewEventDto;
import ru.akpsv.main.event.dto.UpdateEventUserRequest;
import ru.akpsv.main.event.service.PrivateEventService;
import ru.akpsv.main.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PrivateEventController {
    private final PrivateEventService privateEventService;

    /**
     * Private: События. Закрытый АПИ для работы с событиями
     */
    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEvent) {
        return privateEventService.create(userId, newEvent);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return privateEventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getFullEventInfoByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return privateEventService.getFullEventInfoByUser(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEventByUser(@Valid @RequestBody UpdateEventUserRequest updatingRequest,
                                          @PathVariable Long userId,
                                          @PathVariable Long eventId) {
        return privateEventService.updateEventByCurrentUser(updatingRequest, userId, eventId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsOfParticipantsEventOfCurrentUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return privateEventService.getRequestsOfParticipantsEventOfCurrentUser(eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeReqeustsStatusCurrentUser(@Valid @RequestBody EventRequestStatusUpdateRequest updateRequestStatus,
                                                                          @PathVariable Long userId,
                                                                          @PathVariable Long eventId) {
        return privateEventService.changeRequestsStatusCurrentUser(updateRequestStatus, userId, eventId);


    }

}
