package ru.akpsv.main.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.main.error.ViolationOfRestrictionsException;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.event.repository.EventRepository;
import ru.akpsv.main.request.dto.ParticipationRequestDto;
import ru.akpsv.main.request.dto.RequestMapper;
import ru.akpsv.main.request.model.Request;
import ru.akpsv.main.request.model.RequestStatus;
import ru.akpsv.main.request.repository.RequestRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
        Event checkedEvent = eventRepository.findById(eventId)
                //инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
                .filter(event -> !event.getInitiatorId().equals(userId))
                //нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
                .filter(event -> event.getState().equals(EventState.PUBLISHED))
                .orElseThrow(() -> new ViolationOfRestrictionsException("Integrity constraint has been violated"));

        Request request = null;
        if (!checkedEvent.getRequestModeration()) {
            request = request.toBuilder().status(RequestStatus.CONFIRMED).build();
            checkedEvent = checkedEvent.toBuilder().confirmedRequests(checkedEvent.getConfirmedRequests() + 1).build();
            eventRepository.save(checkedEvent);
        } else if (!checkedEvent.getAvailableToParicipants()) {
            //если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
            throw new ViolationOfRestrictionsException("Integrity constraint has been violated");
        } else {
            request = Request.builder()
                    .requesterId(userId)
                    .eventId(eventId)
                    .status(RequestStatus.PENDING)
                    .build();
        }
        //TODO: нельзя добавить повторный запрос (Ожидается код ошибки 409)
        return Optional.ofNullable(request)
                .map(requestRepository::save)
                .map(RequestMapper::toParticipationRequestDto)
                .orElseThrow(() -> new NoSuchElementException("Request not saved."));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfCurrentUser(Long userId) {
        return requestRepository.getRequestsByRequesterId(userId).stream()
                .filter(request -> !eventRepository.findById(request.getEventId()).get().getInitiatorId().equals(userId))
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelOwnRequest(Long userId, Long requestId) {
        return requestRepository.findById(requestId)
                .filter(request -> request.getRequesterId().equals(userId))
                .map(request -> {
                    request = request.toBuilder().status(RequestStatus.CANCELED).build();
                    return RequestMapper.toParticipationRequestDto(request);
                })
                .orElseThrow(() -> new NoSuchElementException("Request id=" + requestId + " is not exist"));
    }
}
