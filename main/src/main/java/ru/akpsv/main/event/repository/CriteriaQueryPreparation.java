package ru.akpsv.main.event.repository;

import ru.akpsv.main.event.EventParams;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@FunctionalInterface
public interface CriteriaQueryPreparation<T> {
    CriteriaQuery<T> prepare(EventParams params, CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> fromEvent);
}
