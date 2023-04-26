package ru.akpsv.statclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.akpsv.statdto.EndpointHit;
import ru.akpsv.statdto.StatDtoOut;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RestClientService {
    //    private static final Object API_PREFIX = "/stats";
    private final RestTemplate restTemplate;
    private final String serverUrl;

    public RestClientService(String serverUrl, RestTemplateBuilder builder) {
        this.serverUrl = serverUrl;
        restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/hits"))
                .build();
    }

    public int post(EndpointHit endpointHit) {
        log.info("Вызов метода post() с параметром типа RequestDtoIn: {}", endpointHit);
        log.debug("Создать объект HttpHeaders");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        log.debug("Создание объекта типа HttpEntity<RequestDtoIn>");
        HttpEntity<EndpointHit> requestDtoInHttpEntity = new HttpEntity<>(endpointHit, headers);
        log.debug("Отправить запрос. RestTemplate.getUriTemplateHandler: {} ", restTemplate.getUriTemplateHandler().expand("/"));
        ResponseEntity<Object> response = null;
        try {
            response = restTemplate.postForEntity("", requestDtoInHttpEntity, Object.class);
        } catch (RestClientException ex) {
            log.debug("Исключение при вызове postForEntity. Сообщение={}, Причина={}, Стэк={}", ex.getMessage(), ex.getCause(), ex.getStackTrace());
        }
        log.debug("Получен ответ. StatusCode = {}, Body = {}", response.getStatusCodeValue(), response.getBody());
        return response.getStatusCodeValue();
    }

    public List<StatDtoOut> getStatDtos(Map<String, ?> parameters) {
        ResponseEntity<List> entityList = restTemplate.getForEntity(
                "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                List.class,
                parameters);
        List<StatDtoOut> statDtoOuts = entityList.getBody();
        return statDtoOuts;
    }
}
