package ru.akpsv.main.request.dto;

import org.springframework.stereotype.Service;
import ru.akpsv.main.request.model.Request;

import java.time.format.DateTimeFormatter;

@Service
public class RequestMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {

        return ParticipationRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequesterId())
                .event(request.getEventId())
                .created(request.getCreated().format(formatter))
                .status(request.getStatus().name())
                .build();
    }
}
