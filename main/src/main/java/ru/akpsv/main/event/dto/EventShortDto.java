package ru.akpsv.main.event.dto;


import lombok.*;
import ru.akpsv.main.category.dto.CategoryDto;
import ru.akpsv.main.user.dto.UserShortDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EventShortDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private String eventDate;
    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private Long views;
}
