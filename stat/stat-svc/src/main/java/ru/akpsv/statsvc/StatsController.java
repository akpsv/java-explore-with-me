package ru.akpsv.statsvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.statdto.RequestDtoIn;
import ru.akpsv.statdto.StatDtoOut;
import ru.akpsv.statsvc.model.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    /*
     * Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
     * Название сервиса, uri и ip пользователя указаны в теле запроса.
     * */
    @PostMapping("/hits")
    public ResponseEntity saveRequestInfo(@RequestBody RequestDtoIn requestDtoIn) {
        log.debug("Получен запрос на эндпоинт /hits. RequestDtoIn= {}", requestDtoIn);
        Request savedReqeust = statsService.save(requestDtoIn);
        log.debug("Данные сохранены в БД. Сохранённый запрос: {}", savedReqeust);
        return new ResponseEntity(HttpStatus.valueOf(201));
    }

    /*
     * Получение статистики по посещениям.
     * Обратите внимание: значение даты и времени нужно закодировать
     * (например используя java.net.URLEncoder.encode)
     * */
    @GetMapping("/stats")
    public List<StatDtoOut> get(@RequestParam String start,
                                @RequestParam String end,
                                @RequestParam String[] uris,
                                @RequestParam(defaultValue = "false") boolean unique) {
        LocalDateTime startDateTime = decodeParamToLocalDateTime(start);
        LocalDateTime endDateTime = decodeParamToLocalDateTime(end);
        Optional<List<StatDtoOut>> statDtoByParameters = statsService.getStatDtoByParameters(startDateTime, endDateTime, uris, unique);
        return statDtoByParameters.get();
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

