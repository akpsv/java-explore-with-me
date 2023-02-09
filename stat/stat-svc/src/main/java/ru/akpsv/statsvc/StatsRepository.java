package ru.akpsv.statsvc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akpsv.dto.StatDtoOut;
import ru.akpsv.statsvc.model.Request;
import ru.akpsv.statsvc.model.Request_;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface StatsRepository extends JpaRepository<Request, Long> {
    /**
     * @param entityManager
     * @param startDateTime - Дата и время начала диапазона за который нужно выгрузить статистику (в формате "yyyy-MM-dd HH:mm:ss")
     * @param endDateTime   - Дата и время конца диапазона за который нужно выгрузить статистику (в формате "yyyy-MM-dd HH:mm:ss")
     * @param uris          - Список uri для которых нужно выгрузить статистику
     * @param unique        -  Нужно ли учитывать только уникальные посещения (только с уникальным ip)
     *                      Default value : false
     * @return - список подходящих запросов
     */
    default Optional<List<StatDtoOut>> getStatDtoByParameters(EntityManager entityManager, LocalDateTime startDateTime,
                                                    LocalDateTime endDateTime, String[] uris, boolean unique) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
        Root<Request> fromRequests = cq.from(Request.class);
        Predicate rangeBetweenTimestamps = cb.between(fromRequests.get(Request_.TIMESTAMP), startDateTime, endDateTime);
        Predicate equalsArrayOfUri = fromRequests.get(Request_.URI).in(uris);
        cq.where(rangeBetweenTimestamps, equalsArrayOfUri);
        cq.groupBy(fromRequests.get(Request_.URI));

        if (unique) {
            //Подсчитать уникальные ip в группе одинаковых uri
            cq.multiselect(fromRequests.get(Request_.APP),fromRequests.get(Request_.URI), cb.countDistinct(fromRequests.get(Request_.IP)));
        } else {
            //Подсчитать количество uri в группе одинаковых
            cq.multiselect(fromRequests.get(Request_.APP), fromRequests.get(Request_.URI), cb.count( fromRequests.get(Request_.URI)));
        }
        TypedQuery<Tuple> query = entityManager.createQuery(cq);
        List<Tuple> resultList = query.getResultList();

        List<StatDtoOut> listStatDtos = resultList.stream()
                .map(tuple -> new StatDtoOut((String) tuple.get(0), (String) tuple.get(1), (Long) tuple.get(2)))
                .collect(Collectors.toList());
        return Optional.of(listStatDtos);
    }
}