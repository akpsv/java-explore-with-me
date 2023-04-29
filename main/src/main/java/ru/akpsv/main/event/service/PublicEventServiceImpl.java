package ru.akpsv.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventMapper;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.event.model.Event_;
import ru.akpsv.main.event.repository.CriteriaQueryPreparation;
import ru.akpsv.main.event.repository.EventRepository;
import ru.akpsv.statclient.WebFluxClientService;
import ru.akpsv.statdto.EndpointHit;
import ru.akpsv.statdto.StatDtoOut;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
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
public class PublicEventServiceImpl implements PublicEventService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventRepository eventRepository;
    WebFluxClientService webClient = new WebFluxClientService(WebClient.builder());
//    @Value("${main-svc.url}")
//    private String serverUrl;

    @Override
    public List<EventShortDto> getEventsByPublicParams(EventParams params, HttpServletRequest request) {
        log.info("Вызван метод getEventsByPublicParams с параметрами params= {}, request={}", params, request);
        log.debug("Зарегистрировть запрос {}", request);
        registerRequestInStatSvc(request);

        log.debug("Получить отсортированый поток объектов EventShortDto");
        Flux<EventShortDto> eventShortDtos = Flux.fromIterable(getEventShortDtosByParams(params));

        log.debug("Запросить у сервера статистики колчество просмотров каждого события.");
        Flux<StatDtoOut> statDtoOuts = getStatDtoOutsFromStatSvc(params, eventShortDtos, webClient);

        log.debug("Добавить число просмотров в объекты EventShortDto и вернуть поток. {}");
        Flux<EventShortDto> eventShortDtoFlux = addViewsToEventShortDtos(eventShortDtos, statDtoOuts);
//        Stream<EventShortDto> eventShortDtoFlux = addViewsToEventShortDtos(eventShortDtos, statDtoOuts);

//        List<EventShortDto> resultEventShortDtos = eventShortDtoFlux.toStream().collect(Collectors.toList());
        List<EventShortDto> resultEventShortDtos = eventShortDtoFlux.toStream().collect(Collectors.toList());
        return resultEventShortDtos;
    }

    protected Flux<EventShortDto> addViewsToEventShortDtos(Flux<EventShortDto> eventShortDtos, Flux<StatDtoOut> statDtoOuts) {
////////////////////////////////////////////////////////////////////////////////////////////

        Flux<EventShortDto> eventShortDtoFlux = statDtoOuts.hasElements()
                .map(isAvailable -> {
                            Flux<EventShortDto> eventShortDtoFlux1;
                            if (isAvailable) {
                                eventShortDtoFlux1 = eventShortDtos.flatMap(eventSD ->
                                        statDtoOuts
                                                .filter(stat -> getIdFromUri(stat.getUri()).equals(eventSD.getId()))
                                                .map(stat -> eventSD.toBuilder().views(stat.getHits()).build())
                                );
                                return eventShortDtoFlux1;
                            } else {
                                return eventShortDtos;
                            }
                        }
                )
                .block();

        return eventShortDtoFlux;
    }

    private Long getIdFromUri(String uri) {
        if (uri != null) {
            try {
                URL url = new URL(uri);
                Path eventId = Path.of(url.getFile()).getFileName();
                return Long.valueOf(eventId.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return -1L;
    }

    protected Flux<StatDtoOut> getStatDtoOutsFromStatSvc(EventParams params,
                                                         Flux<EventShortDto> groupOfEventShortDtos,
                                                         WebFluxClientService webClient) {
        EventParams dateTimeRange = prepareDateTimeRange(params);
        Boolean uniqueValue = prepareUniqueValue(params);

        return groupOfEventShortDtos
                .flatMap(eventShortDto -> Mono.just(eventShortDto)
                        .map(shortDto -> "/event/" + eventShortDto.getId())
                        .subscribeOn(Schedulers.parallel()))
                .collectList()
                .flatMapMany(groupUris -> webClient.getStats(dateTimeRange.getRangeStart().get(),
                        dateTimeRange.getRangeEnd().get(), groupUris, uniqueValue));
    }

    private Boolean prepareUniqueValue(EventParams params) {
        if (params.getOnlyAvailable().isPresent()) {
            return params.getOnlyAvailable().get();
        }
        return false;
    }

    private EventParams prepareDateTimeRange(EventParams eventParams) {
        EventParams datetimeRange = new EventParams();
        eventParams.getRangeStart()
                .ifPresentOrElse(start -> eventParams.getRangeEnd()
                        .ifPresent(end -> {
                            datetimeRange.setRangeStart(Optional.of(start));
                            datetimeRange.setRangeEnd(Optional.of(end));
                        }), () -> {
                    datetimeRange.setRangeStart(Optional.of(LocalDateTime.now().format(formatter)));
                    datetimeRange.setRangeEnd(Optional.of(LocalDateTime.MAX.format(formatter)));
                });
        return datetimeRange;
    }

    protected List<String> createGroupOfUris(List<EventShortDto> eventShortDtos) {
        return eventShortDtos.stream()
                .map(eventShortDto -> "/event/" + eventShortDto.getId())
                .collect(Collectors.toList());
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
        registerRequestInStatSvc(request);
        EventFullDto eventFullDto = registerViewAndGetEventFullDto(eventId);
        return eventFullDto;
    }

    /**
     * Зарегистрировать просмотр и вернуть обновлённый EventFullDto
     *
     * @param eventId - ид события
     * @return - EventFullDto с увеличенным количеством просмотров
     */
    protected EventFullDto registerViewAndGetEventFullDto(Long eventId) {
        EventShortDto eventShortDto = eventRepository.findById(eventId)
                .map(EventMapper::toEventShortDto)
                .orElseThrow(() -> new NoSuchElementException("Event with id=" + eventId + " not found"));

        Flux<EventShortDto> eventShortDtoFlux = Flux.just(eventShortDto);

        EventParams eventParams = EventParams.builder()
                .onlyAvailable(Optional.of(false))
                .rangeStart(Optional.of(LocalDateTime.MIN.format(formatter)))
                .rangeEnd(Optional.of(LocalDateTime.MAX.format(formatter)))
                .build();

        Flux<StatDtoOut> statDtoOutsFromStatSvc = getStatDtoOutsFromStatSvc(eventParams, eventShortDtoFlux, webClient);

        List<EventShortDto> groupOfEventWithView = addViewsToEventShortDtos(eventShortDtoFlux, statDtoOutsFromStatSvc)
                .toStream()
                .collect(Collectors.toList());

        Long views = getViews(groupOfEventWithView);

        return eventRepository.findById(eventId)
                .map(EventMapper::toEventFullDto)
                .map(eventFullDto -> eventFullDto.toBuilder().views(views).build())
                .orElseThrow(() -> new NoSuchElementException("Event with id=" + eventId + " not found"));
    }

    private Long getViews(List<EventShortDto> groupOfEventWithView) {
        Long views = 0L;
        if (groupOfEventWithView.size() > 0) {
            views = groupOfEventWithView.get(0).getViews();
        }
        return views;
    }

    /**
     * Зарегистрировать запрос на сервере статистики
     *
     * @param request - данные запроса
     * @return - код http ответа
     */
    private Mono<Integer> registerRequestInStatSvc(HttpServletRequest request) {
        EndpointHit endpointHit = EndpointHit.builder()
                .app("main-svc")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(formatter)).build();
        return webClient.saveHit(Mono.just(endpointHit));
    }
}
