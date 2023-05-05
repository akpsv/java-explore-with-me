package ru.akpsv.main.event;

import lombok.*;

import java.util.List;

/**
 * Изменение статуса запроса на участие в событии текущего пользователя
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds; //Идентификаторы запросов на участие в событии текущего пользователя
    private String status; //Новый статус запроса на участие в событии текущего пользователя (Enum: [CONFIRMED, REJECTED])
}
