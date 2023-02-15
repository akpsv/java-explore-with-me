package ru.akpsv.main.user;

import ru.akpsv.main.user.dto.NewUserRequest;
import ru.akpsv.main.user.dto.UserDto;
import ru.akpsv.main.user.model.User;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface UserService {
    Optional<List<User>> getUsersByIds(Long[] ids, Integer from, Integer size);
    Optional<UserDto> create(NewUserRequest newUser);
    void deleteById(Long userId) throws NoSuchElementException;
}
