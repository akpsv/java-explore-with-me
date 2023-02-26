package ru.akpsv.main.error;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
public class ApiError {
    //Сведения об ошибке
    private List<String> errors;  //Список стектрейсов или описания ошибок
    private String message; //Сообщение об ошибке
    private String reason;  //Общее описание причины ошибки
    private String status;  //Код статуса HTTP-ответа (Enum)
    private String timestamp;   //Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
}
