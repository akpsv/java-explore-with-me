package ru.akpsv.main.user;

import ru.akpsv.main.user.dto.NewUserRequest;
import ru.akpsv.main.user.dto.UserDto;
import ru.akpsv.main.user.model.User;

import java.util.List;
import java.util.NoSuchElementException;

public interface UserService {
    UserDto create(NewUserRequest newUser);

    List<UserDto> getUsersByIds(List<Long> ids, Integer from, Integer size);

    void deleteById(Long userId) throws NoSuchElementException;
}
