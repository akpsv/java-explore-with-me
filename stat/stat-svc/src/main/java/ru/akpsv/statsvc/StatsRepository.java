package ru.akpsv.statsvc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akpsv.statsvc.model.Request;

@Repository
public interface StatsRepository extends JpaRepository<Request, Long> {
}
