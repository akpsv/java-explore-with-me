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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

        List<Long> userIds = params.getUsers().orElse(Collections.emptyList());
        Predicate users = fromEvent.get(Event_.INITIATOR_ID).in(userIds);


        List<EventState> collectOfEventStates = params.getStates().orElse(Collections.emptyList())
                .stream()
                .map(state -> EventState.valueOf(state))
                .collect(Collectors.toList());
        Predicate states = fromEvent.get(Event_.STATE).in(collectOfEventStates);

        List<Long> categoryIds = params.getCategories().orElseGet(Collections::emptyList);
        Predicate categories = fromEvent.get(Event_.CATEGORY_ID).in(categoryIds);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (params.getRangeStart().isPresent() && params.getRangeEnd().isPresent()) {
            LocalDateTime start = LocalDateTime.parse(params.getRangeStart().get(), formatter);
            LocalDateTime end = LocalDateTime.parse(params.getRangeEnd().get(), formatter);
            Predicate rangeOfDateTime = cb.between(fromEvent.get(Event_.EVENT_DATE), start, end);
            cq.where(users, states, categories, rangeOfDateTime);
        } else {
            cq.where(users, states, categories);
        }


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
        String text = params.getText().orElse("");
        Predicate searchByAnnotationAndDescription = cb.or(
                cb.like(cb.lower(fromEvent.get(Event_.ANNOTATION)), ("%" + text + "%").toLowerCase()),
                cb.like(cb.lower(fromEvent.get(Event_.DESCRIPTION)), ("%" + text + "%").toLowerCase())
        );

        Predicate dateTime;
        if (!params.getRangeStart().isPresent() || !params.getRangeEnd().isPresent()) {
            dateTime = cb.greaterThan(fromEvent.get(Event_.EVENT_DATE), LocalDateTime.now());
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(params.getRangeStart().get(), formatter);
            LocalDateTime endTime = LocalDateTime.parse(params.getRangeEnd().get(), formatter);

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

    default Event getFullEventInfoByUser(EntityManager em, Long userId, Long eventId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> fromEvent = cq.from(Event.class);
        Predicate user = cb.equal(fromEvent.get(Event_.INITIATOR_ID), userId);
        Predicate event = cb.equal(fromEvent.get(Event_.ID), eventId);
        cq.select(fromEvent).where(user, event);
        TypedQuery<Event> query = em.createQuery(cq);
        return query.getSingleResult();
    }

    Optional<Event> getEventByInitiatorIdAndId(Long userId, Long eventId);

}
