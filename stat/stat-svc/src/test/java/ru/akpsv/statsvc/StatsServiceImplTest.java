package ru.akpsv.statsvc;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.akpsv.TestHelper;
import ru.akpsv.statdto.EndpointHit;
import ru.akpsv.statsvc.model.Request;

class StatsServiceImplTest {

    @Test
    void save_WhenCall_CallsSaveFromStatsRepository() {
        //Подготовка
        Request request = TestHelper.createRequest(0L, "http://test.server.ru/endpoint", "192.168.1.1");
        EndpointHit endpointHit = TestHelper.createRequestDtoIn();
        Request savedRequest = TestHelper.createRequest(1L, "http://test.server.ru/endpoint", "192.168.1.1");

        StatsRepository mockStatsRepository = Mockito.mock(StatsRepository.class);
        Mockito.when(mockStatsRepository.save(request)).thenReturn(savedRequest);

        StatsServiceImpl statsService = new StatsServiceImpl(mockStatsRepository);
        //Действия
        statsService.save(endpointHit);
        //Проверка
        Mockito.verify(mockStatsRepository, Mockito.times(1))
                .save(Mockito.any());
    }


    @Test
    void getStatDtoByParameters() {
    }
}
