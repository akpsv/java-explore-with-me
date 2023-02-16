package ru.akpsv.main.category;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.akpsv.main.category.model.Category;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    default List<Category> get(EntityManager em, Integer from, Integer size){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        Root<Category> fromCategory = cq.from(Category.class);
        cq.select(fromCategory);
        TypedQuery<Category> query = em.createQuery(cq);
        query.setFirstResult(from).setMaxResults(size);
        return query.getResultList();
    }
}
