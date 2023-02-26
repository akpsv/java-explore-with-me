package ru.akpsv.dto.statclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.akpsv.dto.RequestDtoIn;
import ru.akpsv.dto.StatDtoOut;

import java.util.List;
import java.util.Map;

@Service
public class RestClientService {
    private static final Object API_PREFIX = "/stats";
    private final RestTemplate restTemplate;

    @Autowired
    public RestClientService(@Value("http://localhost:8080") String serverUrl, RestTemplateBuilder builder) {
        restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build();
    }

    public int post(RequestDtoIn requestDtoIn) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<RequestDtoIn> requestDtoInHttpEntity = new HttpEntity<>(requestDtoIn, headers);
        ResponseEntity<Object> response = restTemplate.postForEntity("/hit", requestDtoInHttpEntity, Object.class);
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
