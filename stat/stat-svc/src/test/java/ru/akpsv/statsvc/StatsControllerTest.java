package ru.akpsv.statsvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.akpsv.TestHelper;
import ru.akpsv.dto.RequestDtoIn;
import ru.akpsv.dto.StatDtoOut;
import ru.akpsv.statsvc.model.Request;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {
    @Mock
    private StatsService stubStatsService;

    @InjectMocks
    private StatsController statsController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(statsController)
                .build();
    }

    @Test
    void saveRequestInfo_RequestDtoIn_ReturnsHttpCode201() throws Exception {
        //Подготовка
        Request request = TestHelper.createRequest(1L, "http://test.server.ru/endpoint", "192.168.1.1");
        RequestDtoIn requestDtoIn = TestHelper.createRequestDtoIn();

        when(stubStatsService.save(Mockito.any())).thenReturn(Optional.of(request));

        //Действия
        //Проверка
        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(requestDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void get_WhenCall_ReturnsListOfStatDtoOut() throws Exception {
        //Подготовка
        StatDtoOut statDtoOut = TestHelper.createStatDtoOut("http://test.server.ru/endpoint", 3);
        List<StatDtoOut> statDtoOuts = new ArrayList<>();
        statDtoOuts.add(statDtoOut);
        String[] uris = {"http://test.server.ru/endpoint"};

        when(stubStatsService.getStatDtoByParameters(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(Optional.of(statDtoOuts));

        //Действия
        //Проверка
        mvc.perform(get("/stats")
                        .param("start", "2022-02-05 11:00:23")
                        .param("end", "2022-03-05 11:00:23")
                        .param("uris", uris)
                        .param("unique", "false"))
                .andExpect(status().isOk());
    }
}