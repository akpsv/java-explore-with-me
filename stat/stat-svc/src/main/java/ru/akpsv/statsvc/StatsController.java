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
        statsService.save(RequestMapper.toRequest(requestDtoIn)).get();
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
                                @RequestParam boolean unique) throws UnsupportedEncodingException {
        LocalDateTime startDateTime = decodeParamToLocalDateTime(start);
        LocalDateTime endDateTime = decodeParamToLocalDateTime(end);

        Optional<List<StatDtoOut>> statDtoByParameters = statsService.getStatDtoByParameters(startDateTime, endDateTime, uris, unique);
        return statDtoByParameters.get();
    }

    private LocalDateTime decodeParamToLocalDateTime(String start) throws UnsupportedEncodingException {
        String startDateTime = URLDecoder.decode(start, "UTF-8");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(startDateTime,dateTimeFormatter);
        return startDate;
    }

}
