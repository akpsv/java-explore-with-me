package ru.akpsv.dto.statclient;


import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.akpsv.dto.RequestDtoIn;

public class BaseWebClient {
    private WebClient webClient;

    public BaseWebClient(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    public void post(String url, RequestDtoIn requestDtoIn){
//        webClient.post()
//                .uri(url)
//                .body(Mono.just(requestDtoIn), RequestDtoIn.class)
//                .ex()

    }
}
