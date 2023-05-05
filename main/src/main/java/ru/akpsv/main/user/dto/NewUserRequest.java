package ru.akpsv.main.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NewUserRequest {
    //Данные нового пользователя
    @NotBlank
    private String name; //Имя
    @Email
    @NotBlank
    private String email; //Почтовый адрес
}
