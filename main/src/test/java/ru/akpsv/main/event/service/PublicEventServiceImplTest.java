package ru.akpsv.main.event.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.akpsv.TestHelper;
import ru.akpsv.main.category.CategoryRepository;
import ru.akpsv.main.category.model.Category;
import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.EventMapper;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.repository.EventRepository;
import ru.akpsv.main.user.model.User;
import ru.akpsv.main.user.repository.UserRepository;
import ru.akpsv.statclient.WebFluxClientService;
import ru.akpsv.statdto.StatDtoOut;

import java.util.List;
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
        //Подготовка
        EventParams eventParams = new EventParams();

        //Действия

        //Проверка
    }

    @Test
    void createGroupOfUris_GroupOfEventShortDtos_GroupOfUris() {
        //Подготовка
        EventShortDto eventShortDto1 = TestHelper.createEventShortDto(1L);
        EventShortDto eventShortDto2 = TestHelper.createEventShortDto(2L);
        int expectedNumberOfUris = 2;

        //Действия
        List<String> groupOfUris = publicEventService.createGroupOfUris(List.of(eventShortDto1, eventShortDto2));
        int actualNumberOfUris = groupOfUris.size();

        //Проверка
        assertThat(actualNumberOfUris, equalTo(expectedNumberOfUris));
        assertThat(groupOfUris.get(0), equalTo("/event/1"));
        assertThat(groupOfUris.get(1), equalTo("/event/2"));
    }

    @Test
    void getStatDtoOutsFromStatSvc() {
        //Подготовка
        EventParams eventParams = EventParams.builder().onlyAvailable(Optional.of(false)).build();

        EventShortDto eventShortDto1 = TestHelper.createEventShortDto(1L);
        Flux<EventShortDto> sortedGroupOfEventShortDtos = Flux.just(eventShortDto1);

        StatDtoOut statDtoOut1 = TestHelper.createStatDtoOut(1L);
        WebFluxClientService stubWebClient = Mockito.mock(WebFluxClientService.class);
        Mockito.when(stubWebClient.getStats(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(Flux.just(statDtoOut1));

        Event event1 = TestHelper.createEvent(1L, 1L, 1L);
        EventRepository stubEventRepository = Mockito.mock(EventRepository.class);
        PublicEventServiceImpl publicEventService = new PublicEventServiceImpl(stubEventRepository);

        //Действия
        Flux<StatDtoOut> statDtoOutFlux = publicEventService
                .getStatDtoOutsFromStatSvc(eventParams, sortedGroupOfEventShortDtos, stubWebClient);

        //Проверка
//        StepVerifier.create(eventShortDtoFlux)
//                .expectSubscription()
//                .expectNextMatches(statDtoOut -> statDtoOut.getHits())
//                .expectComplete()
//                .verify();
    }


    @Test
    void addViewsToEventShortDtos() {
        //Подготовка
        EventShortDto eventShortDto1 = TestHelper.createEventShortDto(1L);
        Flux<EventShortDto> eventShortDtoFlux = Flux.just(eventShortDto1);

        StatDtoOut statDtoOut1 = TestHelper.createStatDtoOut(1L);
        Flux<StatDtoOut> statDtoOutFlux = Flux.just(statDtoOut1);

        EventRepository stubEventRepository = Mockito.mock(EventRepository.class);
        PublicEventServiceImpl publicEventService = new PublicEventServiceImpl(stubEventRepository);

        //Действия
        Flux<EventShortDto> resultEventShortDtoFlux = publicEventService.addViewsToEventShortDtos(eventShortDtoFlux, statDtoOutFlux);
        //Проверка
        StepVerifier.create(resultEventShortDtoFlux)
                .expectSubscription()
                .expectNextMatches(eventShortDto -> eventShortDto.getViews().equals(7L))
                .expectComplete()
                .verify();
    }
}
