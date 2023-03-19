package ru.akpsv.main.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventMapper;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.event.model.Event_;
import ru.akpsv.main.event.repository.CriteriaQueryPreparation;
import ru.akpsv.main.event.repository.EventRepository;
import ru.akpsv.statclient.RestClientService;
import ru.akpsv.statdto.RequestDtoIn;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    @Value("${main-svc.url}")
    private String serverUrl;

    @Override
    public List<EventShortDto> getEventsByPublicParams(EventParams params, HttpServletRequest request) {
        List<EventShortDto> eventShortDtos = getEventShortDtosByParams(params);
        registerRequestInStatSvc(request);
        return eventShortDtos;
    }

    private List<EventShortDto> getEventShortDtosByParams(EventParams params) {
        return eventRepository.getEvents(params, preparePublicRequest()).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    private CriteriaQueryPreparation<Event> preparePublicRequest() {
        return (params, cb, cq, fromEvent) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(fromEvent.get(Event_.STATE), EventState.PUBLISHED));
            params.getText().ifPresent(text -> predicates.add(cb.or(
                            cb.like(cb.lower(fromEvent.get(Event_.ANNOTATION)), ("%" + text + "%").toLowerCase()),
                            cb.like(cb.lower(fromEvent.get(Event_.DESCRIPTION)), ("%" + text + "%").toLowerCase())
                    )
            ));
            params.getCategories().ifPresent(categoryIds -> predicates.add(fromEvent.get(Event_.CATEGORY_ID).in(categoryIds)));
            params.getPaid().ifPresent(paid -> predicates.add(cb.equal(fromEvent.get(Event_.PAID), paid)));
            params.getOnlyAvailable()
                    .filter("true"::equals)
                    .ifPresent(available -> predicates.add(cb.equal(fromEvent.get(Event_.AVAILABLE_TO_PARICIPANTS), available)));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            params.getRangeStart()
                    .flatMap(startTimestamp -> params.getRangeEnd().map(endTimestamp ->
                            predicates.add(cb.between(
                                    fromEvent.get(Event_.EVENT_DATE),
                                    LocalDateTime.parse(startTimestamp, formatter),
                                    LocalDateTime.parse(endTimestamp, formatter)))
                    ))
                    .orElseGet(() -> predicates.add(cb.greaterThan(fromEvent.get(Event_.EVENT_DATE), LocalDateTime.now())));

            params.getSort().ifPresent(sort -> {
                if ("EVENT_DATE".equals(sort.toUpperCase())) cq.orderBy(cb.desc(fromEvent.get(Event_.EVENT_DATE)));
                else cq.orderBy(cb.desc(fromEvent.get(Event_.VIEWS)));
            });
            cq.select(fromEvent).where(predicates.toArray(Predicate[]::new));
            return cq;
        };
    }

    @Override
    public EventFullDto registerViewAndGetEventById(Long eventId, HttpServletRequest request) {
        EventFullDto eventFullDto = registerViewAndGetEventFullDto(eventId);
        registerRequestInStatSvc(request);
        return eventFullDto;
    }

    /**
     * Зарегистрировать просмотр и вернуть обновлённый EventFullDto
     *
     * @param eventId - ид события
     * @return - EventFullDto с увеличенным количеством просмотров
     */
    protected EventFullDto registerViewAndGetEventFullDto(Long eventId) {
        return eventRepository.findById(eventId)
                .map(event -> event.toBuilder().views(event.getViews() + 1).build())
                .map(eventRepository::save)
                .map(EventMapper::toEventFullDto)
                .orElseThrow(() -> new NoSuchElementException("Event with id=" + eventId + " not found"));
    }

    /**
     * Зарегистрировать запрос на сервере статистики
     *
     * @param request - данные запроса
     * @return - код http ответа
     */
    private int registerRequestInStatSvc(HttpServletRequest request) {
        RequestDtoIn requestDtoIn = RequestDtoIn.builder()
                .app("main-svc")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(formatter)).build();

        RestClientService restClientService = new RestClientService(serverUrl, new RestTemplateBuilder());
        return restClientService.post(requestDtoIn);
    }
}
