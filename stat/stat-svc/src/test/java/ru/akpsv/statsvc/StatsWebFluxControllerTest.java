package ru.akpsv.statsvc;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.akpsv.TestHelper;
import ru.akpsv.statdto.EndpointHit;
import ru.akpsv.statdto.StatDtoOut;
import ru.akpsv.statsvc.model.Request;

import java.util.ArrayList;
import java.util.List;

//@ExtendWith(MockitoExtension.class)
class StatsWebFluxControllerTest {

//    @Mock
//    private StatsService stubStatsService;
//
//    @InjectMocks
//    private StatsWebFluxController statsController;

    @Test
    void get_WhenCall_ReturnsListOfStatDtoOut() {
        //Подготовка
        StatDtoOut statDtoOut = TestHelper.createStatDtoOut("http://test.server.ru/endpoint", 3);
        List<StatDtoOut> statDtoOuts = new ArrayList<>();
        statDtoOuts.add(statDtoOut);
        String[] uris = {"http://test.server.ru/endpoint"};

        StatsService stubStatsService = Mockito.mock(StatsServiceImpl.class);
        Mockito
                .when(stubStatsService.getStatDtoByParameters(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(statDtoOuts);

        StatsWebFluxController statsWebFluxController = new StatsWebFluxController(stubStatsService);
        WebTestClient testClient = WebTestClient.bindToController(
                statsWebFluxController)
                .build();

        //Действия
        //Проверка
        testClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/stats")
                                .queryParam("start", "2022-02-05 11:00:23")
                                .queryParam("end", "2022-03-05 11:00:23")
                                .queryParam("uris", uris)
                                .queryParam("unique", "false")
                                .build()
                )
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void saveHit() {
        //Подготовка
        Request request = TestHelper.createRequest(1L, "http://test.server.ru/endpoint", "192.168.1.1");
        EndpointHit endpointHit = TestHelper.createRequestDtoIn();

        //Действия
        //Проверка
        StatsService stubStatsService = Mockito.mock(StatsService.class);
        Mockito.when(stubStatsService.save(Mockito.any())).thenReturn(request);

        WebTestClient webTestClient = WebTestClient.bindToController(
                new StatsWebFluxController(stubStatsService))
                .build();

        webTestClient
                .post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(endpointHit))
                .exchange()
                .expectStatus().isCreated();
    }
}
