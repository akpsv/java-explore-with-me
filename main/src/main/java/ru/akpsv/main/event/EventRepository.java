package ru.akpsv.main.event;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.event.model.Event_;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    default List<Event> getEventsByUser(EntityManager em, Long userId, Integer from, Integer size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> fromEvent = cq.from(Event.class);
        cq.select(fromEvent).where(cb.equal(fromEvent.get(Event_.INITIATOR_ID), userId));
        TypedQuery<Event> query = em.createQuery(cq);
        query.setFirstResult(from).setMaxResults(size);
        List<Event> resultList = query.getResultList();
        return resultList;
    }

    default List<Event> getEventsByAdminParams(EntityManager em, EventParamsForAdmin params) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> fromEvent = cq.from(Event.class);

        Predicate users = fromEvent.get(Event_.INITIATOR_ID).in(params.getUsers());

        List<EventState> collectOfStates = Arrays.stream(params.getStates())
                .map(state -> EventState.valueOf(state))
                .collect(Collectors.toList());
        Predicate states = fromEvent.get(Event_.STATE).in(collectOfStates);

        Predicate categories = fromEvent.get(Event_.CATEGORY_ID).in(params.getCategories());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(params.getRangeStart(), formatter);
        LocalDateTime end = LocalDateTime.parse(params.getRangeEnd(), formatter);
        Predicate rangeOfDateTime = cb.between(fromEvent.get(Event_.eventDate), start, end);

        cq.where(users, states, categories, rangeOfDateTime);
        cq.orderBy(cb.desc(fromEvent.get(Event_.EVENT_DATE)));
        cq.select(fromEvent);

        TypedQuery<Event> query = em.createQuery(cq);
        query.setFirstResult(params.getFrom()).setMaxResults(params.getSize());
        List<Event> resultList = query.getResultList();
        return resultList;
    }

    default List<Event> getEventsByPublicParams(EntityManager em, EventParamsForPublic params) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> fromEvent = cq.from(Event.class);

        Predicate publishedState = cb.equal(fromEvent.get(Event_.STATE), EventState.PUBLISHED);
        Predicate searchByAnnotationAndDescription = cb.or(
                cb.like(cb.lower(fromEvent.get(Event_.ANNOTATION)), ("%" + params.getText() + "%").toLowerCase()),
                cb.like(cb.lower(fromEvent.get(Event_.DESCRIPTION)), ("%" + params.getText() + "%").toLowerCase())
        );

        Predicate dateTime;
        if (params.getRangeStart() == null || params.getRangeEnd() == null) {
            dateTime = cb.greaterThan(fromEvent.get(Event_.EVENT_DATE), LocalDateTime.now());
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(params.getRangeStart(), formatter);
            LocalDateTime endTime = LocalDateTime.parse(params.getRangeEnd(), formatter);

            dateTime = cb.between(fromEvent.get(Event_.EVENT_DATE), startTime, endTime);
        }

        cq.where(publishedState, searchByAnnotationAndDescription, dateTime);
        cq.orderBy(cb.desc(fromEvent.get(Event_.EVENT_DATE)));
        cq.select(fromEvent);

        TypedQuery<Event> query = em.createQuery(cq);
        query.setFirstResult(params.getFrom()).setMaxResults(params.getSize());

        List<Event> resultList = query.getResultList();
        return resultList;
    }

}
