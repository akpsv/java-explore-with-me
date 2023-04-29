package ru.akpsv.statsvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.akpsv.statdto.EndpointHit;
import ru.akpsv.statdto.StatDtoOut;
import ru.akpsv.statsvc.model.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class StatsWebFluxController {
    private final StatsService statsService;

    @Autowired
    public StatsWebFluxController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    public ResponseEntity saveHit(@RequestBody EndpointHit endpointHit) {
        log.debug("Получен запрос на эндпоинт /hit. EndpointHit= {}", endpointHit);
        Request savedReqeust = statsService.save(endpointHit);
        log.debug("Данные сохранены в БД. Сохранённый запрос: {}", savedReqeust);
        return new ResponseEntity(HttpStatus.valueOf(201));
    }

    /*
     * Получение статистики по посещениям.
     * Обратите внимание: значение даты и времени нужно закодировать
     * (например используя java.net.URLEncoder.encode)
     * */
    @GetMapping(value = "/stats")
    public Mono<ResponseEntity<?>> get(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam Optional<List<String>> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        log.debug("Вызвана конечная точка /stats с параметрами: " +
                "start={}, end={}, uris[] = {} , unique= {}", start, end, uris, unique);
        try {
            LocalDateTime startDateTime = decodeParamToLocalDateTime(start);
            LocalDateTime endDateTime = decodeParamToLocalDateTime(end);

            List<StatDtoOut> statDtoByParameters = statsService.getStatDtoByParameters(startDateTime, endDateTime, uris, unique);
            Mono<ResponseEntity<?>> just = Mono.just(new ResponseEntity<>(statDtoByParameters, HttpStatus.OK));
            return just;
        } catch (DateTimeParseException e) {
            return Mono.just(new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST));
        } catch (UnsupportedEncodingException e) {
            return Mono.just(new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST));
        }
    }

    private LocalDateTime decodeParamToLocalDateTime(String dateTime) throws StringOfDateTimeDecodeException, UnsupportedEncodingException {
        String decodedDateTime = URLDecoder.decode(dateTime, "UTF-8");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(decodedDateTime, dateTimeFormatter);
        return startDate;
    }
}
