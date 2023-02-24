package ru.akpsv.main.event;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import ru.akpsv.dto.RequestDtoIn;
import ru.akpsv.dto.statclient.RestClientService;
import ru.akpsv.main.error.LimitReachedException;
import ru.akpsv.main.event.dto.*;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.request.RequestRepository;
import ru.akpsv.main.request.dto.ParticipationRequestDto;
import ru.akpsv.main.request.dto.RequestMapper;
import ru.akpsv.main.request.model.Request;
import ru.akpsv.main.request.model.RequestStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    @PersistenceContext
    EntityManager em;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Optional<EventFullDto> create(Long userId, NewEventDto newEvent) {
        Event event = eventMapper.toEvent(newEvent, userId);
        event = event.toBuilder().state(EventState.PENDING).build();
        Event savedEvent = eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(savedEvent);
        return Optional.of(eventFullDto);
    }

    @Override
    public Optional<List<EventShortDto>> getEventsByUser(Long userId, Integer from, Integer size) {
        List<EventShortDto> groupOfEventShortDtos = eventRepository.getEventsByUser(em, userId, from, size).stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
        return Optional.of(groupOfEventShortDtos);
    }

    @Override
    public List<EventFullDto> getEventsByAdminParams(EventParamsForAdmin params) {
        return eventRepository.getEventsByAdminParams(em, params).stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(UpdateEventAdminRequest updatingRequest, Long eventId) {
        String noSuchElementExceptionMessage = "Event witn id=" + eventId + " was not found";
        Event updatingEvent = eventRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException(noSuchElementExceptionMessage));

        updatingEvent = checkRequestAndFillUpdatingFilds(updatingRequest, updatingEvent);
        Event savedUpdatedEvent = eventRepository.save(updatingEvent);
        return eventMapper.toEventFullDto(savedUpdatedEvent);
    }

    protected Event checkRequestAndFillUpdatingFilds(UpdateEventRequest request, Event updatingEvent) {
        if (request.getAnnotation() != null)
            updatingEvent = updatingEvent.toBuilder().annotation(request.getAnnotation()).build();
        if (request.getCategory() != null)
            updatingEvent = updatingEvent.toBuilder().categoryId(request.getCategory()).build();
        if (request.getDescription() != null)
            updatingEvent = updatingEvent.toBuilder().description(request.getDescription()).build();
        if (request.getEventDate() != null)
            updatingEvent = updatingEvent.toBuilder().eventDate(LocalDateTime.parse(request.getEventDate(), formatter)).build();
        if (request.getLocation() != null)
            updatingEvent = updatingEvent.toBuilder().location(request.getLocation()).build();
        if (request.getPaid() != null)
            updatingEvent = updatingEvent.toBuilder().paid(request.getPaid()).build();
        if (request.getParticipantLimit() != null)
            updatingEvent = updatingEvent.toBuilder().participantLimit(request.getParticipantLimit()).build();
        if (request.getRequestModeration() != null)
            updatingEvent = updatingEvent.toBuilder().requestModeration(request.getRequestModeration()).build();

        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(StateAction.PUBLISH_EVENT.name())) {
                if (!updatingEvent.getState().equals(EventState.PENDING)) {
                    throw new ConcurrencyFailureException("Cannot publish the event because it's not in the right state: PUBLISHED");
                }
                LocalDateTime publishedTime = LocalDateTime.now().plusMinutes(1);
                if (!updatingEvent.getEventDate().minusHours(1L).isAfter(publishedTime)) {
                    throw new ConcurrencyFailureException("Incorrect EventDate or PublishedOn");
                }
                updatingEvent = updatingEvent.toBuilder().publishedOn(publishedTime).state(EventState.PUBLISHED).build();

            } else {
                if (!updatingEvent.getState().equals(EventState.PENDING)) {
                    throw new ConcurrencyFailureException("Cannot cancel the event because it's not in the right state: PUBLISHED");
                }
                updatingEvent = updatingEvent.toBuilder().state(EventState.CANCELED).build();
            }
        }
        if (request.getTitle() != null)
            updatingEvent = updatingEvent.toBuilder().title(request.getTitle()).build();
        return updatingEvent;
    }

    @Override
    public List<EventShortDto> getEventsByPublicParams(EventParamsForPublic params, HttpServletRequest request) {
        RestClientService restClientService = new RestClientService("http://localhost:9090/", new RestTemplateBuilder());
        RequestDtoIn requestDtoIn = RequestDtoIn.builder()
                .app("main-svc")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(formatter)).build();
        int post = restClientService.post(requestDtoIn);

        return eventRepository.getEventsByPublicParams(em, params).stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        RestClientService restClientService = new RestClientService("http://localhost:9090/", new RestTemplateBuilder());

        RequestDtoIn requestDtoIn = RequestDtoIn.builder()
                .app("main-svc")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(formatter)).build();
        int post = restClientService.post(requestDtoIn);

        return eventRepository.findById(eventId)
                .map(eventMapper::toEventFullDto)
                .orElseThrow(() -> new NoSuchElementException("Event not foun"));
    }

    @Override
    public EventFullDto getFullEventInfoByUser(Long userId, Long eventId) {
        Event event = eventRepository.getFullEventInfoByUser(em, userId, eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByUser(final UpdateEventUserRequest updatingRequest, Long userId, Long eventId) {
        String errorMessage = "Event with id=" + eventId + " and initiatorId=" + userId + " not exist";
        EventFullDto eventFullDto = eventRepository.getEventByInitiatorIdAndId(userId, eventId)
                .map(event -> {
                    Event updatedEvent = checkRequestAndFillUpdatingFilds(updatingRequest, event);
                    return eventMapper.toEventFullDto(updatedEvent);
                })
                .orElseThrow(() -> new NoSuchElementException(errorMessage));
        return eventFullDto;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfParticipantsEventOfCurrentUser(Long eventId) {
        return requestRepository.getRequestsByEventId(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
     * @param updateRequestStatus
     * @param userId
     * @param eventId
     * @return
     */
    @Override
    public EventRequestStatusUpdateResult changeRequestsStatus(EventRequestStatusUpdateRequest updateRequestStatus, Long userId, Long eventId) {

        Event event = eventRepository.findById(eventId)
                .filter(someEvent -> someEvent.getInitiatorId() == userId)
                .orElseThrow(() -> new NoSuchElementException("Event with id=" + eventId + " was not found"));
        //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        if (event.getParticipantLimit() == 0 || event.getRequestModeration() == false) {
            return new EventRequestStatusUpdateResult();
        }
        //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        if (event.getParticipantLimit() == 0) {
            throw new LimitReachedException("The participant limit has been reached");
        }

        List<Request> requestsFromList = requestRepository.getRequestsFromList(em, updateRequestStatus.getRequestIds(), userId, eventId);

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        //если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
        for (Request request: requestsFromList) {
            int limit;
            if ((limit = event.getParticipantLimit()) > 0) {
                Event eventWithLimit = event.toBuilder().participantLimit(limit - 1).build();
                eventRepository.save(eventWithLimit);
                Request requestWithChangedStatus = request.toBuilder().status(RequestStatus.valueOf(updateRequestStatus.getStatus())).build();
                Request savedUpdatedReqeust = requestRepository.save(requestWithChangedStatus);
                confirmedRequests.add(RequestMapper.toParticipationRequestDto(savedUpdatedReqeust));
            } else {
                Request requestWithChangedStatus = request.toBuilder().status(RequestStatus.REJECTED).build();
                Request savedUpdatedReqeust = requestRepository.save(requestWithChangedStatus);
                rejectedRequests.add(RequestMapper.toParticipationRequestDto(savedUpdatedReqeust));
            }
        }
        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();

        return result;
    }
}
