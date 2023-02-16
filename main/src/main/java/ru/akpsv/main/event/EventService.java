package ru.akpsv.main.event;

import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.dto.NewEventDto;

import java.util.List;
import java.util.Optional;

public interface EventService {
    Optional<EventFullDto> create(Long userId, NewEventDto newEvent);

    Optional<List<EventShortDto>> getEventsByUser(Long userId, Integer from, Integer size);
}
