package ru.akpsv.main.error;

public class ViolationOfRestrictionsException extends RuntimeException {
    public ViolationOfRestrictionsException(String message) {
        super(message);
    }
}
