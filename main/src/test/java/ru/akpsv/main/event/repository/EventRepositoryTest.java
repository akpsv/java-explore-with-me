package ru.akpsv.main.event.repository;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.akpsv.TestHelper;
import ru.akpsv.main.category.CategoryRepository;
import ru.akpsv.main.category.model.Category;
import ru.akpsv.main.event.EventParamsForAdmin;
import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.user.model.User;
import ru.akpsv.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class EventRepositoryTest {
    @Autowired
    EventRepository eventRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void getEventsByUser_UserId_ReturnsEvents() {
        //Подготовка
        User user = TestHelper.createUser(1L, "user@email.ru");
        userRepository.save(user);
        Category category = TestHelper.createCategory(1L);
        categoryRepository.save(category);
        Event event1 = TestHelper.createEvent(1L, 1L);
        Event event2 = TestHelper.createEvent(1L, 1L);
        eventRepository.save(event1);
        eventRepository.save(event2);
        int expectedSizeOfEventGroup = 2;
        //Действия
        List<Event> actualEventsByUser = eventRepository.getEventsByUser(1L, 0, 10);
        //Проверка
        org.hamcrest.MatcherAssert.assertThat(actualEventsByUser.size(), Matchers.equalTo(expectedSizeOfEventGroup));
    }

    @Test
    void getEventsByAdminParams_SearchParams_ReturnsEvents() {
        //Подготовка
        User user1 = TestHelper.createUser(1L, "user@email.ru");
        User savedUser1 = userRepository.save(user1);
        User user2 = TestHelper.createUser(2L, "user@email.ru");
        User savedUser2 = userRepository.save(user2);

        Category category1 = TestHelper.createCategory(1L);
        Category category2 = TestHelper.createCategory(2L);
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        Event event1 = TestHelper.createEvent(savedUser2.getId(), 2L);
        Event event2 = TestHelper.createEvent(savedUser1.getId(), 2L);
        eventRepository.save(event1);
        eventRepository.save(event2);

        EventParamsForAdmin searchParams = new EventParamsForAdmin();
        searchParams.setUsers(Optional.of(List.of(1L)));
        searchParams.setCategories(Optional.of(List.of(2L)));
        searchParams.setFrom(0);
        searchParams.setSize(10);

        int expectedSizeOfEventGroup = 1;
        //Действия
        List<Event> actualEventsByAdminParams = eventRepository.getEventsByAdminParams(searchParams);
        //Проверка
        org.hamcrest.MatcherAssert.assertThat(actualEventsByAdminParams.size(), Matchers.equalTo(expectedSizeOfEventGroup));
    }

    @Test
    void getEventsByPublicParams_CategoryAndDateTimeRange_ReturnsEvent() {
        //Подготовка
        User user = TestHelper.createUser(1L, "user@email.ru");
        userRepository.save(user);

        Category category1 = TestHelper.createCategory(1L);
        Category category2 = TestHelper.createCategory(2L);
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        Event event1 = TestHelper.createEvent(1L, 1L);
        eventRepository.save(event1);
        Event event2 = TestHelper.createEvent(1L, 2L);
        event2.setState(EventState.PUBLISHED);
        eventRepository.save(event2);
        Event event3 = TestHelper.createEvent(1L, 2L);
        event3.setState(EventState.PUBLISHED);
        event3.setEventDate(LocalDateTime.now().plusHours(2));
        eventRepository.save(event3);

        EventParams searchParams = new EventParams();
        searchParams.setCategories(Optional.of(List.of(2L)));
        searchParams.setRangeStart(Optional.of("2024-01-01 00:00:00"));
        searchParams.setRangeEnd(Optional.of("2025-01-01 00:00:00"));
        searchParams.setFrom(0);
        searchParams.setSize(10);

        int expectedSizeOfEventGroup = 1;
        //Действия
        List<Event> actualEventsByAdminParams = eventRepository.getEventsByPublicParams(searchParams);
        //Проверка
        org.hamcrest.MatcherAssert.assertThat(actualEventsByAdminParams.size(), Matchers.equalTo(expectedSizeOfEventGroup));
    }

    @Test
    void getFullEventInfoByUser() {
    }

    @Test
    void getEventByInitiatorIdAndId() {
    }
}