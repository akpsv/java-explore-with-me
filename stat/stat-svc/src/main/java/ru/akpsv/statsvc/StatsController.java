package ru.akpsv.statsvc;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.dto.RequestDtoIn;
import ru.akpsv.dto.StatDtoOut;

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
    public StatDtoOut get(@RequestParam String start,
                          @RequestParam String end,
                          @RequestParam String[] uris,
                          @RequestParam boolean unique) {
        return null;
    }

}
