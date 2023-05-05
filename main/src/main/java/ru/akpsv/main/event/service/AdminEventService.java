package ru.akpsv.main.event.service;

import ru.akpsv.main.event.EventParams;
import ru.akpsv.main.event.dto.EventFullDto;
import ru.akpsv.main.event.dto.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventService {
    List<EventFullDto> getEventsByAdminParams(EventParams params);

    EventFullDto updateEventByAdmin(UpdateEventAdminRequest updatingRequest, Long eventId);
}
