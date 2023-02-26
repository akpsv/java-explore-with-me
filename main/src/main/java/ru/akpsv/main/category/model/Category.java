package ru.akpsv.main.category.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;        //Идентификатор категории

    @Column(name = "name")
    private String name;    //Название категории
}
