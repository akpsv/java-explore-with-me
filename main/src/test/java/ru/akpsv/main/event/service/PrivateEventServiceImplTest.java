package ru.akpsv.main.event.service;

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
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.dto.NewEventDto;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.repository.EventRepository;
import ru.akpsv.main.user.dto.UserShortDto;
import ru.akpsv.main.user.model.User;
import ru.akpsv.main.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;

@ExtendWith(MockitoExtension.class)
class PrivateEventServiceImplTest {

    @Mock
    EventRepository stubEventRepository;
    @InjectMocks
    PrivateEventServiceImpl privateEventService;
    @Mock
    CategoryRepository stubCategoryRepository;
    @Mock
    UserRepository stubUserRepository;

    @InjectMocks
    ru.akpsv.main.event.dto.EventMapper EventMapper;

    @Test
    void create_NewEventDto_ReturnsEventFullDto() {
        //Подготовка
        NewEventDto newEventDto = TestHelper.createNewEventDto();
        Event event = TestHelper.createEvent(1L, 1L);
        Mockito.when(stubEventRepository.save(Mockito.any())).thenReturn(event);
        CategoryDto categoryDto = TestHelper.createCategoryDto(1L, "category");
        UserShortDto userShortDto = TestHelper.createUserShortDto(1L, "user");
        EventFullDto expectedEventFullDto = TestHelper.createEventFullDto(categoryDto, userShortDto);

        Category category = TestHelper.createCategory(1L);
        Mockito.when(stubCategoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(category));

        User user = TestHelper.createUser(1L, "user@email.ru");
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        //Действия
        EventFullDto actualEventFullDto = privateEventService.create(1L, newEventDto);
        //Проверка
        assertThat(actualEventFullDto, samePropertyValuesAs(expectedEventFullDto, "category", "initiator", "location"));
    }

    @Test
    void getEventsByUser_UserId_ReturnsEventsByUser() {
        //Подготовка
        Event event = TestHelper.createEvent(1L, 1L);
        Mockito.when(stubEventRepository.getEvents(Mockito.any(), Mockito.any())).thenReturn(List.of(event));

        Category category = TestHelper.createCategory(1L);
        Mockito.when(stubCategoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(category));

        User user = TestHelper.createUser(1L, "user@email.ru");
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        int expectedSizeOfGroup = 1;
        //Действия
        List<EventShortDto> acutalEventsByUser = privateEventService.getEventsByUser(1L, 0, 10);
        //Проверка
        assertThat(acutalEventsByUser.size(), equalTo(expectedSizeOfGroup));
    }

}