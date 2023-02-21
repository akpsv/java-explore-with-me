package ru.akpsv.main.event;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import ru.akpsv.dto.RequestDtoIn;
import ru.akpsv.dto.statclient.RestClientService;
import ru.akpsv.main.event.dto.*;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
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
    public EventFullDto updateEvent(UpdateEventAdminRequest updateEvent, Long eventId) {
        String noSuchElementExceptionMessage = "Event witn id=" + eventId + " was not found";
        Event updatingEvent = eventRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException(noSuchElementExceptionMessage));

        updatingEvent = checkRequestAndFillUpdatingFilds(updateEvent, updatingEvent);
        Event savedUpdatedEvent = eventRepository.save(updatingEvent);
        return eventMapper.toEventFullDto(savedUpdatedEvent);
    }

    protected Event checkRequestAndFillUpdatingFilds(UpdateEventAdminRequest request, Event updatingEvent) {
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
}
