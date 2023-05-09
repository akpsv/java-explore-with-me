package ru.akpsv.main.event.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryAdvanced {
    Optional<Event> getEventByInitiatorIdAndId(Long userId, Long eventId);

    List<Event> findByInitiatorIdAndState(Long userId, EventState state);
}
