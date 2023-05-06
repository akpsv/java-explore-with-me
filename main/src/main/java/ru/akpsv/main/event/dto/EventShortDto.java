package ru.akpsv.main.event.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;

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

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CategoryDto {
        private Long id;
        @NotBlank
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserShortDto {
        private Long id;
        private String name;
    }
}
