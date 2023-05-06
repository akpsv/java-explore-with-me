package ru.akpsv.main.compilation.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akpsv.main.compilation.Compilation;
import ru.akpsv.main.event.dto.EventMapper;
import ru.akpsv.main.event.dto.EventShortDto;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.repository.EventRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompilationMapper {
    private static EventRepository eventRepository;

    @Autowired
    private CompilationMapper(EventRepository eventRepository) {
        CompilationMapper.eventRepository = eventRepository;
    }


    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        Set<Event> eventsByIds = eventRepository.findAllById(newCompilationDto.getEvents()).stream()
                .collect(Collectors.toSet());
        return Compilation.builder()
                .events(eventsByIds)
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .events(eventShortDtos)
                .build();
    }
}
