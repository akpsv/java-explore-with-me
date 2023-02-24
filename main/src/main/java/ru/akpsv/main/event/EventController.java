package ru.akpsv.main.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.event.dto.*;
import ru.akpsv.main.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    /**
     * Private: События. Закрытый АПИ для работы с событиями
     */
    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId, @RequestBody NewEventDto newEvent) {
        return eventService.create(userId, newEvent).get();
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEventsByUser(userId, from, size).get();
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getFullEventInfoByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getFullEventInfoByUser(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEventByUser(@RequestBody UpdateEventUserRequest updatingRequest,
                                          @PathVariable Long userId,
                                          @PathVariable Long eventId) {
        return eventService.updateEventByUser(updatingRequest, userId, eventId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsOfParticipantsEventOfCurrentUser(@PathVariable Long userId, @PathVariable Long eventId){
        return eventService.getRequestsOfParticipantsEventOfCurrentUser(eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeReqeustsStatus(@RequestBody EventRequestStatusUpdateRequest updateRequestStatus,
                                                               @PathVariable Long userId,
                                                               @PathVariable Long eventId){
        return eventService.changeRequestsStatus(updateRequestStatus, userId, eventId);


    }

    /**
     * Admin: События. АПИ для работы с событиями
     */
    @GetMapping("/admin/events")
    public List<EventFullDto> getAdminEventsByParams(@RequestParam Long[] users,
                                                     @RequestParam String[] states,
                                                     @RequestParam Long[] categories,
                                                     @RequestParam String rangeStart,
                                                     @RequestParam String rangeEnd,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        EventParamsForAdmin params = new EventParamsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);

        List<EventFullDto> eventsByParams = eventService.getEventsByAdminParams(params);
        return eventsByParams;
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEvent(@RequestBody UpdateEventAdminRequest updatingRequest, @PathVariable Long eventId) {
        return eventService.updateEvent(updatingRequest, eventId);
    }

    //Public: События. Публичный АПИ для работы с событиями
    @GetMapping("/events")
    public List<EventShortDto> getPublicEventsByParams(@RequestParam String text,
                                                       @RequestParam Long[] categories,
                                                       @RequestParam Boolean paid,
                                                       @RequestParam String rangeStart,
                                                       @RequestParam String rangeEnd,
                                                       @RequestParam Boolean onlyAvailable,
                                                       @RequestParam String sort,
                                                       @RequestParam(defaultValue = "0") Integer from,
                                                       @RequestParam(defaultValue = "10") Integer size,
                                                       HttpServletRequest request
    ) {
        EventParamsForPublic params = new EventParamsForPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.getEventsByPublicParams(params, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventById(@PathVariable("id") Long eventId, HttpServletRequest request) {
        return eventService.getEventById(eventId, request);
    }
}
