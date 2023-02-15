package ru.akpsv.main.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventMapper;
import ru.akpsv.main.event.dto.NewEventDto;
import ru.akpsv.main.event.model.Event;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    @Override
    public Optional<EventFullDto> create(Long userId,NewEventDto newEvent) {
        Event event = eventMapper.toEvent(newEvent, userId);
        Event savedEvent = eventRepository.save(event);
        EventFullDto eventFullDto = eventMapper.toEventFullDto(savedEvent);
        return Optional.of(eventFullDto);
    }
}
