package ru.akpsv.statsvc;

import ru.akpsv.statdto.RequestDtoIn;
import ru.akpsv.statdto.StatDtoOut;
import ru.akpsv.statsvc.model.Request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

interface StatsService {

    Request save(RequestDtoIn requestDtoIn);

    Optional<List<StatDtoOut>> getStatDtoByParameters(LocalDateTime startDateTime, LocalDateTime endDateTime, String[] uris, boolean unique);
}
