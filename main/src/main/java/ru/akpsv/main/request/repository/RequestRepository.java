package ru.akpsv.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akpsv.main.request.model.Request;
import ru.akpsv.main.request.model.RequestStatus;
import ru.akpsv.main.request.model.Request_;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>, RequestRepositoryAdvanced {
    List<Request> getRequestsByRequesterId(Long userId);

    List<Request> getRequestsByEventId(Long eventId);

//    default List<Request> getRequestsFromList(EntityManager em, Long[] requestIds, Long userId, Long eventId) {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Request> cq = cb.createQuery(Request.class);
//        Root<Request> fromRequest = cq.from(Request.class);
//        Predicate requestsForEvent = cb.equal(fromRequest.get(Request_.EVENT_ID), eventId);
//        Predicate requestsWithIdsFromList = fromRequest.get(Request_.ID).in(requestIds);
//        Predicate onlyPendingRequests = cb.equal(fromRequest.get(Request_.STATUS), RequestStatus.PENDING);
//        cq.select(fromRequest).where(requestsForEvent, requestsWithIdsFromList, onlyPendingRequests);
//        TypedQuery<Request> query = em.createQuery(cq);
//        return query.getResultList();
//    }
}
