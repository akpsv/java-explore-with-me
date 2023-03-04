package ru.akpsv.main.user.repository;

import ru.akpsv.main.user.model.User;
import ru.akpsv.main.user.model.User_;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class UserRepositoryAdvancedImpl implements UserRepositoryAdvanced {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<User> getUsersByIds(List<Long> ids, Integer from, Integer size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> fromUser = cq.from(User.class);
        Predicate idMatch = fromUser.get(User_.ID).in(ids);
        cq.select(fromUser).where(idMatch);
        TypedQuery<User> query = em.createQuery(cq);
        query.setFirstResult(from).setMaxResults(size);
        return query.getResultList();
    }
}
