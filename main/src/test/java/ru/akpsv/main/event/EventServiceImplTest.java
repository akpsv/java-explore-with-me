package ru.akpsv.main.event;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akpsv.TestHelper;
import ru.akpsv.main.event.dto.EventMapper;
import ru.akpsv.main.event.dto.UpdateEventAdminRequest;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    EventRepository stubEventRepository;
    @Mock
    EventMapper stubEventMapper;
    @InjectMocks
    EventServiceImpl eventService;

//    @Test
//    void create() {
//    }
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
        Event actualUpdatedEvent = eventService.checkAdminRequestAndFillUpdatingFilds(requestWitnPublishEvent, updatingEvent) ;      //Проверка
        //Проверка
        org.hamcrest.MatcherAssert.assertThat(actualUpdatedEvent.getState(), Matchers.equalTo(EventState.PUBLISHED));
    }
}