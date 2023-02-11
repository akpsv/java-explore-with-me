package ru.akpsv.main.user;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<List<User>> get();
    Optional<User> create(User user);
    void deleteById(int userId);
}
