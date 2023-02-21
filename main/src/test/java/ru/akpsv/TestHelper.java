package ru.akpsv;

import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.NewEventDto;
import ru.akpsv.main.event.dto.UpdateEventAdminRequest;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.event.model.Location;
import ru.akpsv.main.user.dto.NewUserRequest;
import ru.akpsv.main.user.dto.UserDto;
import ru.akpsv.main.user.dto.UserShortDto;
import ru.akpsv.main.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                .annotation("annotaion")
                .category(1L)
                .description("description")
                .eventDate("2024-12-31 15:10:05")
                .location(new Location(55.755864, 37.617698))
                .paid(true)
                .participantLimit(10)
                .requestModeration(true)
                .title("title")
                .build();
    }

    public static Event createEvent(Long initiatorId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return Event.builder()
                .annotation("annotaion")
                .categoryId(1L)
                .description("description")
                .eventDate(LocalDateTime.parse("2024-12-31 17:10:05", formatter))
                .publishedOn(LocalDateTime.parse("2024-12-31 15:10:05", formatter))
                .location(new Location(37.617698, 55.755864))
                .initiatorId(initiatorId)
                .paid(true)
                .participantLimit(10)
                .requestModeration(true)
                .state(EventState.PENDING)
                .title("title")
                .build();
    }

    public static UpdateEventAdminRequest createUpdateEventAdminRequestWitnPublishEvent() {
        return UpdateEventAdminRequest.builder().stateAction("PUBLISH_EVENT").build();
    }

    public static EventFullDto createEventFullDto() {

        return EventFullDto.builder()
                .annotation("annotaion")
                .category(CategoryDto.builder().build())
                .description("description")
                .eventDate("2024-12-31 17:15:05")
                .publishedOn("2024-12-31 15:10:05")
                .location(new Location(37.617698, 55.755864))
                .initiator(UserShortDto.builder().build())
                .paid(true)
                .participantLimit(10)
                .requestModeration(true)
                .state("PENDING")
                .title("title")
                .build();

    }
}
