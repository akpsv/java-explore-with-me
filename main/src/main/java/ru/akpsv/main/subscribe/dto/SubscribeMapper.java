package ru.akpsv.main.subscribe.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akpsv.main.subscribe.model.Subscribe;
import ru.akpsv.main.user.model.User;
import ru.akpsv.main.user.repository.UserRepository;

import java.util.List;

@Service
public class SubscribeMapper {
    private static UserRepository userRepository;

    @Autowired
    private SubscribeMapper(UserRepository userRepository) {
        SubscribeMapper.userRepository = userRepository;
    }

    public static SubscribeDtoOut toSubscribeDtoOut(Subscribe subscribe) {
        List<User> users = userRepository.findAllById(
                List.of(subscribe.getPublisherId(), subscribe.getSubscriberId()));

        return SubscribeDtoOut.builder()
                .subscribeId(subscribe.getSubscribeId())
                .publisherId(subscribe.getPublisherId())
                .publisher(new SubscribeDtoOut.UserDtoOut(
                        users.get(0).getName(),
                        users.get(0).getEmail()))
                .subscriber(new SubscribeDtoOut.UserDtoOut(
                        users.get(1).getName(),
                        users.get(1).getEmail()))
                .build();
    }
}
