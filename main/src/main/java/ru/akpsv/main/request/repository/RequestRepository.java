package ru.akpsv.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akpsv.main.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>, RequestRepositoryAdvanced {
    List<Request> getRequestsByRequesterId(Long userId);

    List<Request> getRequestsByEventId(Long eventId);
}
