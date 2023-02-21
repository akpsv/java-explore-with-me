package ru.akpsv.main.event;

import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.dto.NewEventDto;
import ru.akpsv.main.event.dto.UpdateEventAdminRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Optional<EventFullDto> create(Long userId, NewEventDto newEvent);

    Optional<List<EventShortDto>> getEventsByUser(Long userId, Integer from, Integer size);

    List<EventFullDto> getEventsByAdminParams(EventParamsForAdmin params);

    EventFullDto updateEvent(UpdateEventAdminRequest updateEvent, Long eventId);

    List<EventShortDto> getEventsByPublicParams(EventParamsForPublic params, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

}
