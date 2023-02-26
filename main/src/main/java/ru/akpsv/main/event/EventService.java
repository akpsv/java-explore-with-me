package ru.akpsv.main.event;

import ru.akpsv.main.event.dto.*;
import ru.akpsv.main.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Optional<EventFullDto> create(Long userId, NewEventDto newEvent);

    Optional<List<EventShortDto>> getEventsByUser(Long userId, Integer from, Integer size);

    List<EventFullDto> getEventsByAdminParams(EventParamsForAdmin params);

    EventFullDto updateEventByAdmin(UpdateEventAdminRequest updatingRequest, Long eventId);

    List<EventShortDto> getEventsByPublicParams(EventParamsForPublic params, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

    EventFullDto getFullEventInfoByUser(Long userId, Long eventId);

    EventFullDto updateEventByCurrentUser(UpdateEventUserRequest updatingRequest, Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsOfParticipantsEventOfCurrentUser(Long eventId);

    EventRequestStatusUpdateResult changeRequestsStatusCurrentUser(EventRequestStatusUpdateRequest updateRequestStatus, Long userId, Long eventId);
}
