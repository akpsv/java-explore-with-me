package ru.akpsv.main.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService{
    private final CompilationRepository compilationRepository;
    @Override
    public Optional<Compilation> create(Compilation compilation) {
        return Optional.empty();
    }

    @Override
    public Optional<List<Compilation>> update(long compId) {
        return Optional.empty();
    }

    @Override
    public void deleteById(long compId) {

    }
}
