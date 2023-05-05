package ru.akpsv.main.event.repository;

import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.Event_;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class EventRepositoryAdvancedImpl implements EventRepositoryAdvanced {
    @PersistenceContext
    private EntityManager em;

    public List<Event> getEvents(EventParams params, CriteriaQueryPreparation<Event> queryPreparation) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> fromEvent = cq.from(Event.class);

        CriteriaQuery<Event> resultCQ = queryPreparation.prepare(params, cb, cq, fromEvent);

        TypedQuery<Event> query = em.createQuery(resultCQ);
        query.setFirstResult(params.getFrom()).setMaxResults(params.getSize());
        return query.getResultList();
    }

    @Override
    public Event getFullEventInfoByUser(Long userId, Long eventId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> fromEvent = cq.from(Event.class);
        Predicate user = cb.equal(fromEvent.get(Event_.INITIATOR_ID), userId);
        Predicate event = cb.equal(fromEvent.get(Event_.ID), eventId);
        cq.select(fromEvent).where(user, event);
        TypedQuery<Event> query = em.createQuery(cq);
        return query.getSingleResult();
    }
}
