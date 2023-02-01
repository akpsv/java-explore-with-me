package ru.akpsv.statsvc;

import ru.akpsv.statsvc.model.Request;

import java.util.Optional;

interface StatsService {

    Optional<Request> save(Request request);
}
