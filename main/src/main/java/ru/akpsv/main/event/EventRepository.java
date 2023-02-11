package ru.akpsv.main.event;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.akpsv.main.event.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
