package ru.akpsv.main.compilation;

import java.util.List;
import java.util.Optional;

public interface CompilationService {
    Optional<Compilation> create(Compilation compilation);
    Optional<List<Compilation>> update(long compId);
    void deleteById(long compId);
}
