package ru.akpsv.main.subscribe;

import ru.akpsv.main.subscribe.dto.SubscribeDtoOut;
import ru.akpsv.main.subscribe.model.Subscribe;

import java.util.List;

public interface SubscribeService {
    SubscribeDtoOut addSubscriber(Long subscriberId, Long publisherId);

    List<SubscribeDtoOut> getSubscribes(Long subscriberId);
}
