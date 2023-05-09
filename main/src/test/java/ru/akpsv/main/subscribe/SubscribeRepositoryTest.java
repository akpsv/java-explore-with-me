package ru.akpsv.main.subscribe;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.akpsv.TestHelper;
import ru.akpsv.main.subscribe.model.Subscribe;
import ru.akpsv.main.subscribe.repository.SubscribeRepository;

import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
class SubscribeRepositoryTest {
    @Autowired
    private SubscribeRepository subscribeRepository;

    @Test
    void save_SubscribeWithoutId_ReturnsSubscribeWithId() {
        //Подготовка
        Subscribe subscribe = TestHelper.createSubscribe();
        Subscribe expectedSubscribe = TestHelper.createSubscribe();
        //Действия
        Subscribe actualSubscribe = subscribeRepository.save(subscribe);
        //Проверка
        assertThat(actualSubscribe, Matchers.samePropertyValuesAs(expectedSubscribe));
    }
}
