package ru.akpsv.main.event.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akpsv.main.category.CategoryRepository;
import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.category.dto.CategoryMapper;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.user.dto.UserMapper;
import ru.akpsv.main.user.dto.UserShortDto;
import ru.akpsv.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EventMapper {
    private static CategoryRepository categoryRepository;
    private static UserRepository userRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private EventMapper(CategoryRepository categoryRepository, UserRepository userRepository) {
        EventMapper.categoryRepository = categoryRepository;
        EventMapper.userRepository = userRepository;
    }

    public static Event toEvent(NewEventDto newEvent, Long initiatorId) {
        return Event.builder()
                .annotation(newEvent.getAnnotation())
                .categoryId(newEvent.getCategory())
                .description(newEvent.getDescription())
                .initiatorId(initiatorId)
                .eventDate(LocalDateTime.parse(newEvent.getEventDate(), EventMapper.formatter))
                .location(newEvent.getLocation())
                .title(newEvent.getTitle())
                .paid(newEvent.getPaid())
                .participantLimit(newEvent.getParticipantLimit())
                .confirmedRequests(0L)
                .availableToParicipants(true)
                .requestModeration(newEvent.getRequestModeration())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        String publishedOn = Optional.ofNullable(event.getPublishedOn())
                .map(timestamp -> event.getPublishedOn().format(formatter))
                .orElse("");

        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(getCategoryDto(event))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(formatter))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(formatter))
                .id(event.getId())
                .initiator(getUserShortDto(event))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(publishedOn)
                .requestModeration(event.getRequestModeration())
                .state(event.getState().name())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(getCategoryDto(event))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(formatter))
                .initiator(getUserShortDto(event))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    private static UserShortDto getUserShortDto(Event event) {
        return userRepository.findById(event.getInitiatorId())
                .map(UserMapper::toUserShotDto)
                .orElseThrow(() -> new NoSuchElementException("User with id=" + event.getInitiatorId() + " not exist"));
    }

    private static CategoryDto getCategoryDto(Event event) {
        return categoryRepository.findById(event.getCategoryId())
                .map(CategoryMapper::toCategoryDto)
                .orElseThrow(() -> new NoSuchElementException("Category with id=" + event.getCategoryId() + " not exist"));
    }
}
