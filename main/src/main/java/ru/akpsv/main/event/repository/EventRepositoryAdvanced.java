package ru.akpsv.main.event.repository;

import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepositoryAdvanced {
    Event getFullEventInfoByUser(Long userId, Long eventId);

    List<Event> getEvents(EventParams params, CriteriaQueryPreparation<Event> queryPreparation) ;

    }
