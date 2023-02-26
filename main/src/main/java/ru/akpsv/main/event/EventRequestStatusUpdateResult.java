package ru.akpsv.main.event;

import lombok.*;
import ru.akpsv.main.request.dto.ParticipationRequestDto;

import java.util.List;

/**
 * Результат подтверждения/отклонения заявок на участие в событии
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;  //Идентификаторы запросов на участие в событии текущего пользователя
    private List<ParticipationRequestDto> rejectedRequests;      //Новый статус запроса на участие в событии текущего пользователя (Enum: [CONFIRMED, REJECTED])
}

