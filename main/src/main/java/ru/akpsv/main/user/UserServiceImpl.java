package ru.akpsv.main.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.main.user.dto.NewUserRequest;
import ru.akpsv.main.user.dto.UserDto;
import ru.akpsv.main.user.dto.UserMapper;
import ru.akpsv.main.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids, Integer from, Integer size) {
        return userRepository.getUsersByIds(ids, from, size).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(NewUserRequest newUser) {
        return Stream.of(newUser)
                .map(UserMapper::toUser)
                .map(userRepository::save)
                .map(UserMapper::toUserDto)
                .findFirst()
                .get();
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
