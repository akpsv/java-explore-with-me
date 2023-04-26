package ru.akpsv.statclient;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.akpsv.statdto.EndpointHit;
import ru.akpsv.statdto.StatDtoOut;

import java.util.List;

@Service
public class WebFluxClientService {
    private final WebClient webClient;

    public WebFluxClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:9090")
                .build();
    }

    public Mono<Integer> saveHit(Mono<EndpointHit> endpointHit) {
        return webClient
                .post()
                .uri("/hit")
                .body(endpointHit, EndpointHit.class)
                .exchangeToMono(clientResponse -> Mono.just(clientResponse.rawStatusCode()));
    }

    public Flux<StatDtoOut> getStats(String startTimestamp, String endTimestamp, List<String> uris, Boolean uniqueValue) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", startTimestamp)
                        .queryParam("end", endTimestamp)
                        .queryParam("uris", uris)
                        .queryParam("unique", uniqueValue)
                        .build())
                .exchangeToFlux(clientResponse -> clientResponse.bodyToFlux(StatDtoOut.class));
    }
}
