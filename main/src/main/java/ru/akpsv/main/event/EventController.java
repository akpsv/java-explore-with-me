package ru.akpsv.main.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.dto.NewEventDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId, @RequestBody NewEventDto newEvent){
        return eventService.create(userId,newEvent).get();
    }

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size){
        return eventService.getEventsByUser(userId, from, size).get();
    }
}
