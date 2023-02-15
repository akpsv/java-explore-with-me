package ru.akpsv;

import ru.akpsv.main.event.dto.NewEventDto;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.Location;
import ru.akpsv.main.user.dto.NewUserRequest;
import ru.akpsv.main.user.dto.UserDto;
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
                .eventDate(LocalDateTime.parse("2024-12-31 15:10:05", formatter))
                .locationLatitude(55.755864)
                .locationLongitude(37.617698)
                .initiatorId(initiatorId)
                .paid(true)
                .participantLimit(10)
                .requestModeration(true)
                .title("title")
                .build();
    }
}
