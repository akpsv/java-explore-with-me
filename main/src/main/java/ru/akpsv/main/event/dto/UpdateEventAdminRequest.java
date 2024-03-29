package ru.akpsv.main.event.dto;

import lombok.Builder;
import ru.akpsv.main.event.model.Location;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Данные для изменения информации о событии.
 * Если поле в запросе не указано (равно null) - значит изменение этих данных не треубется.
 */
public class UpdateEventAdminRequest extends UpdateEventRequest {
    @Builder(toBuilder = true)
    public UpdateEventAdminRequest(@Max(2000) @Min(20) String annotation,
                                   Long category,
                                   @Max(7000) @Min(20) String description,
                                   String eventDate,
                                   Location location,
                                   Boolean paid,
                                   Long participantLimit,
                                   Boolean requestModeration,
                                   String stateAction,
                                   @Max(120) @Min(3) String title) {
        super(annotation, category, description, eventDate, location, paid, participantLimit, requestModeration, stateAction, title);
    }
}
