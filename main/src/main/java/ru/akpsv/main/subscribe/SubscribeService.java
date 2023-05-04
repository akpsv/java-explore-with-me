package ru.akpsv.main.subscribe;

import ru.akpsv.main.subscribe.model.Subscribe;

import java.util.List;

public interface SubscribeService {
    Subscribe addSubscriber(Long subscriberId, Long publisherId);

    List<Subscribe> getSubscribes(Long subscriberId);
}
