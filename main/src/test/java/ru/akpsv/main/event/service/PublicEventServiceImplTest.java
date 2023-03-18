package ru.akpsv.main.event.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akpsv.TestHelper;
import ru.akpsv.main.category.CategoryRepository;
import ru.akpsv.main.category.model.Category;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventMapper;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.repository.EventRepository;
import ru.akpsv.main.user.model.User;
import ru.akpsv.main.user.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
class PublicEventServiceImplTest {

    @Mock
    EventRepository stubEventRepository;
    @InjectMocks
    PublicEventServiceImpl publicEventService;

    @Mock
    CategoryRepository stubCategoryRepository;
    @Mock
    UserRepository stubUserRepository;
    @InjectMocks
    EventMapper EventMapper;

    @Test
    void registerViewAndGetEventFullDto_EventId_ReturnsViewHasOne() {
        //Подготовка
        Category category = TestHelper.createCategory(1L);
        Mockito.when(stubCategoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(category));

        User user = TestHelper.createUser(1L, "user@email.ru");
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        Event eventBeforeView = TestHelper.createEvent(1L, 1L, 1L);
        Mockito.when(stubEventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(eventBeforeView));

        Mockito.when(stubEventRepository.save(Mockito.any())).thenAnswer(invocationOnMock -> {
            return invocationOnMock.getArgument(0, Event.class);
        });

        Long expectedNumberOfViews = 1L;
        //Действия
        EventFullDto actualEventFullDto = publicEventService.registerViewAndGetEventFullDto(1L);
        //Проверка
        assertThat(actualEventFullDto.getViews(), equalTo(expectedNumberOfViews));
    }

    @Test
    void getEventsByPublicParams() {
    }
}