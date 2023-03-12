package ru.akpsv.main.event.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akpsv.TestHelper;
import ru.akpsv.main.category.CategoryRepository;
import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.category.model.Category;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.user.dto.UserShortDto;
import ru.akpsv.main.user.model.User;
import ru.akpsv.main.user.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {
    @Mock
    CategoryRepository stubCategoryRepository;
    @Mock
    UserRepository stubUserRepository;

    @InjectMocks
    EventMapper eventMapper;

    @Test
    void toEvent_EventNewDto_ReturnsEvent() {
        //Подготовка
        NewEventDto newEventDto = TestHelper.createNewEventDto();
        Event expectedEvent = TestHelper.createEvent(1L,1L, 1L);

        //Действия
        Event actualEvent = EventMapper.toEvent(newEventDto, 1L);
        //Проверка
        assertThat(actualEvent, samePropertyValuesAs(expectedEvent, "id","createdOn", "views", "publishedOn", "state", "location"));
    }

    @Test
    void toEventFullDto_Event_ReturnsEventFullDto() {
        //Подготовка
        Event event = TestHelper.createEvent(1L,1L, 1L);
        Mockito.when(stubCategoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(TestHelper.createCategory(1L)));
        User user = TestHelper.createUser(1L, "user@email.ru");
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        CategoryDto categoryDto = TestHelper.createCategoryDto(1L, "category");
        UserShortDto userShortDto = TestHelper.createUserShortDto(1L, "user");
        EventFullDto expectedEventFullDto = TestHelper.createEventFullDto(categoryDto, userShortDto);

        //Действия
        EventFullDto actualEventFullDto = EventMapper.toEventFullDto(event);

        //Проверка
        assertThat(actualEventFullDto, samePropertyValuesAs(expectedEventFullDto, "category", "location", "initiator"));
        assertThat(actualEventFullDto.getCategory(), samePropertyValuesAs(categoryDto));
        assertThat(actualEventFullDto.getInitiator(), samePropertyValuesAs(userShortDto));
    }


    @Test
    void toEventShortDto_Event_ReturnsEventShortDto() {
        //Подготовка
        Event event = TestHelper.createEvent(1L,1L, 1L);
        Category category = TestHelper.createCategory(1L);
        Mockito.when(stubCategoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(category));

        User user = TestHelper.createUser(1L, "user");
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        EventShortDto expectedEventShortDto = TestHelper.createEventShortDto();

        //Действия
        EventShortDto actualEventShortDto = EventMapper.toEventShortDto(event);
        //Проверка
        assertThat(actualEventShortDto, samePropertyValuesAs(expectedEventShortDto, "id", "initiator", "category"));
    }
}