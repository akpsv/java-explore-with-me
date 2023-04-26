package ru.akpsv.statsvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ru.akpsv.statdto.EndpointHit;
import ru.akpsv.statdto.StatDtoOut;
import ru.akpsv.statsvc.model.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
//@RequiredArgsConstructor
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
    public Flux<StatDtoOut> get(@RequestParam String start,
                                @RequestParam String end,
                                @RequestParam String[] uris,
                                @RequestParam(defaultValue = "false") boolean unique) {
        log.debug("Вызвана конечная точка /stats с параметрами: " +
                "start={}, end={}, uris[] = {} , unique= {}", start, end, uris, unique);
        List<StatDtoOut> statDtoByParameters = statsService.getStatDtoByParameters(
                decodeParamToLocalDateTime(start), decodeParamToLocalDateTime(end), uris, unique);
        return Flux.fromIterable(statDtoByParameters);
    }

    private LocalDateTime decodeParamToLocalDateTime(String dateTime) throws StringOfDateTimeDecodeException {
        try {
            String decodedDateTime = URLDecoder.decode(dateTime, "UTF-8");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDate = LocalDateTime.parse(decodedDateTime, dateTimeFormatter);
            return startDate;
        } catch (UnsupportedEncodingException e) {
            throw new StringOfDateTimeDecodeException("Строка представляющая время не может быть декодирована");
        }
    }
}
