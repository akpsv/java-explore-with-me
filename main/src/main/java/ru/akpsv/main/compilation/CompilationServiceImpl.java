package ru.akpsv.main.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.akpsv.main.compilation.dto.CompilationDto;
import ru.akpsv.main.compilation.dto.CompilationMapper;
import ru.akpsv.main.compilation.dto.NewCompilationDto;
import ru.akpsv.main.compilation.dto.UpdateCompilationRequest;
import ru.akpsv.main.event.EventRepository;
import ru.akpsv.main.event.model.Event;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    @PersistenceContext
    private EntityManager em;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        Compilation savedCompilation = compilationRepository.save(compilation);
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(savedCompilation);
        return compilationDto;
    }

    @Override
    public Optional<List<Compilation>> update(long compId) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        return compilationRepository.findById(compId)
                .map(CompilationMapper::toCompilationDto)
                .orElseThrow(() -> new NoSuchElementException("Compilation with id=" + compId + " was not found"));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<CompilationDto> collect = compilationRepository.getCompilations(em, pinned, from, size).stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updatingRequest) {
        return compilationRepository.findById(compId)
                .map(compilation -> {
                    Compilation checkedCompilation = checkAndFillCompilationFields(updatingRequest, compilation);
                    Compilation savedCompilation = compilationRepository.save(checkedCompilation);
                    return savedCompilation;
                })
                .map(CompilationMapper::toCompilationDto)
                .orElseThrow(() -> new NoSuchElementException("Compilation with id=" + compId + " was not found"));
    }

    protected Compilation checkAndFillCompilationFields(UpdateCompilationRequest updatingRequest, Compilation compilation) {

        if (updatingRequest.getEvents() != null) {
            Set<Event> events = updatingRequest.getEvents().stream()
                    .map(eventRepository::findById)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toSet());

            compilation.setEvents(events);
        }
        if (updatingRequest.getPinned() != null) {
            compilation.setPinned(updatingRequest.getPinned());
        }
        if (updatingRequest.getTitle() != null) {
            compilation.setTitle(updatingRequest.getTitle());
        }
        return compilation;
    }
}
