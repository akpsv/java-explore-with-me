package ru.akpsv.statsvc;

import ru.akpsv.dto.RequestDtoIn;
import ru.akpsv.dto.StatDtoOut;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

interface StatsService {

    void save(RequestDtoIn requestDtoIn);

    Optional<List<StatDtoOut>> getStatDtoByParameters(LocalDateTime startDateTime, LocalDateTime endDateTime, String[] uris, boolean unique);
}
