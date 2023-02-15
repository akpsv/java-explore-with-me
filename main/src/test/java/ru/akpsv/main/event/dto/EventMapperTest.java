package ru.akpsv.main.event.dto;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akpsv.TestHelper;
import ru.akpsv.main.category.CategoryRepository;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.user.UserRepository;

import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    EventMapper eventMapper;

    @Test
    void toEvent_EventNewDto_ReturnsCorrectEvent() {
        //Подготовка
        NewEventDto newEventDto = TestHelper.createNewEventDto();
        Event expectedEvent = TestHelper.createEvent(1L);

        //Действия
        Event actualEvent = eventMapper.toEvent(newEventDto, 1L);
        //Проверка
        assertThat(actualEvent, samePropertyValuesAs(expectedEvent));
    }

    @Test
    void toEventFullDto() {
    }
}