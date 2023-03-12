package ru.akpsv.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.akpsv.main.error.LimitReachedException;
import ru.akpsv.main.error.ViolationOfRestrictionsException;
import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.EventRequestStatusUpdateRequest;
import ru.akpsv.main.event.EventRequestStatusUpdateResult;
import ru.akpsv.main.event.dto.*;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.event.model.Event_;
import ru.akpsv.main.event.repository.CriteriaQueryPreparation;
import ru.akpsv.main.event.repository.EventRepository;
import ru.akpsv.main.request.dto.ParticipationRequestDto;
import ru.akpsv.main.request.dto.RequestMapper;
import ru.akpsv.main.request.model.Request;
import ru.akpsv.main.request.model.RequestStatus;
import ru.akpsv.main.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EventFullDto create(Long userId, NewEventDto newEvent) {
        Event event = EventMapper.toEvent(newEvent, userId)
                .toBuilder()
                .state(EventState.PENDING)
                .build();
        return Stream.of(event)
                .map(eventRepository::save)
                .map(EventMapper::toEventFullDto)
                .findFirst()
                .get();
    }

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        EventParams eventParams = EventParams.builder().from(from).size(size).build();

        CriteriaQueryPreparation<Event> request = (params, cb, cq, fromEvent) -> {
            cq.select(fromEvent).where(cb.equal(fromEvent.get(Event_.INITIATOR_ID), userId));
            return cq;
        };

        return eventRepository.getEvents(eventParams, request).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }


    @Override
    public EventFullDto updateEventByCurrentUser(UpdateEventUserRequest updatingRequest, Long userId, Long eventId) {
        return eventRepository.getEventByInitiatorIdAndId(userId, eventId)
                .map(updatingEvent -> checkRequestAndSetFields(updatingRequest, updatingEvent))
                .map(eventRepository::save)
                .map(EventMapper::toEventFullDto)
                .orElseThrow(() -> new NoSuchElementException("Event with id=" + eventId + " and initiatorId=" + userId + " not exist"));
    }

    /**
     * Проверка запроса на изменение и заполнение полей обновляемого события для текущего пользователя
     *
     * @param request
     * @param updatingEvent
     * @return
     */
    protected Event checkRequestAndSetFields(UpdateEventRequest request, Event updatingEvent) {
        checkCorrectEventDate(request);
        return Stream.of(updatingEvent)
                .map(event -> checkAndSetBaseFields(request, event))
                .map(event -> checkConditionsAndSetStateField(request, event))
                .findFirst().get();
    }

    protected void checkCorrectEventDate(UpdateEventRequest request) throws ViolationOfRestrictionsException {
        Predicate<UpdateEventRequest> eventDateNotNull = req -> req.getEventDate() == null;
        Predicate<UpdateEventRequest> eventDateAfter = req -> LocalDateTime.parse(req.getEventDate(), formatter).isBefore(LocalDateTime.now());
        Optional.ofNullable(request)
                .filter(eventDateNotNull.or(eventDateAfter))
                .orElseThrow(() -> new ViolationOfRestrictionsException("Changing event date is not correct"));
    }

    /**
     * Проверка всех полей кроме State на присутствие какого-то значения.
     * Если значение присутствует, то значение обновляется из запроса.
     *
     * @param request
     * @param updatingEvent
     * @return
     */
    protected Event checkAndSetBaseFields(UpdateEventRequest request, Event updatingEvent) {
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
        if (request.getTitle() != null)
            updatingEvent = updatingEvent.toBuilder().title(request.getTitle()).build();
        return updatingEvent;
    }


    /**
     * Проверка условий изменения поля state для текущего пользователя и его заполнение
     *
     * @param request
     * @param updatingEvent
     * @return
     */
    protected Event checkConditionsAndSetStateField(UpdateEventRequest request, Event updatingEvent) {
        return Optional.ofNullable(request.getStateAction())
                .map(unusedValue -> checkConditionsAndChangeEventState(request, updatingEvent))
                .orElse(updatingEvent);
    }

    private Event checkConditionsAndChangeEventState(UpdateEventRequest request, Event updatingEvent) {
        Predicate<Event> isEventStateNotPublished = event -> !event.getState().equals(EventState.PUBLISHED);
        Predicate<Event> isEventStateCanceled = event -> event.getState().equals(EventState.CANCELED);
        Predicate<Event> isEventStatePanding = event -> event.getState().equals(EventState.PENDING);
        Predicate<Event> isEventDateAfterNow = event -> event.getEventDate().minusHours(2).isAfter(LocalDateTime.now());

        return Optional.of(updatingEvent)
                .filter(isEventStateNotPublished)
                .filter(isEventStateCanceled.or(isEventStatePanding))
                .filter(isEventDateAfterNow)
                .map(event -> checkStateActionAndChangeEventState(request, event))
                .orElseThrow(() -> new ViolationOfRestrictionsException("EventDate not correct"));
    }

    private Event checkStateActionAndChangeEventState(UpdateEventRequest request, Event updatingEvent) {
        switch (request.getStateAction()){
            case "SEND_TO_REVIEW":
                updatingEvent = updatingEvent.toBuilder().state(EventState.PENDING).build();
                break;
            case "CANCEL_REVIEW":
                updatingEvent = updatingEvent.toBuilder().state(EventState.CANCELED).build();
                break;
        }
        return updatingEvent;
    }


    @Override
    public EventFullDto getFullEventInfoByUser(Long userId, Long eventId) {
        Event event = eventRepository.getFullEventInfoByUser(userId, eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOfParticipantsEventOfCurrentUser(Long eventId) {
        return requestRepository.getRequestsByEventId(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
     *
     * @param updateRequestStatus
     * @param userId
     * @param eventId
     * @return
     */
    @Override
    public EventRequestStatusUpdateResult changeRequestsStatusCurrentUser(EventRequestStatusUpdateRequest updateRequestStatus, Long userId, Long eventId) {
        //Проверка, что заявка принадлежит текущему пользователю
        log.debug("Проверка, что заявка принадлежит текущему пользователю");
        Event event = eventRepository.findById(eventId)
                .filter(someEvent -> someEvent.getInitiatorId().equals(userId))
                .orElseThrow(() -> new NoSuchElementException("Event with id=" + eventId + " was not found"));
        //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        log.debug("Проверка лимита заявок и пре-модерации");
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            return new EventRequestStatusUpdateResult();
        }
        //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new LimitReachedException("The participant limit has been reached");
        }

        log.debug("Получение заявок из репозитория");
        List<Request> requestsFromList = requestRepository.getRequestsFromList(updateRequestStatus.getRequestIds(), userId, eventId);

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        //если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
        log.debug("Обработать заявки в соответствии с лимитом. Отклонение заявок которым не хватило разрешений.");
        for (Request request : requestsFromList) {
            if (!event.getParticipantLimit().equals(event.getConfirmedRequests())) {
                Event eventWithLimit = event.toBuilder().confirmedRequests(event.getConfirmedRequests() + 1).build();
                eventRepository.save(eventWithLimit);
                Request requestWithChangedStatus = request.toBuilder().status(RequestStatus.valueOf(updateRequestStatus.getStatus())).build();
                Request savedUpdatedReqeust = requestRepository.save(requestWithChangedStatus);
                if (updateRequestStatus.getStatus().equals(RequestStatus.REJECTED.name())) {
                    rejectedRequests.add(RequestMapper.toParticipationRequestDto(savedUpdatedReqeust));
                } else if (updateRequestStatus.getStatus().equals(RequestStatus.CONFIRMED.name())) {
                    confirmedRequests.add(RequestMapper.toParticipationRequestDto(savedUpdatedReqeust));
                }
            } else {
                Request requestWithChangedStatus = request.toBuilder().status(RequestStatus.REJECTED).build();
                Request savedUpdatedReqeust = requestRepository.save(requestWithChangedStatus);
                rejectedRequests.add(RequestMapper.toParticipationRequestDto(savedUpdatedReqeust));
            }
        }
        log.debug("Вернуть список заявок");
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }
}
