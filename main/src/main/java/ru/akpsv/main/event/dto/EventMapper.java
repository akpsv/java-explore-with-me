package ru.akpsv.main.event.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akpsv.main.category.CategoryRepository;
import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.category.model.Category;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.user.UserRepository;
import ru.akpsv.main.user.dto.UserMapper;
import ru.akpsv.main.user.dto.UserShortDto;
import ru.akpsv.main.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EventMapper {
    private static CategoryRepository categoryRepository;
    private static UserRepository userRepository;
    private static DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private EventMapper(CategoryRepository categoryRepository, UserRepository userRepository){
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
                .requestModeration(newEvent.getRequestModeration())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        Category category = categoryRepository.findById(event.getCategoryId()).get();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();

        User user = userRepository.findById(event.getInitiatorId()).get();
        UserShortDto userShortDto = UserMapper.toUserShotDto(user);
        String publishedOn;
        if (event.getPublishedOn() == null) {
            publishedOn = "";
        } else {
            publishedOn = event.getPublishedOn().format(formatter);
        }
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(formatter))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(formatter))
                .id(event.getId())
                .initiator(userShortDto)
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
        Category category = categoryRepository.findById(event.getCategoryId()).get();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();

        User user = userRepository.findById(event.getInitiatorId()).get();
        UserShortDto userShortDto = UserMapper.toUserShotDto(user);

        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(formatter))
                .initiator(userShortDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}
