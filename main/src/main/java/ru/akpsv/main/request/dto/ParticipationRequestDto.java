package ru.akpsv.main.request.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ParticipationRequestDto {
    //Заявка на участие в событии
    private Long id; //Идентификатор заявки
    @NotNull
    private Long event; //Идентификатор события
    @NotNull
    private Long requester; //Идентификатор пользователя, отправившего заявку
    private String status; //Статус заявки. example: PENDING (может быть перечисление)
    private String created; //example: 2022-09-06T21:10:05.432 Дата и время создания заявки
}
