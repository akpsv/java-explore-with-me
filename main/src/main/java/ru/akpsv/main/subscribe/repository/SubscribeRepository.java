package ru.akpsv.main.subscribe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akpsv.main.subscribe.model.Subscribe;

import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    List<Subscribe> findByPublisherId(Long publisherId);

    List<Subscribe> findBySubscriberId(Long subscriberId);
}
