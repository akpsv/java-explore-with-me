package ru.akpsv.main.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.main.error.ViolationOfRestrictionsException;
import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.dto.*;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.event.model.Event_;
import ru.akpsv.main.event.repository.CriteriaQueryPreparation;
import ru.akpsv.main.event.repository.EventRepository;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventFullDto> getEventsByAdminParams(EventParams param) {
        return eventRepository.getEvents(param, prepareAdminRequest()).stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    private CriteriaQueryPreparation<Event> prepareAdminRequest() {
        return (params, cb, cq, fromEvent) -> {
            List<Predicate> predicates = new ArrayList<>();
            params.getUsers().ifPresent(userIds -> predicates.add(fromEvent.get(Event_.INITIATOR_ID).in(userIds)));
            params.getStates()
                    .ifPresent(groupOfEventStates -> {
                        List<EventState> collectOfEventStates = groupOfEventStates.stream().map(EventState::valueOf).collect(Collectors.toList());
                        predicates.add(fromEvent.get(Event_.STATE).in(collectOfEventStates));
                    });
            params.getCategories().ifPresent(categoryIds -> predicates.add(fromEvent.get(Event_.CATEGORY_ID).in(categoryIds)));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            params.getRangeStart().flatMap(startTimestamp -> params.getRangeEnd().map(endTimestamp ->
                    predicates.add(cb.between(
                            fromEvent.get(Event_.EVENT_DATE),
                            LocalDateTime.parse(startTimestamp, formatter),
                            LocalDateTime.parse(endTimestamp, formatter)))
            ));
            cq.orderBy(cb.desc(fromEvent.get(Event_.EVENT_DATE)));
            cq.select(fromEvent).where(predicates.toArray(Predicate[]::new));
            return cq;
        };
    }

    @Override
    public EventFullDto updateEventByAdmin(UpdateEventAdminRequest updatingRequest, Long eventId) {
        return eventRepository.findById(eventId)
                .map(updatingEvent -> checkAdminRequestAndFillUpdatingFilds(updatingRequest, updatingEvent))
                .map(eventRepository::save)
                .map(EventMapper::toEventFullDto)
                .orElseThrow(() -> new NoSuchElementException("Event witn id=" + eventId + " was not found"));
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
                checkOfPending(updatingEvent);
                updatingEvent = checkAndSetPublishedOnTime(updatingEvent);
            } else {
                updatingEvent = checkAndSetCanceledState(updatingEvent);
            }
        }
        return updatingEvent;
    }

    private void checkOfPending(Event updatingEvent) {
        if (!updatingEvent.getState().equals(EventState.PENDING)) {
            throw new ViolationOfRestrictionsException("Cannot publish the event because it's not in the right state: PUBLISHED");
        }
    }

    private Event checkAndSetPublishedOnTime(Event updatingEvent) {
        LocalDateTime publishedTime = LocalDateTime.now().plusMinutes(1);
        if (!updatingEvent.getEventDate().minusHours(1L).isAfter(publishedTime)) {
            throw new ViolationOfRestrictionsException("Incorrect EventDate or PublishedOn");
        }
        updatingEvent = updatingEvent.toBuilder().publishedOn(publishedTime).state(EventState.PUBLISHED).build();
        return updatingEvent;
    }

    private Event checkAndSetCanceledState(Event updatingEvent) {
        if (!updatingEvent.getState().equals(EventState.PENDING)) {
            throw new ViolationOfRestrictionsException("Cannot cancel the event because it's not in the right state: PUBLISHED");
        }
        updatingEvent = updatingEvent.toBuilder().state(EventState.CANCELED).build();
        return updatingEvent;
    }

}
