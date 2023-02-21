package ru.akpsv.main.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.dto.NewEventDto;
import ru.akpsv.main.event.dto.UpdateEventAdminRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    /**
     *     Private: События. Закрытый АПИ для работы с событиями
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
    public EventFullDto getFullEventInfoByUser(@PathVariable Long userId, @PathVariable Long eventId){
        throw new UnsupportedOperationException("Эндпоинт пока не реализован");
    }


    /**
     *      Admin: События. АПИ для работы с событиями
     */
    @GetMapping("/admin/events")
    public List<EventFullDto> getAdminEventsByParams(@RequestParam Long[] users,
                                                @RequestParam String[] states,
                                                @RequestParam Long[] categories,
                                                @RequestParam String rangeStart,
                                                @RequestParam String rangeEnd,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size){
        EventParamsForAdmin params = new EventParamsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);

        List<EventFullDto> eventsByParams = eventService.getEventsByAdminParams(params);
        return eventsByParams;
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEvent(@RequestBody UpdateEventAdminRequest updateEvent, @PathVariable Long eventId){
        return eventService.updateEvent(updateEvent, eventId);
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
                                                       ){
        EventParamsForPublic params = new EventParamsForPublic(text,categories,paid,rangeStart,rangeEnd,onlyAvailable,sort,from,size);
        return eventService.getEventsByPublicParams(params, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventById(@PathVariable("id") Long eventId, HttpServletRequest request){
        return eventService.getEventById(eventId, request);
    }
}
