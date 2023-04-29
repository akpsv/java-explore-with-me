package ru.akpsv.statclient;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.akpsv.statdto.EndpointHit;
import ru.akpsv.statdto.StatDtoOut;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class WebFluxClientService {
    private WebClient webClient;

    public WebFluxClientService(String serverUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .build();
    }

    public Mono<Integer> saveHit(Mono<EndpointHit> endpointHit) {
        return webClient
                .post()
                .uri("/hit")
                .body(endpointHit, EndpointHit.class)
                .exchangeToMono(clientResponse -> Mono.just(clientResponse.rawStatusCode()));
    }

    public List<StatDtoOut> getStats(String startTimestamp, String endTimestamp, List<String> uris, Boolean uniqueValue) {
        List<StatDtoOut> statDtoOutList = new ArrayList<>();
        webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", startTimestamp)
                        .queryParam("end", endTimestamp)
                        .queryParam("uris", uris)
                        .queryParam("unique", uniqueValue)
                        .build())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> Mono.just(new UnsupportedEncodingException("Строка представляющая время не может быть декодирована")))
                .bodyToMono(ResponseEntity.class)
                .map(responseEntity -> (ResponseEntity<List<StatDtoOut>>) responseEntity)
                .subscribe(responseEntity -> statDtoOutList.addAll(responseEntity.getBody()));

        return statDtoOutList;
    }
}
