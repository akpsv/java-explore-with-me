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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @PersistenceContext
    EntityManager em;

    @Override
    public Optional<List<User>> getUsersByIds(Long[] ids, Integer from, Integer size) {
        return Optional.of(userRepository.getUsersByIds(em, ids, from, size));
    }

    @Override
    public Optional<UserDto> create(NewUserRequest newUser) {
        User user = UserMapper.toUser(newUser);
        User savedUser = userRepository.save(user);
        UserDto userDto = UserMapper.toUserDto(savedUser);
        return Optional.of(userDto);
    }

    @Override
    public void deleteById(Long userId) throws NoSuchElementException {
        userRepository.findById(userId)
                .ifPresentOrElse(user -> userRepository.deleteById(userId),
                        () -> {
                            String exceptionMessage = "User with id=" + userId + " was not found";
                            throw new NoSuchElementException(exceptionMessage);
                        });
    }
}
