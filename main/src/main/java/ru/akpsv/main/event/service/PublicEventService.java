package ru.akpsv.main.event.service;

import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PublicEventService {
    List<EventShortDto> getEventsByPublicParams(EventParams params, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

}
