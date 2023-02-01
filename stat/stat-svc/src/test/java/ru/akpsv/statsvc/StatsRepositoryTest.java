package ru.akpsv.statsvc;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.akpsv.TestHelper;
import ru.akpsv.statsvc.model.Request;

import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DataJpaTest
class StatsRepositoryTest {
    private final StatsRepository statsRepository;

    @Test
    void save_RequestWithoutId_ReutrnsRequestWithId() {
        //Подготовка
        Request request = TestHelper.createRequest(0L);
        //Действия
        long actualRequestId = statsRepository.save(request).getId();
        //Проверка
        assertNotEquals(0, actualRequestId);

    }
}