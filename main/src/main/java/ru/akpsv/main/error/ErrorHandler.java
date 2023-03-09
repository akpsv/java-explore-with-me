package ru.akpsv.main.error;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.akpsv.main.category.CategoryController;
import ru.akpsv.main.compilation.CompilationController;
import ru.akpsv.main.event.controller.AdminEventCotnroller;
import ru.akpsv.main.event.controller.PrivateEventController;
import ru.akpsv.main.event.controller.PublicEventController;
import ru.akpsv.main.request.RequestController;
import ru.akpsv.main.user.UserController;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestControllerAdvice(assignableTypes = {
        UserController.class,
        AdminEventCotnroller.class, PublicEventController.class, PrivateEventController.class,
        CategoryController.class, CompilationController.class,
        RequestController.class
})
public class ErrorHandler {

    //Запрос составлен некорректно (400)
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectRequest(MethodArgumentNotValidException exception) {
        return ApiError.builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .reason(exception.getMessage())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    //Нарушение целостности данных (409)
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIncorrectRequest(ViolationOfRestrictionsException exception) {
        return ApiError.builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .reason("For the requested operation the conditions are not met.")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIncorrectRequest(javax.validation.ConstraintViolationException exception) {
        return ApiError.builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .reason(exception.getMessage())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIntegrityConstraint(final ConstraintViolationException exception) {
        return ApiError.builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .reason(exception.getConstraintName())
                .message(exception.toString())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIntegrityConstraint(final LimitReachedException exception) {
        return ApiError.builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .reason("For the requested operation the conditions are not met.")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    //Пользователь не найден или недоступен (404)
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleIntegrityConstraint(final NoSuchElementException exception) {
        return ApiError.builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .reason("The required object was not found.")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }


}
