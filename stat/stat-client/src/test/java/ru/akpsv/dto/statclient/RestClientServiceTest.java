package ru.akpsv.dto.statclient;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class RestClientServiceTest {

    private final RestClientService restClientService;

//    @Test
//    void getStatDtos_WhenCall_ReturnsListStatDtoOut() {
//        //Подготовка
//        RequestDtoIn requestDtoIn = TestHelper.createRequestDtoIn();
//        String[] uris = {"http://test.server.ru/endpoint"};
//
//        long expectedListSize = 1;
////        Map<String, Object> requestParameters = new HashMap<>();
////        requestParameters.put("start", "2022-02-05 11:00:23");
////        requestParameters.put("end", "2022-03-05 11:00:23");
////        requestParameters.put("uris", uris);
////        requestParameters.put("unique", "false");
//        String startTime = "2022-02-05 11:00:23";
//        String endTime = "2024-03-05 11:00:23";
//        String uniqueValue = "false";
//        Map<String, ?> requestParameters = Map.of(
//                "start", startTime,
//                "end", endTime,
//                "uris", uris,
//                "unique", uniqueValue
//        );
//        //Действия
//
//        long actualListSize = restClientService.getStatDtos(requestParameters).size();
//        //Проверка
//        assertThat(actualListSize, Matchers.equalTo(expectedListSize));
//
//    }
}