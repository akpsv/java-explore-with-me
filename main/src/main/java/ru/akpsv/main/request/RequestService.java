package ru.akpsv.main.request;

import ru.akpsv.main.request.dto.ParticipationRequestDto;

public interface RequestService {
    ParticipationRequestDto create(Long userId, Long eventId);
}
