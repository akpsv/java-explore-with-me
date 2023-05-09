package ru.akpsv.main.subscribe.repository;

import lombok.RequiredArgsConstructor;
import ru.akpsv.main.subscribe.model.Subscribe;
import ru.akpsv.main.subscribe.model.Subscribe_;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@RequiredArgsConstructor
public class SubscribeRepositoryAdvancedImpl implements SubscribeRepositoryAdvanced {
//    @PersistenceContext
//    private final EntityManager em;
//
//    @Override
//    public List<Subscribe> findByPublisherId(Long publisherId) {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Subscribe> cq = cb.createQuery(Subscribe.class);
//        Root<Subscribe> fromSubscribe = cq.from(Subscribe.class);
////        cq.where(fromSubscribe.get(Subscribe_. ))
//        return null;
//    }
//
//    @Override
//    public List<Subscribe> findBySubscriberId(Long subscriberId) {
//        return null;
//    }
}
