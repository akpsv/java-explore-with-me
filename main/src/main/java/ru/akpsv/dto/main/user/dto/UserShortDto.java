package ru.akpsv.dto.main.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder(toBuilder = true)
public class UserShortDto {
     private int id;
     private String name;
}
