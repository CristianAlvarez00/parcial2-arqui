package com.udea.parcialFinal.controller;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail manejarIntegridad(DataIntegrityViolationException excepcion) {
        String restriccion = obtenerRestriccion(excepcion);
        if ("uq_estudiante_cedula".equals(restriccion)) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "La cédula ya está registrada.");
        }
        if ("uq_matricula_estudiante_materia".equals(restriccion)) {
            return ProblemDetail.forStatusAndDetail(
                    HttpStatus.CONFLICT, "El estudiante ya tiene una nota registrada para esta materia."
            );
        }

        LOG.error("No se pudo guardar el registro por una restricción de integridad ({}).", restriccion);
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar el registro."
        );
    }

    private String obtenerRestriccion(Throwable excepcion) {
        for (Throwable causa = excepcion; causa != null; causa = causa.getCause()) {
            if (causa instanceof ConstraintViolationException violacion) {
                return violacion.getConstraintName();
            }
        }
        return null;
    }
}
