package ru.akpsv.main.subscribe;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.event.repository.EventRepository;
import ru.akpsv.main.subscribe.dto.SubscribeDtoIn;
import ru.akpsv.main.subscribe.dto.SubscribeDtoOut;
import ru.akpsv.main.subscribe.dto.SubscribeMapper;
import ru.akpsv.main.subscribe.model.Subscribe;
import ru.akpsv.main.subscribe.repository.SubscribeRepository;
import ru.akpsv.main.user.model.User;
import ru.akpsv.main.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {
    private final SubscribeRepository subscribeRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    /**
     * Добавить подписку на пользователя
     *
     * @param subscribeDtoIn
     * @return
     */
    @Override
    public SubscribeDtoOut addSubscribe(SubscribeDtoIn subscribeDtoIn) {
        Subscribe savedSubscribe = subscribeRepository.save(
                Subscribe.builder()
                        .subscriberId(subscribeDtoIn.getSubscriberId())
                        .publisherId(subscribeDtoIn.getPublisherId())
                        .build()
        );

        Function<Subscribe, Boolean> notifyFunc = subscribe -> {
            List<Event> publishedEventsOfPublisher = eventRepository.findByInitiatorIdAndState(
                    savedSubscribe.getPublisherId(),
                    EventState.PUBLISHED);

            if (publishedEventsOfPublisher.size()==0){
                return false;
            }
            User subscriber = userRepository.findById(subscribeDtoIn.getSubscriberId())
                    .orElseThrow(() -> new NoSuchElementException("User with id=" + subscribeDtoIn.getSubscriberId() + "hot exist"));

            emailService.sendEmails(subscriber, publishedEventsOfPublisher);
            return true;
        };

        Boolean notifyResult = notifySubscribers(savedSubscribe, notifyFunc);

        return SubscribeMapper.toSubscribeDtoOut(savedSubscribe);
    }

    /**
     * Уведомить подписчиков о событии
     *
     * @param subscribe  - подписка, содержащая идентификаторы подписчик и издателя
     * @param notifyFunc - функция, которая реализует уведомление
     * @param <T>        - подписка
     * @param <R>        - возвращаемое значение типа Boolean
     * @return - возвращет тип Boolean
     */
    public <T, R> R notifySubscribers(T subscribe, Function<T, R> notifyFunc) {
        return notifyFunc.apply(subscribe);
    }

    /**
     * Получить подписки в которых учствует подписчик
     *
     * @param subscriberId - идентификтор подписчик
     * @return - вернуть список подписок
     */
    @Override
    public List<SubscribeDtoOut> getSubscribesOfSubscriber(Long subscriberId) {
        return subscribeRepository.findBySubscriberId(subscriberId)
                .stream()
                .map(SubscribeMapper::toSubscribeDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSubscribe(Long subscribeId) {
        subscribeRepository.deleteById(subscribeId);
    }

    /**
     * Получить подписки в которых участвует конкретный издатель
     *
     * @param publisherId - идентификатор издателя
     * @return - вернуть список подписок подписчиков подписанных на издателя
     */
    @Override
    public List<SubscribeDtoOut> getSubscribesOfPublisher(Long publisherId) {
        return subscribeRepository.findByPublisherId(publisherId)
                .stream()
                .map(SubscribeMapper::toSubscribeDtoOut)
                .collect(Collectors.toList());
    }
}
