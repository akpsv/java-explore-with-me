package ru.akpsv.dto.main.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    @Override
    public Optional<List<User>> get() {
        return Optional.of(userRepository.findAll());
    }

    @Override
    public Optional<User> create(User user) {
        return Optional.of(userRepository.save(user));
    }

    @Override
    public void deleteById(int userId) {
        userRepository.deleteById(Long.valueOf(userId));
    }
}
