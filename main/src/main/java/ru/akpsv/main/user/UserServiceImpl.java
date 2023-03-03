package ru.akpsv.main.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.main.user.dto.NewUserRequest;
import ru.akpsv.main.user.dto.UserDto;
import ru.akpsv.main.user.dto.UserMapper;
import ru.akpsv.main.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @PersistenceContext
    EntityManager em;

    @Override
    public List<User> getUsersByIds(List<Long> ids, Integer from, Integer size) {
        return userRepository.getUsersByIds(em, ids, from, size);
    }

    @Override
    public UserDto create(NewUserRequest newUser) {
        return Stream.of(newUser)
                .map(UserMapper::toUser)
                .map(userRepository::save)
                .map(UserMapper::toUserDto)
                .findFirst().get();
    }

    @Override
    public void deleteById(Long userId) throws NoSuchElementException {
        userRepository.findById(userId)
                .ifPresentOrElse(user -> userRepository.deleteById(userId),
                        () -> {
                            throw new NoSuchElementException("User with id=" + userId + " was not found");
                        });
    }
}
