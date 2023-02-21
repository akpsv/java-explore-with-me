package ru.akpsv.main.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.main.request.dto.ParticipationRequestDto;
import ru.akpsv.main.request.dto.RequestMapper;
import ru.akpsv.main.request.model.Request;
import ru.akpsv.main.request.model.RequestStatus;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService{
    private final RequestRepository requestRepository;
    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        Request request = Request.builder()
                .requesterId(userId)
                .eventId(eventId)
                .status(RequestStatus.PENDING)
                .build();

        Request savedRequest = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(savedRequest);

    }
}
