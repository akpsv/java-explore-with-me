package ru.akpsv.statsvc;

import ru.akpsv.statdto.EndpointHit;
import ru.akpsv.statdto.StatDtoOut;
import ru.akpsv.statsvc.model.Request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

interface StatsService {
    Request save(EndpointHit endpointHit);

    List<StatDtoOut> getStatDtoByParameters(LocalDateTime startDateTime, LocalDateTime endDateTime, Optional<List<String>> uris, boolean unique);
}
