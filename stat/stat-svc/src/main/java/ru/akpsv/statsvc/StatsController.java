package ru.akpsv.statsvc;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.dto.RequestDtoIn;
import ru.akpsv.dto.StatDtoOut;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class StatsController {
    private final StatsService statsService;

    /*
     * Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
     * Название сервиса, uri и ip пользователя указаны в теле запроса.
     * */
    @PostMapping("/hit")
    public ResponseEntity saveRequestInfo(@RequestBody RequestDtoIn requestDtoIn) {
        statsService.save(requestDtoIn);
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
