package com.uespi.reservalab.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.uespi.reservalab.ApiErrors;
import com.uespi.reservalab.exceptions.RegistroDuplicadoException;

@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(RegistroDuplicadoException.class)
    public ResponseEntity<ApiErrors> handleRegistroDuplicadoException(RegistroDuplicadoException ex) {
        return new ResponseEntity<>(new ApiErrors(ex.getMessage()), HttpStatus.CONFLICT); // 409
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrors> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ApiErrors(ex.getMessage()), HttpStatus.BAD_REQUEST); // 400
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrors> handleGenericException(Exception ex) {
        return new ResponseEntity<>(new ApiErrors("Erro interno: " + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
