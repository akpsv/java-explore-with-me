package ru.akpsv.main.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventMapper;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.dto.NewEventDto;
import ru.akpsv.main.event.model.Event;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @PersistenceContext
    EntityManager em;
    @Override
    public Optional<EventFullDto> create(Long userId,NewEventDto newEvent) {
        Event event = eventMapper.toEvent(newEvent, userId);
        Event savedEvent = eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(savedEvent);
        return Optional.of(eventFullDto);
    }

    @Override
    public Optional<List<EventShortDto>> getEventsByUser(Long userId, Integer from, Integer size) {
        List<EventShortDto> groupOfEventShortDtos = eventRepository.getEventsByUser(em, userId, from, size).stream()
                .map(event -> eventMapper.toEventShortDto(event))
                .collect(Collectors.toList());
        return Optional.of(groupOfEventShortDtos);
    }
}
