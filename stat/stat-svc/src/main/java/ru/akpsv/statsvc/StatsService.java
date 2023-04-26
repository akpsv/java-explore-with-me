package ru.akpsv.statsvc;

import ru.akpsv.statdto.EndpointHit;
import ru.akpsv.statdto.StatDtoOut;
import ru.akpsv.statsvc.model.Request;

import java.time.LocalDateTime;
import java.util.List;

interface StatsService {

    Request save(EndpointHit endpointHit);

    List<StatDtoOut> getStatDtoByParameters(LocalDateTime startDateTime, LocalDateTime endDateTime, String[] uris, boolean unique);
}
