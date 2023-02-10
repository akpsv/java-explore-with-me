package ru.akpsv.dto.main.event.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import ru.akpsv.dto.main.category.dto.CategoryDto;
import ru.akpsv.dto.main.user.dto.UserShortDto;

@AllArgsConstructor
@Builder(toBuilder = true)
public class EventShortDto {
    private int id;
    private String annotation;
    private CategoryDto category;
    private int confirmedRequests;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private int views;
}
