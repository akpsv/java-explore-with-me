package ru.akpsv.main.request.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ParticipationRequestDto {
    //Заявка на участие в событии
    private String created;     //example: 2022-09-06T21:10:05.432 Дата и время создания заявки
    private Long event;         //Идентификатор события
    private Long id;            //Идентификатор заявки
    private Long requester;     //Идентификатор пользователя, отправившего заявку
    private String status;      //Статус заявки. example: PENDING (может быть перечисление)
}
