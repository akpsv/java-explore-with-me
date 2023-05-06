package ru.akpsv.statsvc;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.akpsv.TestHelper;
import ru.akpsv.statsvc.model.Request;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DataJpaTest
class StatsRepositoryTest {
    private final StatsRepository statsRepository;

    @Test
    void save_RequestWithoutId_ReutrnsRequestWithId() {
        //Подготовка
        Request request = TestHelper.createRequest(0L, "http://test.server.ru/endpoint", "192.168.1.1");
        //Действия
        long actualRequestId = statsRepository.save(request).getId();
        //Проверка
        assertNotEquals(0, actualRequestId);

    }

    @PersistenceContext
    EntityManager entityManager;

    @ParameterizedTest
    @CsvSource({"false, 3", "true, 2"})
    void getByParameters_IsIpInuque_ReturnsCorrectHitsIntoStatDtoOut(String isIpUnique, String expectedQuantity) {
        //Подготовка
        boolean uniqueIp = Boolean.parseBoolean(isIpUnique);

        long expectedQuantityOfHits = Long.parseLong(expectedQuantity);
        Request request1 = TestHelper.createRequest(0L, "http://test.server.ru/endpoint1", "192.168.1.1");
        Request request2 = TestHelper.createRequest(0L, "http://test.server.ru/endpoint1", "192.168.1.1");
        Request request3 = TestHelper.createRequest(0L, "http://test.server.ru/endpoint1", "192.168.1.2");
        statsRepository.save(request1);
        statsRepository.save(request2);
        statsRepository.save(request3);
        List<String> arrayOfUri = List.of(request1.getUri(), request3.getUri());

        //Действия
        Map<Request, Long> resultStatDtoOuts = statsRepository.getStatDtoByParameters(
                LocalDateTime.now().minusHours(1L),
                LocalDateTime.now().plusHours(1L),
                Optional.of(arrayOfUri),
                uniqueIp);

        //Проверка
        org.hamcrest.MatcherAssert.assertThat(resultStatDtoOuts.entrySet().iterator().next().getValue(), equalTo(expectedQuantityOfHits));
    }
}
