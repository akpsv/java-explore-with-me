package ru.akpsv.main.event.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Location {
    //Широта и долгота места проведения события
    private Double lat; //Широта
    private Double lon; //Долгота
}
