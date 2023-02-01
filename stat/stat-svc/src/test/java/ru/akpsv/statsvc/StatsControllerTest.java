package ru.akpsv.statsvc;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.akpsv.TestHelper;
import ru.akpsv.dto.RequestDtoIn;
import ru.akpsv.statsvc.model.Request;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StatsControllerTest {
    @Mock
    private StatsService statsService;

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
        Request request = TestHelper.createRequest(1L);
        RequestDtoIn requestDtoIn = TestHelper.createRequestDtoIn();

        Mockito.when(statsService.save(Mockito.any())).thenReturn(Optional.of(request));

        //Действия
        //Проверка
        mvc.perform(post("/hit")
                .content(mapper.writeValueAsString(requestDtoIn))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void get() {
    }
}