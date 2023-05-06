package ru.akpsv.main.user.repository;

import ru.akpsv.main.user.model.User;

import java.util.List;

public interface UserRepositoryAdvanced {
    List<User> getUsersByIds(List<Long> ids, Integer from, Integer size);
}
