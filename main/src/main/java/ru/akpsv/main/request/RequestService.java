package ru.akpsv.main.request;

import ru.akpsv.main.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto create(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsOfCurrentUser(Long userId);

    ParticipationRequestDto cancelOwnRequest(Long userId, Long requestId);
}
