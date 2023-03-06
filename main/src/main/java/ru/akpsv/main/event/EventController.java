package ru.akpsv.main.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.event.dto.*;
import ru.akpsv.main.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
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
    public EventFullDto create(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEvent) {
        return eventService.create(userId, newEvent);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getFullEventInfoByUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getFullEventInfoByUser(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEventByUser(@Valid @RequestBody UpdateEventUserRequest updatingRequest,
                                          @PathVariable Long userId,
                                          @PathVariable Long eventId) {
        return eventService.updateEventByCurrentUser(updatingRequest, userId, eventId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsOfParticipantsEventOfCurrentUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getRequestsOfParticipantsEventOfCurrentUser(eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeReqeustsStatusCurrentUser(@Valid @RequestBody EventRequestStatusUpdateRequest updateRequestStatus,
                                                                          @PathVariable Long userId,
                                                                          @PathVariable Long eventId) {
        return eventService.changeRequestsStatusCurrentUser(updateRequestStatus, userId, eventId);


    }

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

        EventParamsForAdmin params = new EventParamsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getEventsByAdminParams(params);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEvent(@RequestBody UpdateEventAdminRequest updatingRequest, @PathVariable Long eventId) {
        return eventService.updateEventByAdmin(updatingRequest, eventId);
    }

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
                                                       HttpServletRequest request
    ) {
        EventParams params = new EventParams();
        params.setText(text);
        params.setCategories(categories);
        params.setPaid(paid);
        params.setRangeStart(rangeStart);
        params.setRangeEnd(rangeEnd);
        params.setOnlyAvailable(onlyAvailable);
        params.setSort(sort);
        params.setFrom(from);
        params.setSize(size);

        EventParams eventParams = EventParams.builder().from(0).paid(Optional.empty()).build();

        log.debug("Вызов метода eventService.getEventsByPublicParams c параметрами: params= {}, reqeust={}", params, request);
        List<EventShortDto> eventsByPublicParams = eventService.getEventsByPublicParams(params, request);
        return eventsByPublicParams;
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventById(@PathVariable("id") Long eventId, HttpServletRequest request) {
        return eventService.getEventById(eventId, request);
    }
}
