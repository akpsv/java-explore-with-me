package ru.akpsv.main.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.NewEventDto;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventFullDto create(@PathVariable Long userId, @RequestBody NewEventDto newEvent){
        return eventService.create(userId,newEvent).get();
    }
}
