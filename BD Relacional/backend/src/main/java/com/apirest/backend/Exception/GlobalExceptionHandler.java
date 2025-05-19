package com.apirest.backend.Exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("Error!", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarErroresGenerales(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("Error!", "Ocurrió un fallo inesperado. Por favor, intente más tarde.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
}
