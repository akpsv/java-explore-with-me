package ru.akpsv.statsvc;

import ru.akpsv.dto.StatDtoOut;
import ru.akpsv.statsvc.model.Request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

interface StatsService {

    Optional<Request> save(Request request);

    Optional<List<StatDtoOut>> getStatDtoByParameters(LocalDateTime startDateTime, LocalDateTime endDateTime, String[] uris, boolean unique);
}
