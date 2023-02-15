package ru.akpsv.main.error;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.akpsv.main.user.UserController;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestControllerAdvice(assignableTypes = {UserController.class})
public class ErrorHandler {

    //Запрос составлен некорректно (400)
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectRequest(javax.validation.ConstraintViolationException exception) {
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
    public ApiError handleIntegrityConstraint(final ConstraintViolationException exception) {
        return ApiError.builder()
                .status(HttpStatus.FORBIDDEN.toString())
                .reason(exception.getConstraintName())
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
