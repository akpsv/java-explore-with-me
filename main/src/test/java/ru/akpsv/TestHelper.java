package ru.akpsv;

import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.category.model.Category;
import ru.akpsv.main.compilation.Compilation;
import ru.akpsv.main.compilation.dto.NewCompilationDto;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.dto.NewEventDto;
import ru.akpsv.main.event.dto.UpdateEventAdminRequest;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.event.model.Location;
import ru.akpsv.main.request.model.Request;
import ru.akpsv.main.request.model.RequestStatus;
import ru.akpsv.main.subscribe.model.Subscribe;
import ru.akpsv.main.subscribe.model.SubscribeId;
import ru.akpsv.main.user.dto.NewUserRequest;
import ru.akpsv.main.user.dto.UserDto;
import ru.akpsv.main.user.dto.UserShortDto;
import ru.akpsv.main.user.model.User;
import ru.akpsv.statdto.StatDtoOut;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;

public class TestHelper {

    public static NewUserRequest createNewUserRequest(String email) {
        return NewUserRequest.builder()
                .name("user")
                .email(email)
                .build();
    }

    public static UserDto createUserDto(Long userId, String email) {
        return UserDto.builder()
                .id(userId)
                .name("user")
                .email(email)
                .build();
    }

    public static User createUser(Long userId, String userEmail) {
        return User.builder()
                .id(userId)
                .name("user")
                .email(userEmail)
                .build();
    }

    public static NewEventDto createNewEventDto() {
        return NewEventDto.builder()
                .annotation("annotation")
                .category(1L)
                .description("description")
                .eventDate("2024-12-31 17:10:05")
                .location(new Location(55.755864, 37.617698))
                .paid(false)
                .participantLimit(0L)
                .requestModeration(true)
                .title("title")
                .build();
    }

    public static Event createEvent(Long eventId, Long initiatorId, Long categoryId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        return Event.builder()
                .id(eventId)
                .annotation("annotation")
                .categoryId(categoryId)
                .description("description")
                .createdOn(LocalDateTime.parse("2024-10-31 17:10:05", formatter))
                .eventDate(LocalDateTime.parse("2024-12-31 17:10:05", formatter))
                .publishedOn(LocalDateTime.parse("2024-12-31 15:10:05", formatter))
                .location(new Location(37.617698, 55.755864))
                .initiatorId(initiatorId)
                .paid(false)
                .participantLimit(10L)
                .confirmedRequests(0L)
                .availableToParicipants(false)
                .requestModeration(true)
                .state(EventState.PENDING)
                .title("title")
                .views(0L)
                .build();
    }

    public static Event createEventWithView(Long eventId, Long initiatorId, Long categoryId, Long view) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        return Event.builder()
                .id(eventId)
                .annotation("annotation")
                .categoryId(categoryId)
                .description("description")
                .createdOn(LocalDateTime.parse("2024-10-31 17:10:05", formatter))
                .eventDate(LocalDateTime.parse("2024-12-31 17:10:05", formatter))
                .publishedOn(LocalDateTime.parse("2024-12-31 15:10:05", formatter))
                .location(new Location(37.617698, 55.755864))
                .initiatorId(initiatorId)
                .paid(false)
                .participantLimit(10L)
                .confirmedRequests(0L)
                .availableToParicipants(false)
                .requestModeration(true)
                .state(EventState.PENDING)
                .title("title")
                .views(view)
                .build();
    }

    public static UpdateEventAdminRequest createUpdateEventAdminRequestWitnPublishEvent() {
        return UpdateEventAdminRequest.builder().stateAction("PUBLISH_EVENT").build();

    }

    public static EventFullDto createEventFullDto(CategoryDto categoryDto, UserShortDto userShortDto) {

        return EventFullDto.builder()
                .id(1L)
                .annotation("annotation")
                .category(CategoryDto.builder().build())
                .description("description")
                .createdOn("2024-10-31 17:10:05")
                .eventDate("2024-12-31 17:10:05")
                .publishedOn("2024-12-31 15:10:05")
                .location(new Location(37.617698, 55.755864))
                .initiator(UserShortDto.builder().build())
                .paid(false)
                .participantLimit(0L)
                .confirmedRequests(0L)
                .requestModeration(true)
                .state("PENDING")
                .title("title")
                .views(0L)
                .build();

    }

    public static NewCompilationDto createNewCompilationDto() {
        return NewCompilationDto.builder()
                .events(Set.of(1L, 2L))
                .pinned(false)
                .title("title")
                .build();
    }

    public static Compilation createCompilation() {
        return Compilation.builder()
                .id(1L)
                .title("title")
                .pinned(false)
                .events(Collections.emptySet())
                .build();
    }

    public static Category createCategory(Long id) {
        return Category.builder()
                .id(id)
                .name("category")
                .build();
    }

    public static CategoryDto createCategoryDto(Long id, String name) {
        return CategoryDto.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static UserShortDto createUserShortDto(Long id, String name) {
        return UserShortDto.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static EventShortDto createEventShortDto(Long id) {
        return EventShortDto.builder()
                .id(id)
                .title("title")
                .views(0L)
                .initiator(createUserShortDto(1L, "user"))
                .eventDate("2024-12-31 17:10:05")
                .confirmedRequests(0L)
                .annotation("annotation")
                .category(createCategoryDto(1L, "category"))
                .paid(false)
                .build();

    }

    public static Request createRequest(Long requestId, Long requestorId, RequestStatus status) {
        return Request.builder()
                .id(requestId)
                .requesterId(requestorId)
                .status(status)
                .eventId(1L)
                .created(LocalDateTime.now())
                .build();
    }

    public static StatDtoOut createStatDtoOut(Long eventId) {
        return StatDtoOut.builder()
                .app("main-mvc")
                .uri("http://testserver/event/" + eventId)
                .hits(7L)
                .build();
    }

    public static Subscribe createSubscribe() {
        return Subscribe.builder()
                .id(new SubscribeId(1L,2L))
                .build();
    }
}
