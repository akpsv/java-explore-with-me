package ru.akpsv.main.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import ru.akpsv.main.error.LimitReachedException;
import ru.akpsv.main.error.ViolationOfRestrictionsException;
import ru.akpsv.main.event.dto.*;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.request.RequestRepository;
import ru.akpsv.main.request.dto.ParticipationRequestDto;
import ru.akpsv.main.request.dto.RequestMapper;
import ru.akpsv.main.request.model.Request;
import ru.akpsv.main.request.model.RequestStatus;
import ru.akpsv.statclient.RestClientService;
import ru.akpsv.statdto.RequestDtoIn;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    @PersistenceContext
    EntityManager em;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
    public EventFullDto updateEventByAdmin(UpdateEventAdminRequest updatingRequest, Long eventId) {
        String noSuchElementExceptionMessage = "Event witn id=" + eventId + " was not found";
        Event updatingEvent = eventRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException(noSuchElementExceptionMessage));

        updatingEvent = checkAdminRequestAndFillUpdatingFilds(updatingRequest, updatingEvent);
        Event savedUpdatedEvent = eventRepository.save(updatingEvent);
        return eventMapper.toEventFullDto(savedUpdatedEvent);
    }

    @Override
    public EventFullDto updateEventByCurrentUser(UpdateEventUserRequest updatingRequest, Long userId, Long eventId) {
        String errorMessage = "Event with id=" + eventId + " and initiatorId=" + userId + " not exist";
        return eventRepository.getEventByInitiatorIdAndId(userId, eventId)
                .map(event -> {
                    Event updatedEvent = checkCurrentUserRequestAndFillUpdatingFields(updatingRequest, event);
                    return eventMapper.toEventFullDto(updatedEvent);
                })
                .orElseThrow(() -> new NoSuchElementException(errorMessage));
    }

    /**
     * Проверка запроса на изменение и заполнение полей обновляемого события для администратора
     *
     * @param request
     * @param updatingEvent
     * @return
     */
    protected Event checkAdminRequestAndFillUpdatingFilds(UpdateEventRequest request, Event updatingEvent) {
        updatingEvent = checkAndFillBaseFields(request, updatingEvent);
        updatingEvent = checkAdminConditionsAndFillStateField(request, updatingEvent);
        return updatingEvent;
    }

    /**
     * Проверка запроса на изменение и заполнение полей обновляемого события для текущего пользователя
     *
     * @param request
     * @param updatingEvent
     * @return
     */
    protected Event checkCurrentUserRequestAndFillUpdatingFields(UpdateEventRequest request, Event updatingEvent) {
        if (request.getEventDate() != null && LocalDateTime.parse(request.getEventDate(), formatter).isBefore(LocalDateTime.now())) {
            throw new ViolationOfRestrictionsException("Changing event date is not correct");
        }
        updatingEvent = checkAndFillBaseFields(request, updatingEvent);
        updatingEvent = checkUserConditionsAndFillStateField(request, updatingEvent);
        return updatingEvent;
    }

    /**
     * Проверка условий изменения поля state для текущего пользователя и его заполнение
     *
     * @param request
     * @param updatingEvent
     * @return
     */
    private Event checkUserConditionsAndFillStateField(UpdateEventRequest request, Event updatingEvent) {
        if (updatingEvent.getState().equals(EventState.PUBLISHED)) {
            throw new ViolationOfRestrictionsException("Only pending or canceled events can be changed");
        }
        if (request.getStateAction() != null) {
            //изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
            if (updatingEvent.getState().equals(EventState.CANCELED) || updatingEvent.getState().equals(EventState.PENDING)) {
                //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
                if (updatingEvent.getEventDate().minusHours(2).isAfter(LocalDateTime.now())) {
                    if (request.getStateAction().equals(StateAction.SEND_TO_REVIEW.name())) {
                        updatingEvent = updatingEvent.toBuilder().state(EventState.PENDING).build();
                    } else if (request.getStateAction().equals(StateAction.CANCEL_REVIEW.name())) {
                        updatingEvent = updatingEvent.toBuilder().state(EventState.CANCELED).build();
                    }
                } else {
                    throw new ViolationOfRestrictionsException("EventDate not correct");
                }
            }
        }
        return updatingEvent;
    }

    /**
     * Проверка условий изменения поля state для текущего пользователя и его заполнение
     * Редактирование данных любого события администратором. Валидация данных не требуется.
     * Обратите внимание:
     * дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
     * событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
     * событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409
     *
     * @param request
     * @param updatingEvent
     * @return
     */
    private Event checkAdminConditionsAndFillStateField(UpdateEventRequest request, Event updatingEvent) {
        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(StateAction.PUBLISH_EVENT.name())) {
                if (!updatingEvent.getState().equals(EventState.PENDING)) {
                    throw new ViolationOfRestrictionsException("Cannot publish the event because it's not in the right state: PUBLISHED");
                }
                LocalDateTime publishedTime = LocalDateTime.now().plusMinutes(1);
                if (!updatingEvent.getEventDate().minusHours(1L).isAfter(publishedTime)) {
                    throw new ConcurrencyFailureException("Incorrect EventDate or PublishedOn");
                }
                updatingEvent = updatingEvent.toBuilder().publishedOn(publishedTime).state(EventState.PUBLISHED).build();

            } else {
                if (!updatingEvent.getState().equals(EventState.PENDING)) {
                    throw new ViolationOfRestrictionsException("Cannot cancel the event because it's not in the right state: PUBLISHED");
                }
                updatingEvent = updatingEvent.toBuilder().state(EventState.CANCELED).build();
            }
        }
        return updatingEvent;
    }

    /**
     * Проверка всех полей кроме State на присутствие какого-то значения.
     * Если значение присутствует, то значение обновляется из запроса.
     *
     * @param request
     * @param updatingEvent
     * @return
     */
    private Event checkAndFillBaseFields(UpdateEventRequest request, Event updatingEvent) {
        if (request.getAnnotation() != null)
            updatingEvent = updatingEvent.toBuilder().annotation(request.getAnnotation()).build();
        if (request.getCategory() != null)
            updatingEvent = updatingEvent.toBuilder().categoryId(request.getCategory()).build();
        if (request.getDescription() != null)
            updatingEvent = updatingEvent.toBuilder().description(request.getDescription()).build();

        if (request.getEventDate() != null) {
            updatingEvent = updatingEvent.toBuilder().eventDate(LocalDateTime.parse(request.getEventDate(), formatter)).build();
        }

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

    @Value("${main-svc.url}")
    private String serverUrl;

    @Override
    public List<EventShortDto> getEventsByPublicParams(EventParamsForPublic params, HttpServletRequest request) {

        RestClientService restClientService = new RestClientService(serverUrl, new RestTemplateBuilder());
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
        RestClientService restClientService = new RestClientService(serverUrl, new RestTemplateBuilder());

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
        List<Request> requestsFromList = requestRepository.getRequestsFromList(em, updateRequestStatus.getRequestIds(), userId, eventId);

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
