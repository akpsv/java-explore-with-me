package ru.akpsv.main.event.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.akpsv.TestHelper;
import ru.akpsv.main.category.CategoryRepository;
import ru.akpsv.main.category.model.Category;
import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.user.model.User;
import ru.akpsv.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class EventRepositoryTest {
    @Autowired
    EventRepository eventRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        eventRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        categoryRepository.deleteAll();
        categoryRepository.flush();
    }

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
        assertThat(actualEventsByUser.size(), equalTo(expectedSizeOfEventGroup));
    }

    @Test
    void getEventsByAdminParams_SearchParams_ReturnsEvents() {
        //Подготовка
        User user1 = TestHelper.createUser(0L, "user1@email.ru");
        User savedUser1 = userRepository.save(user1);
        User user2 = TestHelper.createUser(0L, "user2@email.ru");
        User savedUser2 = userRepository.save(user2);

        Category category1 = TestHelper.createCategory(0L);
        Category category2 = TestHelper.createCategory(0L);
        Category savedCategory1 = categoryRepository.save(category1);
        Category savedCategory2 = categoryRepository.save(category2);

        Event event1 = TestHelper.createEvent(savedUser2.getId(), savedCategory2.getId());
        Event event2 = TestHelper.createEvent(savedUser1.getId(), savedCategory2.getId());
        eventRepository.save(event1);
        eventRepository.save(event2);

        EventParams searchParams = new EventParams();
        searchParams.setUsers(Optional.of(List.of(savedUser2.getId())));
        searchParams.setCategories(Optional.of(List.of(savedCategory2.getId())));
        searchParams.setFrom(0);
        searchParams.setSize(10);

        int expectedSizeOfEventGroup = 1;
        //Действия
        List<Event> actualEventsByAdminParams = eventRepository.getEventsByAdminParams(searchParams);
        List<Event> all = eventRepository.findAll();
        //Проверка
        assertThat(actualEventsByAdminParams.size(), equalTo(expectedSizeOfEventGroup));
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
        assertThat(actualEventsByAdminParams.size(), equalTo(expectedSizeOfEventGroup));
    }
}