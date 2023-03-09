package ru.akpsv.main.event.repository;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.akpsv.TestHelper;
import ru.akpsv.main.category.CategoryRepository;
import ru.akpsv.main.category.model.Category;
import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.dto.EventMapper;
import ru.akpsv.main.event.dto.NewEventDto;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.event.model.Event_;
import ru.akpsv.main.user.model.User;
import ru.akpsv.main.user.repository.UserRepository;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

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
    void save_NewEventDto_ReturnsCorrectEvent() {
        //Подготоска
        User user = TestHelper.createUser(0L, "user@email.ru");
        User savedUser = userRepository.save(user);
        NewEventDto newEventDto = TestHelper.createNewEventDto();

        Event eventAfterMapping = EventMapper.toEvent(newEventDto, savedUser.getId());
        //Действия
        Event actualSavedEvent = eventRepository.save(eventAfterMapping);

        //Проверка
        assertThat(actualSavedEvent.getId(), not(0L));
        assertThat(actualSavedEvent.getCreatedOn(), notNullValue());
    }

    @Test
    void getEventsByUser_UserId_ReturnsEvents() {
        //Подготовка
        User user = TestHelper.createUser(0L, "user@email.ru");
        Long userId = userRepository.save(user).getId();

        Category category = TestHelper.createCategory(0L);
        Long categoryId = categoryRepository.save(category).getId();
        Event event1 = TestHelper.createEvent(userId, categoryId);
        Event event2 = TestHelper.createEvent(userId, categoryId);
        eventRepository.save(event1);
        eventRepository.save(event2);

        EventParams eventParams = new EventParams();
        eventParams.setFrom(0);
        eventParams.setSize(10);

        CriteriaQueryPreparation<Event> request = (params, cb, cq, fromEvent) -> {
            cq.select(fromEvent).where(cb.equal(fromEvent.get(Event_.INITIATOR_ID), userId));
            return cq;
        };

        int expectedSizeOfEventGroup = 2;
        //Действия
        List<Event> actualEventsByUser = eventRepository.getEvents(eventParams, request);
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
        List<Event> actualEventsByAdminParams = eventRepository.getEvents(searchParams, prepareAdminRequest() );
        //Проверка
        assertThat(actualEventsByAdminParams.size(), equalTo(expectedSizeOfEventGroup));
    }
    private CriteriaQueryPreparation<Event> prepareAdminRequest() {
        return (params, cb, cq, fromEvent) -> {
//            EventParams params = eventParams.orElseThrow(() -> new NoSuchElementException("Parameters not passed."));
            List<Predicate> predicates = new ArrayList<>();
            params.getUsers().ifPresent(userIds -> predicates.add(fromEvent.get(Event_.INITIATOR_ID).in(userIds)));
            params.getStates()
                    .ifPresent(groupOfEventStates -> {
                        List<EventState> collectOfEventStates = groupOfEventStates.stream().map(EventState::valueOf).collect(Collectors.toList());
                        predicates.add(fromEvent.get(Event_.STATE).in(collectOfEventStates));
                    });
            params.getCategories().ifPresent(categoryIds -> predicates.add(fromEvent.get(Event_.CATEGORY_ID).in(categoryIds)));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            params.getRangeStart().flatMap(startTimestamp -> params.getRangeEnd().map(endTimestamp ->
                    predicates.add(cb.between(
                            fromEvent.get(Event_.EVENT_DATE),
                            LocalDateTime.parse(startTimestamp, formatter),
                            LocalDateTime.parse(endTimestamp, formatter)))
            ));
            cq.orderBy(cb.desc(fromEvent.get(Event_.EVENT_DATE)));
            cq.select(fromEvent).where(predicates.toArray(Predicate[]::new));
            return cq;
        };
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
        List<Event> actualEventsByAdminParams = eventRepository.getEvents(searchParams, preparePublicRequest());
        //Проверка
        assertThat(actualEventsByAdminParams.size(), equalTo(expectedSizeOfEventGroup));
    }
    private CriteriaQueryPreparation<Event> preparePublicRequest() {
        return (params, cb, cq, fromEvent) -> {
//            EventParams params = eventParams.orElseThrow(() -> new NoSuchElementException("Parameters not passed."));
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(fromEvent.get(Event_.STATE), EventState.PUBLISHED));
            params.getText().ifPresent(text -> predicates.add(cb.or(
                            cb.like(cb.lower(fromEvent.get(Event_.ANNOTATION)), ("%" + text + "%").toLowerCase()),
                            cb.like(cb.lower(fromEvent.get(Event_.DESCRIPTION)), ("%" + text + "%").toLowerCase())
                    )
            ));
            params.getCategories().ifPresent(categoryIds -> predicates.add(fromEvent.get(Event_.CATEGORY_ID).in(categoryIds)));
            params.getPaid().ifPresent(paid -> predicates.add(cb.equal(fromEvent.get(Event_.PAID), paid)));
            params.getOnlyAvailable().ifPresent(available -> predicates.add(cb.equal(fromEvent.get(Event_.AVAILABLE_TO_PARICIPANTS), available)));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            params.getRangeStart()
                    .flatMap(startTimestamp -> params.getRangeEnd().map(endTimestamp ->
                            predicates.add(cb.between(
                                    fromEvent.get(Event_.EVENT_DATE),
                                    LocalDateTime.parse(startTimestamp, formatter),
                                    LocalDateTime.parse(endTimestamp, formatter)))
                    ))
                    .orElseGet(() -> predicates.add(cb.greaterThan(fromEvent.get(Event_.EVENT_DATE), LocalDateTime.now())));

            params.getSort().ifPresent(sort -> {
                if ("EVENT_DATE".equals(sort.toUpperCase())) cq.orderBy(cb.desc(fromEvent.get(Event_.EVENT_DATE)));
                else cq.orderBy(cb.desc(fromEvent.get(Event_.VIEWS)));
            });
            cq.select(fromEvent).where(predicates.toArray(Predicate[]::new));
            return cq;
        };
    }

}