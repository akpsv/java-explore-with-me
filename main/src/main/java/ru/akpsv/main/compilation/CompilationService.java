package ru.akpsv.main.compilation;

import ru.akpsv.main.compilation.dto.CompilationDto;
import ru.akpsv.main.compilation.dto.NewCompilationDto;
import ru.akpsv.main.compilation.dto.UpdateCompilationRequest;

import java.util.List;
import java.util.Optional;

public interface CompilationService {
    CompilationDto create(NewCompilationDto compilation);

    Optional<List<Compilation>> update(long compId);

    void deleteById(Long compId);

    CompilationDto getCompilationById(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updatingRequest);
}
