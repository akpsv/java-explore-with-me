package ru.akpsv.statsvc;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.akpsv.TestHelper;
import ru.akpsv.statsvc.model.Request;

class StatsServiceImplTest {

    @Test
    void save_WhenCall_CallsSaveFromStatsRepository() {
        //Подготовка
        Request request = TestHelper.createRequest(0L);
        Request savedRequest = TestHelper.createRequest(1L);

        StatsRepository mockStatsRepository = Mockito.mock(StatsRepository.class);
        Mockito.when(mockStatsRepository.save(request)).thenReturn(savedRequest);

        StatsServiceImpl statsService = new StatsServiceImpl(mockStatsRepository);
        //Действия
        statsService.save(request);
        //Проверка
        Mockito.verify(mockStatsRepository, Mockito.times(1))
                .save(Mockito.any());

    }
}