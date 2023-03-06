package ru.akpsv.main.event.repository;

import ru.akpsv.main.event.EventParamsForAdmin;
import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.event.model.EventState;
import ru.akpsv.main.event.model.Event_;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventRepositoryAdvancedImpl implements EventRepositoryAdvanced {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Event> getEventsByUser(Long userId, Integer from, Integer size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> fromEvent = cq.from(Event.class);
        cq.select(fromEvent).where(cb.equal(fromEvent.get(Event_.INITIATOR_ID), userId));
        TypedQuery<Event> query = em.createQuery(cq);
        query.setFirstResult(from).setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public List<Event> getEventsByAdminParams(EventParamsForAdmin params) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> fromEvent = cq.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();
        params.getUsers().ifPresent(userIds -> predicates.add(fromEvent.get(Event_.INITIATOR_ID).in(userIds)));
        params.getStates()
                .ifPresent(groupOfEventStates -> {
                    List<EventState> collectOfEventStates = groupOfEventStates.stream().map(EventState::valueOf).collect(Collectors.toList());
                    predicates.add(fromEvent.get(Event_.STATE).in(collectOfEventStates));
                });
        params.getCategories().ifPresent(categoryIds -> predicates.add(fromEvent.get(Event_.CATEGORY_ID).in(categoryIds)));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        params.getRangeStart().flatMap(startTimestamp -> params.getRangeEnd().map(endTimestamp ->
                predicates.add(cb.between(
                        fromEvent.get(Event_.EVENT_DATE),
                        LocalDateTime.parse(startTimestamp, formatter),
                        LocalDateTime.parse(endTimestamp, formatter)))
        ));

        cq.orderBy(cb.desc(fromEvent.get(Event_.EVENT_DATE)));
        cq.select(fromEvent).where(predicates.toArray(Predicate[]::new));
        TypedQuery<Event> query = em.createQuery(cq);
        query.setFirstResult(params.getFrom()).setMaxResults(params.getSize());
        return query.getResultList();
    }

    @Override
    public List<Event> getEventsByPublicParams(EventParams params) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> fromEvent = cq.from(Event.class);

      //  List<Predicate> predicates = handleOfParams(params, cb, cq, fromEvent);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(fromEvent.get(Event_.STATE), EventState.PUBLISHED));
        params.getText().ifPresent(text -> predicates.add(cb.or(
                        cb.like(cb.lower(fromEvent.get(Event_.ANNOTATION)), ("%" + text + "%").toLowerCase()),
                        cb.like(cb.lower(fromEvent.get(Event_.DESCRIPTION)), ("%" + text + "%").toLowerCase())
                )
        ));
        params.getCategories().ifPresent(categoryIds -> predicates.add(fromEvent.get(Event_.CATEGORY_ID).in(categoryIds)));

        params.getPaid().ifPresent(paid -> predicates.add(cb.equal(fromEvent.get(Event_.PAID), paid)));
        //Только события у который не исчерпан лимит запросов на участие
        params.getOnlyAvailable().ifPresent(available -> predicates.add(cb.equal(fromEvent.get(Event_.AVAILABLE_TO_PARICIPANTS), available)));
        params.getSort().ifPresent(sort -> {
            if ("EVENT_DATE".equals(sort.toUpperCase())) cq.orderBy(cb.desc(fromEvent.get(Event_.EVENT_DATE)));
            else cq.orderBy(cb.desc(fromEvent.get(Event_.VIEWS)));
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        params.getRangeStart()
                .flatMap(startTimestamp -> params.getRangeEnd().map(endTimestamp ->
                        predicates.add(cb.between(
                                fromEvent.get(Event_.EVENT_DATE),
                                LocalDateTime.parse(startTimestamp, formatter),
                                LocalDateTime.parse(endTimestamp, formatter)))
                ))
                .orElseGet(() -> predicates.add(cb.greaterThan(fromEvent.get(Event_.EVENT_DATE), LocalDateTime.now())));

        cq.select(fromEvent).where(predicates.toArray(Predicate[]::new));
        TypedQuery<Event> query = em.createQuery(cq);
        query.setFirstResult(params.getFrom()).setMaxResults(params.getSize());
        List<Event> resultList = query.getResultList();
        return resultList;
    }

    //List<Predicate> handleOfParams(EventParamsForPublic params, CriteriaBuilder cb, CriteriaQuery<Event> cq, Root<Event> fromEvent)

    private List<Predicate> handleOfParams(EventParams params, CriteriaBuilder cb, CriteriaQuery<Event> cq, Root<Event> fromEvent) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(fromEvent.get(Event_.STATE), EventState.PUBLISHED));
        params.getText().ifPresent(text -> predicates.add(cb.or(
                        cb.like(cb.lower(fromEvent.get(Event_.ANNOTATION)), ("%" + text + "%").toLowerCase()),
                        cb.like(cb.lower(fromEvent.get(Event_.DESCRIPTION)), ("%" + text + "%").toLowerCase())
                )
        ));
        params.getCategories().ifPresent(categoryIds -> predicates.add(fromEvent.get(Event_.CATEGORY_ID).in(categoryIds)));

        params.getPaid().ifPresent(paid -> predicates.add(cb.equal(fromEvent.get(Event_.PAID), paid)));
        //Только события у который не исчерпан лимит запросов на участие
        params.getOnlyAvailable().ifPresent(available -> predicates.add(cb.equal(fromEvent.get(Event_.AVAILABLE_TO_PARICIPANTS), available)));
        params.getSort().ifPresent(sort -> {
            if ("EVENT_DATE".equals(sort.toUpperCase())) cq.orderBy(cb.desc(fromEvent.get(Event_.EVENT_DATE)));
            else cq.orderBy(cb.desc(fromEvent.get(Event_.VIEWS)));
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        params.getRangeStart()
                .flatMap(startTimestamp -> params.getRangeEnd().map(endTimestamp ->
                        predicates.add(cb.between(
                                fromEvent.get(Event_.EVENT_DATE),
                                LocalDateTime.parse(startTimestamp, formatter),
                                LocalDateTime.parse(endTimestamp, formatter)))
                ))
                .orElseGet(() -> predicates.add(cb.greaterThan(fromEvent.get(Event_.EVENT_DATE), LocalDateTime.now())));
        return predicates;
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
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public List<Event> getDeffrentEvents(EventParams params, GetGroupOfPredicates predicates){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> fromEvent = cq.from(Event.class);

        List<Predicate> groupOfPredicates = predicates.getPredicates(params, cb, cq, fromEvent);

        cq.select(fromEvent).where(groupOfPredicates.toArray(Predicate[]::new));
        TypedQuery<Event> query = em.createQuery(cq);
        return query.getResultList();
    }

    public void executeMethod(EventParams eventParams){
        GetGroupOfPredicates predicatesFunc = (EventParams params, CriteriaBuilder cb, CriteriaQuery<Event> cq, Root<Event> fromEvent)->{
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(fromEvent.get(Event_.STATE), EventState.PUBLISHED));
            params.getText().ifPresent(text -> predicates.add(cb.or(
                            cb.like(cb.lower(fromEvent.get(Event_.ANNOTATION)), ("%" + text + "%").toLowerCase()),
                            cb.like(cb.lower(fromEvent.get(Event_.DESCRIPTION)), ("%" + text + "%").toLowerCase())
                    )
            ));
            params.getCategories().ifPresent(categoryIds -> predicates.add(fromEvent.get(Event_.CATEGORY_ID).in(categoryIds)));

            params.getPaid().ifPresent(paid -> predicates.add(cb.equal(fromEvent.get(Event_.PAID), paid)));
            //Только события у который не исчерпан лимит запросов на участие
            params.getOnlyAvailable().ifPresent(available -> predicates.add(cb.equal(fromEvent.get(Event_.AVAILABLE_TO_PARICIPANTS), available)));
            params.getSort().ifPresent(sort -> {
                if ("EVENT_DATE".equals(sort.toUpperCase())) cq.orderBy(cb.desc(fromEvent.get(Event_.EVENT_DATE)));
                else cq.orderBy(cb.desc(fromEvent.get(Event_.VIEWS)));
            });

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            params.getRangeStart()
                    .flatMap(startTimestamp -> params.getRangeEnd().map(endTimestamp ->
                            predicates.add(cb.between(
                                    fromEvent.get(Event_.EVENT_DATE),
                                    LocalDateTime.parse(startTimestamp, formatter),
                                    LocalDateTime.parse(endTimestamp, formatter)))
                    ))
                    .orElseGet(() -> predicates.add(cb.greaterThan(fromEvent.get(Event_.EVENT_DATE), LocalDateTime.now())));
            return predicates;
        };
        List<Event> deffrentEvents = getDeffrentEvents(eventParams, predicatesFunc);
    }

}

interface GetGroupOfPredicates{
    List<Predicate> getPredicates(EventParams params, CriteriaBuilder cb, CriteriaQuery<Event> cq, Root<Event> fromEvent);
}
