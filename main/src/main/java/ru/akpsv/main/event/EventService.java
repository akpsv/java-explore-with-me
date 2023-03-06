package ru.akpsv.main.event;

import ru.akpsv.main.event.dto.*;
import ru.akpsv.main.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto create(Long userId, NewEventDto newEvent);

    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    List<EventFullDto> getEventsByAdminParams(EventParams params);

    EventFullDto updateEventByAdmin(UpdateEventAdminRequest updatingRequest, Long eventId);

    List<EventShortDto> getEventsByPublicParams(EventParams params, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

    EventFullDto getFullEventInfoByUser(Long userId, Long eventId);

    EventFullDto updateEventByCurrentUser(UpdateEventUserRequest updatingRequest, Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsOfParticipantsEventOfCurrentUser(Long eventId);

    EventRequestStatusUpdateResult changeRequestsStatusCurrentUser(EventRequestStatusUpdateRequest updateRequestStatus, Long userId, Long eventId);
}
