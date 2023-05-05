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
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EventFullDto create(Long userId, NewEventDto newEvent) {
        return Stream.of(EventMapper.toEvent(newEvent, userId))
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
                .collect(toList());
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
        switch (request.getStateAction()) {
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
                .collect(toList());
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
        log.debug("Проверка, что заявка принадлежит текущему пользователю");
        Event event = checkEventOwner(userId, eventId);

        log.debug("Проверка, что заявка находится в статусе ожидания (PENDING)");
        List<Request> pendingRequests = getAndCheckPendingRequests(updateRequestStatus, userId, eventId);

        log.debug("Проверка отключения лимита заявок и пре-модерации");
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration())
            confirmRequestIfLimitIsZeroOrPreModerationIsFalse(updateRequestStatus, pendingRequests);

        //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        log.debug("Проверка достижения лимита по заявкам на данное событие (Ожидается код ошибки 409)");
        if (event.getConfirmedRequests() >= event.getParticipantLimit())
            throw new LimitReachedException("The participant limit has been reached");

        log.debug("Выполнение обработки заявок");
        return handleGroupOfRequests(updateRequestStatus, event, pendingRequests);
    }

    /**
     * Проверка принадлежности события запрашивающему пользователю
     *
     * @param userId
     * @param eventId
     * @return
     */
    protected Event checkEventOwner(Long userId, Long eventId) {
        return eventRepository.findById(eventId)
                .filter(someEvent -> someEvent.getInitiatorId().equals(userId))
                .orElseThrow(() -> new NoSuchElementException("Event with id=" + eventId + " was not found"));
    }

    protected EventRequestStatusUpdateResult handleGroupOfRequests(EventRequestStatusUpdateRequest updateRequestStatus, Event event, List<Request> pendingRequests) {
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        //если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
        log.debug("Обработать заявки в соответствии с лимитом. Отклонение заявок которым не хватило разрешений.");
        //Если статус заявок обновляется на CONFIRMED, то происходит обработка, иначе все заявки отклоняются
        if (updateRequestStatus.getStatus().equals(RequestStatus.CONFIRMED.name())) {
            log.debug("если лимит подтверждённых заявок ещё не исчерпан, то заявка одобряется, иначе отклоняется");
            pendingRequests.stream()
                    .forEach(request -> {
                        if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                            updateCountOfConfirmedRequests(event);
                            confirmRequestAndAddToGroup(confirmedRequests, request);
                        } else {
                            setUnavailabilityForParticipation(event);
                            rejectRequests(request).ifPresent(rejectedRequests::add);
                        }
                    });
        } else if (updateRequestStatus.getStatus().equals(RequestStatus.REJECTED.name())) {
            log.debug("отклонить все заявки");
            pendingRequests.stream()
                    .map(this::rejectRequests)
                    .map(Optional::get)
                    .forEach(rejectedRequests::add);
        }

        log.debug("Вернуть список заявок");
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    private void setUnavailabilityForParticipation(Event event) {
        if (event.getAvailableToParicipants()) {
            eventRepository.save(event.toBuilder().availableToParicipants(false).build());
        }
    }

    private void confirmRequestAndAddToGroup(List<ParticipationRequestDto> confirmedRequests, Request request) {
        Stream.of(request)
                .map(changingRequest -> changingRequest.toBuilder().status(RequestStatus.CONFIRMED).build())
                .map(requestRepository::save)
                .map(RequestMapper::toParticipationRequestDto)
                .forEach(confirmedRequests::add);
    }

    protected Optional<ParticipationRequestDto> rejectRequests(Request request) {
        return Stream.of(request)
                .map(changingRequest -> changingRequest.toBuilder().status(RequestStatus.REJECTED).build())
                .map(requestRepository::save)
                .map(RequestMapper::toParticipationRequestDto)
                .findFirst();
    }

    protected void updateCountOfConfirmedRequests(Event event) {
        Stream.of(event)
                .map(updatingEvent -> updatingEvent.toBuilder().confirmedRequests(updatingEvent.getConfirmedRequests() + 1).build())
                .forEach(eventRepository::save);
    }

    /**
     * Получить запросы на участие в событии и проверить что они находятся в статусе ожидания
     * иначе выбросить исключение о нарушении ограничений
     *
     * @param updateRequestStatus
     * @param userId
     * @param eventId
     * @return
     */
    protected List<Request> getAndCheckPendingRequests(EventRequestStatusUpdateRequest updateRequestStatus,
                                                       Long userId, Long eventId) {
        log.debug("Получение заявок из репозитория");
        return requestRepository.getRequestsFromList(updateRequestStatus.getRequestIds(), userId, eventId)
                .stream()
                .map(request -> {
                            if (!request.getStatus().equals(RequestStatus.PENDING))
                                throw new ViolationOfRestrictionsException("\"For the requested operation the conditions are not met.");
                            return request;
                        }
                )
                .collect(toList());
    }

    protected EventRequestStatusUpdateResult confirmRequestIfLimitIsZeroOrPreModerationIsFalse(
            EventRequestStatusUpdateRequest updatingRequest,
            List<Request> pendingRequests
    ) {
        log.debug("Авто согласование заявок не требующих подтвердждения");
        //Согласовать все заявки и добавить в группу подтверждённых
        List<ParticipationRequestDto> requestWithChangedStatus = pendingRequests.stream()
                .map(request -> request.toBuilder().status(RequestStatus.valueOf(updatingRequest.getStatus())).build())
                .map(RequestMapper::toParticipationRequestDto)
                .collect(toList());

        RequestStatus requestStatus = RequestStatus.valueOf(updatingRequest.getStatus());
        if (requestStatus.equals(RequestStatus.CONFIRMED))
            return EventRequestStatusUpdateResult.builder().confirmedRequests(requestWithChangedStatus).build();
        else if (requestStatus.equals(RequestStatus.REJECTED))
            return EventRequestStatusUpdateResult.builder().rejectedRequests(requestWithChangedStatus).build();
        else throw new ViolationOfRestrictionsException("RequestStatus is incorrect.");
    }
}
