package ru.akpsv.main.event.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Location {
    //Широта и долгота места проведения события
    @NotBlank
    @Column(name = "location_longitude")
    private Double lon; //Долгота

    @NotBlank
    @Column(name = "location_latitude")
    private Double lat; //Широта
}
