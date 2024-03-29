package ru.akpsv.main.request.repository;

import ru.akpsv.main.request.model.Request;

import java.util.List;

public interface RequestRepositoryAdvanced {
    List<Request> getRequestsFromList(List<Long> requestIds, Long userId, Long eventId);
}
