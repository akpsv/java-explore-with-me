package ru.akpsv.main.event;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.akpsv.TestHelper;
import ru.akpsv.main.event.dto.EventMapper;
import ru.akpsv.main.event.dto.NewEventDto;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.user.UserRepository;
import ru.akpsv.main.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventRepositoryTest {
    @Autowired
    EventRepository eventRepository;
    @Autowired
    UserRepository userRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    void save_NewEventDto_ReturnsCorrectEvent() {
        //Подготоска
        User user = TestHelper.createUser(0L, "user@email.ru");
        User savedUser = userRepository.save(user);
        NewEventDto newEventDto = TestHelper.createNewEventDto();
        EventMapper eventMapper = new EventMapper();

        Event eventAfterMapping = eventMapper.toEvent(newEventDto, savedUser.getId());
        //Действия
        Event actualSavedEvent = eventRepository.save(eventAfterMapping);

        //Проверка
        org.hamcrest.MatcherAssert.assertThat(actualSavedEvent.getId(), Matchers.not(0L));
        org.hamcrest.MatcherAssert.assertThat(actualSavedEvent.getCreatedOn(), notNullValue());
    }

    @Test
    void getEventsByUser_UserId_ReturnsCorrectSizeEvents() {
        //Подготоска
        User user = TestHelper.createUser(0L, "user@email.ru");
        User savedUser = userRepository.save(user);

        NewEventDto newEventDto = TestHelper.createNewEventDto();
        NewEventDto newEventDto2 = TestHelper.createNewEventDto();

        EventMapper eventMapper = new EventMapper();
        Event eventAfterMapping = eventMapper.toEvent(newEventDto, savedUser.getId());
        Event eventAfterMapping2 = eventMapper.toEvent(newEventDto2, savedUser.getId());

        Event actualSavedEvent = eventRepository.save(eventAfterMapping);
        Event actualSavedEvent2 = eventRepository.save(eventAfterMapping2);

        int expectedSize = 2;
        //Действия
        List<Event> listEventsByUser = eventRepository.getEventsByUser(em, savedUser.getId(), 0, 10);
        int actualSize = listEventsByUser.size();

        //Проверка
        assertThat(actualSize, equalTo(expectedSize));
    }
}