package ru.akpsv.main.subscribe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akpsv.TestHelper;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.service.PrivateEventService;
import ru.akpsv.main.subscribe.model.Subscribe;
import ru.akpsv.main.user.UserService;
import ru.akpsv.main.user.dto.UserDto;

import java.util.List;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
class SubscribeServiceImplTest {
    @Mock
    private SubscribeRepository repository;
    @Mock
    private PrivateEventService eventService;
    @Mock
    private UserService userService;
    @InjectMocks
    private SubscribeServiceImpl subscribeService;

    @Test
    void notifySubscribers_UsersAndEvents_ReturnsTrue() {
        //Подготовка
        Subscribe savedSubscribe = TestHelper.createSubscribe();
        Function<Subscribe, Boolean> notifyFunc = subscribe -> {
            List<EventShortDto> publisherEvents = eventService.getEventsByUser(savedSubscribe.getId().getPublisherId(), null, null);
            List<UserDto> subscribersByIds = userService.getUsersByIds(List.of(savedSubscribe.getId().getSubscriberId()), null, null);
            return new EmailService().sendEmails(subscribersByIds, publisherEvents);
        };

        //Действия
        Boolean actualResult = subscribeService.notifySubscribers(savedSubscribe, notifyFunc);

        //Проверка
        assertThat(actualResult, equalTo(true));
    }
}
