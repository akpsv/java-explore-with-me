package ru.akpsv.main.event;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import ru.akpsv.TestHelper;
import ru.akpsv.main.category.CategoryRepository;
import ru.akpsv.main.category.model.Category;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventMapper;
import ru.akpsv.main.event.dto.NewEventDto;
import ru.akpsv.main.event.dto.UpdateEventAdminRequest;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.user.UserRepository;
import ru.akpsv.main.user.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    EventRepository stubEventRepository;
    @InjectMocks
    EventServiceImpl eventService;
    @Mock
    CategoryRepository stubCategoryRepository;
    @Mock
    UserRepository stubUserRepository;
    @InjectMocks
    EventMapper EventMapper;

    @Test
    void create_NewEventDto_ReturnsEventFullDto() {
        //Подготовка
        NewEventDto newEventDto = TestHelper.createNewEventDto();
        Event event = TestHelper.createEvent(1L);
        Mockito.when(stubEventRepository.save(Mockito.any())).thenReturn(event);
        EventFullDto expectedEventFullDto = TestHelper.createEventFullDto();

        Category category = TestHelper.createCategory();
        Mockito.when(stubCategoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(category));

        User user = TestHelper.createUser(1L, "user@email.ru");
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        //Действия
        EventFullDto actualEventFullDto = eventService.create(1L, newEventDto);
        //Проверка
        assertThat(actualEventFullDto, samePropertyValuesAs(expectedEventFullDto,"category", "initiator","location"));
    }
//
//    @Test
//    void getEventsByUser() {
//    }
//
//    @Test
//    void getEventsByParams() {
//    }

    @Test
    void checkRequestAndFillUpdationFields_UpdateEventAdminRequestWithPublishEvent_ReturnsCorrectEventFullDto() {
        //Подготовка
        UpdateEventAdminRequest requestWitnPublishEvent = TestHelper.createUpdateEventAdminRequestWitnPublishEvent();
        Event updatingEvent = TestHelper.createEvent(1L);

        //Действия
        Event actualUpdatedEvent = eventService.checkAdminRequestAndFillUpdatingFilds(requestWitnPublishEvent, updatingEvent);      //Проверка
        //Проверка
        assertThat(actualUpdatedEvent.getState(), equalTo(EventState.PUBLISHED));
    }
}