package ru.akpsv.statsvc;

import ru.akpsv.statsvc.model.Request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StatsRepositoryAdvanced {
    Map<Request, Long> getStatDtoByParameters(LocalDateTime startDateTime,
                                              LocalDateTime endDateTime,
                                              Optional<List<String>> uris, boolean unique);
}
