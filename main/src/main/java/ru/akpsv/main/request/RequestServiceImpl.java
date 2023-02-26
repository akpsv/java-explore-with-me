package ru.akpsv.main.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.main.error.ViolationOfRestrictionsException;
import ru.akpsv.main.event.EventRepository;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.request.dto.ParticipationRequestDto;
import ru.akpsv.main.request.dto.RequestMapper;
import ru.akpsv.main.request.model.Request;
import ru.akpsv.main.request.model.RequestStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    @PersistenceContext
    private EntityManager em;

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        Event checkedEvent = eventRepository.findById(eventId).filter(event -> event.getInitiatorId() != userId)
                .filter(event -> event.getState().equals(EventState.PUBLISHED))
                .filter(event -> event.getParticipantLimit() != event.getConfirmedRequests())
                .orElseThrow(() -> new ViolationOfRestrictionsException("Initiator of event cannot to add a request"));

        Request request = Request.builder()
                .requesterId(userId)
                .eventId(eventId)
                .status(RequestStatus.PENDING)
                .build();

        if (checkedEvent.getRequestModeration().equals(false)) {
            request = request.toBuilder().status(RequestStatus.CONFIRMED).build();
            checkedEvent = checkedEvent.toBuilder().confirmedRequests(checkedEvent.getConfirmedRequests() + 1).build();
            eventRepository.save(checkedEvent);
        }

        Request savedRequest = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(savedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfCurrentUser(Long userId) {
        return requestRepository.getRequestsByRequesterId(userId).stream()
                .filter(request -> eventRepository.findById(request.getEventId()).get().getInitiatorId() != userId)
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelOwnRequest(Long userId, Long requestId) {
        return requestRepository.findById(requestId)
                .filter(request -> request.getRequesterId() == userId)
                .map(request -> {
                    request = request.toBuilder().status(RequestStatus.CANCELED).build();
                    return RequestMapper.toParticipationRequestDto(request);
                })
                .orElseThrow(() -> new NoSuchElementException("Request id=" + requestId + " is not exist"));
    }
}
