package ru.akpsv.statclient;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.akpsv.statdto.EndpointHit;
import ru.akpsv.statdto.StatDtoOut;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
class WebFluxClientServiceTest {
    private WebFluxClientService webFluxClientService;

    @BeforeEach
    void setUp() {
        webFluxClientService = new WebFluxClientService(WebClient.builder());
    }

    @Test
    void saveHit_PassEndpointHit_ReturnsHttpStatusCode201() {
        //Подготовка
        EndpointHit endpointHit = TestHelper.createEndpointHit();
        //Действия
        Mono<Integer> integerMono = webFluxClientService.saveHit(Mono.just(endpointHit));
        //Проверка
        int actualHttpStatusCode = integerMono.block();
        org.hamcrest.MatcherAssert.assertThat(actualHttpStatusCode, Matchers.equalTo(201));
    }

    @Test
    void getStats_PassParams_ReturnsGroupOfStatDtoOut() {
        //Подготовка
        String startTime = "2022-02-05 11:00:23";
        String endTime = "2024-03-05 11:00:23";
        List<String> uris = List.of("http://test.server.ru/endpoint");
        Boolean uniqueValue = false;
        Long expectedNumberOfStatDto = 1L;

        //Действия
        Flux<StatDtoOut> statDtoOutFlux = webFluxClientService.getStats(startTime, endTime, uris, uniqueValue);
        Long actualNumberOfStatDto = statDtoOutFlux.count().block();
        //Проверка
        assertThat(actualNumberOfStatDto, Matchers.equalTo(expectedNumberOfStatDto));
    }
}
