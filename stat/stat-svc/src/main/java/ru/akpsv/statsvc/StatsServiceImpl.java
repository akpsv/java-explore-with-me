package ru.akpsv.statsvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.statdto.RequestDtoIn;
import ru.akpsv.statdto.StatDtoOut;
import ru.akpsv.statsvc.model.Request;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Request save(RequestDtoIn requestDtoIn) {
        return  statsRepository.save(RequestMapper.toRequest(requestDtoIn));
    }

    @Override
    public Optional<List<StatDtoOut>> getStatDtoByParameters(LocalDateTime startDateTime, LocalDateTime endDateTime, String[] uris, boolean unique) {
        Optional<Map<Request, Long>> requestsByParameters = statsRepository.getStatDtoByParameters(entityManager, startDateTime, endDateTime, uris, unique);
        List<StatDtoOut> statDtoOuts = requestsByParameters.get().entrySet().stream()
                .map(requestLongEntry -> StatDtoOut.builder()
                        .app(requestLongEntry.getKey().getApp())
                        .uri(requestLongEntry.getKey().getUri())
                        .hits(requestLongEntry.getValue())
                        .build())
                .sorted(Comparator.comparingLong(StatDtoOut::getHits).reversed())
                .collect(Collectors.toList());
        return Optional.of(statDtoOuts);
    }
}
