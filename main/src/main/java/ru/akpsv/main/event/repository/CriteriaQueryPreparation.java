package ru.akpsv.main.event.repository;

import ru.akpsv.main.event.EventParams;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

@FunctionalInterface
public interface CriteriaQueryPreparation<T> {
    CriteriaQuery<T> prepare(EventParams params, CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> fromEvent);
}