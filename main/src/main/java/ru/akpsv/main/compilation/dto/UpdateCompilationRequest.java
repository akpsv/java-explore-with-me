package ru.akpsv.main.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Изменение информации о подборке событий. Если поле в запросе не указано (равно null) - значит изменение этих данных не треубется.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    private Set<Long> events; //Список id событий подборки для полной замены текущего списка
    private Boolean pinned; //Закреплена ли подборка на главной странице сайта
    private String title; //Заголовок подборки
}
