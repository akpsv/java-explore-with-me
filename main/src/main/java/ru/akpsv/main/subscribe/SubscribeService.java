package ru.akpsv.main.subscribe;

import ru.akpsv.main.subscribe.dto.SubscribeDtoIn;
import ru.akpsv.main.subscribe.dto.SubscribeDtoOut;

import java.util.List;
import java.util.function.Function;

public interface SubscribeService {
    SubscribeDtoOut addSubscribe(SubscribeDtoIn subscribeDtoIn);

    void deleteSubscribe(Long subscribeId);

    List<SubscribeDtoOut> getSubscribesOfPublisher(Long publisherId);

    <T, R> R notifySubscribers(T subscribe, Function<T, R> notifyFunc);

    List<SubscribeDtoOut> getSubscribesOfSubscriber(Long subscriberId);
}
