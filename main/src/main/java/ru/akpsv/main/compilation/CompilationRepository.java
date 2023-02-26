package ru.akpsv.main.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    default List<Compilation> getCompilations(EntityManager em, Boolean pinned, Integer from, Integer size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Compilation> cq = cb.createQuery(Compilation.class);
        Root<Compilation> fromCompilation = cq.from(Compilation.class);
        Predicate pinnedPredicate = cb.equal(fromCompilation.get(Compilation_.PINNED), pinned);
        cq.select(fromCompilation).where(pinnedPredicate);
        TypedQuery<Compilation> query = em.createQuery(cq);
        query.setFirstResult(from).setMaxResults(size);
        return query.getResultList();
    }
}
