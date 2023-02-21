package ru.akpsv.main.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akpsv.main.request.model.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
}
