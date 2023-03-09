package ru.akpsv.main.event.service;

import ru.akpsv.main.event.EventRequestStatusUpdateRequest;
import ru.akpsv.main.event.EventRequestStatusUpdateResult;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.dto.NewEventDto;
import ru.akpsv.main.event.dto.UpdateEventUserRequest;
import ru.akpsv.main.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {
    EventFullDto create(Long userId, NewEventDto newEvent);

    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);



    EventFullDto getFullEventInfoByUser(Long userId, Long eventId);

    EventFullDto updateEventByCurrentUser(UpdateEventUserRequest updatingRequest, Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsOfParticipantsEventOfCurrentUser(Long eventId);

    EventRequestStatusUpdateResult changeRequestsStatusCurrentUser(EventRequestStatusUpdateRequest updateRequestStatus, Long userId, Long eventId);
}
