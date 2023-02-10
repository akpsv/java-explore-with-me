package ru.akpsv.dto.main.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private int id;
    private String name;
    private String email;
}
