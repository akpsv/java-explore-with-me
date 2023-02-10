package ru.akpsv.statsvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.dto.StatDtoOut;
import ru.akpsv.statsvc.model.Request;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<Request> save(Request request) {
        return Optional.of(statsRepository.save(request));
    }

    @Override
    public Optional<List<StatDtoOut>> getStatDtoByParameters(LocalDateTime startDateTime, LocalDateTime endDateTime, String[] uris, boolean unique) {
        return statsRepository.getStatDtoByParameters(entityManager, startDateTime, endDateTime, uris, unique);
    }
}
