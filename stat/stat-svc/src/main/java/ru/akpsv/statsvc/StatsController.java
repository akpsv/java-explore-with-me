//package ru.akpsv.statsvc;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ru.akpsv.statdto.EndpointHit;
//import ru.akpsv.statdto.StatDtoOut;
//import ru.akpsv.statsvc.model.Request;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//public class StatsController {
//    private final StatsService statsService;
//
//    /*
//     * Сохранение информации о том, что на uri конкретного сервиса был отправлен запрос пользователем.
//     * Название сервиса, uri и ip пользователя указаны в теле запроса.
//     * */
//    @PostMapping("/hits")
//    public ResponseEntity saveRequestInfo(@RequestBody EndpointHit endpointHit) {
//        log.debug("Получен запрос на эндпоинт /hits. RequestDtoIn= {}", endpointHit);
//        Request savedReqeust = statsService.save(endpointHit);
//        log.debug("Данные сохранены в БД. Сохранённый запрос: {}", savedReqeust);
//        return new ResponseEntity(HttpStatus.valueOf(201));
//    }
//
//    /*
//     * Получение статистики по посещениям.
//     * Обратите внимание: значение даты и времени нужно закодировать
//     * (например используя java.net.URLEncoder.encode)
//     * */
//    @GetMapping("/stats")
//    public List<StatDtoOut> get(@RequestParam String start,
//                                @RequestParam String end,
//                                @RequestParam String[] uris,
//                                @RequestParam(defaultValue = "false") boolean unique) {
//        return statsService
//                .getStatDtoByParameters(decodeParamToLocalDateTime(start), decodeParamToLocalDateTime(end), uris, unique);
////                .orElseThrow(() -> new NoSuchElementException("No such stat elements"));
//    }
//
//    private LocalDateTime decodeParamToLocalDateTime(String dateTime) throws StringOfDateTimeDecodeException {
//        try {
//            String decodedDateTime = URLDecoder.decode(dateTime, "UTF-8");
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            LocalDateTime startDate = LocalDateTime.parse(decodedDateTime, dateTimeFormatter);
//            return startDate;
//        } catch (UnsupportedEncodingException e) {
//            throw new StringOfDateTimeDecodeException("Строка представляющая время не может быть декодирована");
//        }
//    }
//}
//
