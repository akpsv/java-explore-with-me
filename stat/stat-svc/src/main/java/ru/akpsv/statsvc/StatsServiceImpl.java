package ru.akpsv.statsvc;

import org.springframework.stereotype.Service;
import ru.akpsv.statdto.EndpointHit;
import ru.akpsv.statdto.StatDtoOut;
import ru.akpsv.statsvc.model.Request;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public Request save(EndpointHit endpointHit) {
        return statsRepository.save(RequestMapper.toRequest(endpointHit));
    }

    @Override
    public List<StatDtoOut> getStatDtoByParameters(LocalDateTime startDateTime, LocalDateTime endDateTime, String[] uris, boolean unique) {
        Map<Request, Long> requestsByParameters = statsRepository.getStatDtoByParameters(startDateTime, endDateTime, uris, unique);
        List<StatDtoOut> statDtoOuts = requestsByParameters.entrySet().stream()
                .map(requestLongEntry -> StatDtoOut.builder()
                        .app(requestLongEntry.getKey().getApp())
                        .uri(requestLongEntry.getKey().getUri())
                        .hits(requestLongEntry.getValue())
                        .build())
                .sorted(Comparator.comparingLong(StatDtoOut::getHits).reversed())
                .collect(Collectors.toList());
        return statDtoOuts;
    }
}
