package ru.akpsv.main.event;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.Event_;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    default List<Event> getEventsByUser(EntityManager em, Long userId, Integer from, Integer size){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> fromEvent = cq.from(Event.class);
        cq.select(fromEvent).where(cb.equal( fromEvent.get(Event_.INITIATOR_ID), userId));
        TypedQuery<Event> query = em.createQuery(cq);
        query.setFirstResult(from).setMaxResults(size);
        List<Event> resultList = query.getResultList();
        return resultList;
    }
}
