package ru.akpsv.dto.main.compilation.dto;

import ru.akpsv.dto.main.event.Event;
import ru.akpsv.dto.main.event.dto.EventShortDto;

import java.util.List;

public class CompilationDto {
    private long id;
    private String title;
    private boolean pinned;
    private List<EventShortDto> events;
}
