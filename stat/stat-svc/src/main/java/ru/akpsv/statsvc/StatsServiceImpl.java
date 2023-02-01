package ru.akpsv.statsvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.statsvc.model.Request;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService{
    private final StatsRepository statsRepository;
    @Override
    public Optional<Request> save(Request request) {
        return Optional.of(statsRepository.save(request));
    }
}
