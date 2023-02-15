package ru.akpsv.main.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akpsv.main.user.model.User;
import ru.akpsv.main.user.model.User_;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    default List<User> getUsersByIds(EntityManager em, Long[] ids, Integer from, Integer size){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> fromUser = cq.from(User.class);
        Predicate afterIn = fromUser.get(User_.ID).in(ids);
        cq.select(fromUser).where(afterIn);
        TypedQuery<User> query = em.createQuery(cq);
        query.setFirstResult(from).setMaxResults(size);
        return query.getResultList();
    }
}
