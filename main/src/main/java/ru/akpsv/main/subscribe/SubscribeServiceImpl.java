package ru.akpsv.main.subscribe;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.service.PrivateEventService;
import ru.akpsv.main.subscribe.dto.SubscribeDtoOut;
import ru.akpsv.main.subscribe.dto.SubscribeMapper;
import ru.akpsv.main.subscribe.model.Subscribe;
import ru.akpsv.main.subscribe.model.SubscribeId;
import ru.akpsv.main.user.UserService;
import ru.akpsv.main.user.dto.UserDto;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {
    private final SubscribeRepository repository;
    private final PrivateEventService eventService;
    private final UserService userService;

    @Override
    public SubscribeDtoOut addSubscriber(Long subscriberId, Long publisherId) {
        Subscribe savedSubscribe = repository.save(
                Subscribe.builder()
                        .id(new SubscribeId(subscriberId, publisherId))
                        .build()
        );
        Function<Subscribe, Boolean> notifyFunc = subscribe -> {
            List<EventShortDto> publisherEvents = eventService.getEventsByUser(savedSubscribe.getId().getPublisherId(), null, null);
            List<UserDto> subscribersByIds = userService.getUsersByIds(List.of(savedSubscribe.getId().getSubscriberId()), null, null);
            return new EmailService().sendEmails(subscribersByIds, publisherEvents);
        };
        notifySubscribers(savedSubscribe, notifyFunc);

        return SubscribeMapper.subscribeDtoOut(savedSubscribe);
    }

//    protected Boolean notifySubscribers(Subscribe subscribe){
//        List<EventShortDto> publisherEvents = eventService.getEventsByUser(subscribe.getId().getPublisherId(), null, null);
//        List<UserDto> subscribersByIds = userService.getUsersByIds(List.of(subscribe.getId().getSubscriberId()), null, null);
//        return new EmailService().sendEmails(subscribersByIds, publisherEvents);
//    }

    protected <T, R> R notifySubscribers(T subscribe, Function<T, R> notifyFunc) {
        return notifyFunc.apply(subscribe);
    }

    @Override
    public List<SubscribeDtoOut> getSubscribes(Long subscriberId) {
        throw new UnsupportedOperationException("Метод не релизован");
    }
}
