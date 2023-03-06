package ru.akpsv.main.event.repository;

import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.model.Event;

import java.util.List;

public interface EventRepositoryAdvanced {
    Event getFullEventInfoByUser(Long userId, Long eventId);

    List<Event> getEventsByPublicParams(EventParams params);

    List<Event> getEventsByAdminParams(EventParams params);

    List<Event> getEventsByUser(Long userId, Integer from, Integer size);
}
