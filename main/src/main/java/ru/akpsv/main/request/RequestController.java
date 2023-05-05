package ru.akpsv.main.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.akpsv.main.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     *
     * @param userId
     * @param eventId
     * @return
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.create(userId, eventId);
    }

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     *
     * @return
     */
    @GetMapping
    public List<ParticipationRequestDto> getRequestsOfCurrentUser(@PathVariable Long userId) {
        return requestService.getRequestsOfCurrentUser(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelOwnRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelOwnRequest(userId, requestId);
    }
}
