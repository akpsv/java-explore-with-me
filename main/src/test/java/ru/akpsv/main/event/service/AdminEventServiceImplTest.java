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
import ru.akpsv.main.event.dto.UpdateEventAdminRequest;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.event.repository.EventRepository;
import ru.akpsv.main.user.model.User;
import ru.akpsv.main.user.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
class AdminEventServiceImplTest {
    @Mock
    EventRepository stubEventRepository;
    @InjectMocks
    AdminEventServiceImpl adminEventService;
    @Mock
    CategoryRepository stubCategoryRepository;
    @Mock
    UserRepository stubUserRepository;

    @InjectMocks
    EventMapper eventMapper;

    @Test
    void checkAdminRequestAndFillUpdatingFilds_UpdateEventAdminRequestWithPublishEvent_ReturnsCorrectEventFullDto() {
        //Подготовка
        UpdateEventAdminRequest requestWitnPublishEvent = TestHelper.createUpdateEventAdminRequestWitnPublishEvent();
        Event updatingEvent = TestHelper.createEvent(1L, 1L, 1L);
//        EventRepository stubEventRepository = Mockito.mock(EventRepository.class);
//        AdminEventServiceImpl adminEventService = new AdminEventServiceImpl(stubEventRepository);

        //Действия
        Event actualUpdatedEvent = adminEventService.checkAdminRequestAndFillUpdatingFilds(requestWitnPublishEvent, updatingEvent);      //Проверка
        //Проверка
        assertThat(actualUpdatedEvent.getState(), equalTo(EventState.PUBLISHED));
    }

    @Test
    void updateEventByAdmin_UpdatingEventIdAndUpdatingRequest_ReturnsEventFullDto() {
        //Подготовка
        Event updatingEvent = TestHelper.createEvent(1L, 1L, 1L);
        Event expectedChangedEvent = TestHelper.createEvent(1L, 1L, 1L).toBuilder().annotation("changed annotation").build();
        Mockito.when(stubEventRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(updatingEvent));
        Mockito.when(stubEventRepository.save(Mockito.any())).thenReturn(expectedChangedEvent);

        Category category = TestHelper.createCategory(1L);
        Mockito.when(stubCategoryRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(category));
        User user = TestHelper.createUser(1L, "user@email.ru");
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        UpdateEventAdminRequest updatingRequest = UpdateEventAdminRequest.builder().annotation("changed annotation").build();

        //Действия
        EventFullDto actualEventFullDto = adminEventService.updateEventByAdmin(updatingRequest, 1L);
        //Проверка
        assertThat(actualEventFullDto.getAnnotation(), equalTo(expectedChangedEvent.getAnnotation()));
    }
}