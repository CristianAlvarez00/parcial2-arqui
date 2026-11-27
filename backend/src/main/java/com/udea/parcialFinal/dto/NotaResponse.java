package com.udea.parcialFinal.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Calificación registrada de un estudiante en una materia")
public record NotaResponse(
        @Schema(description = "Identificador interno del registro de matrícula/nota", example = "10")
        Long id,

        @Schema(description = "Cédula del estudiante", example = "1001234567")
        String cedula,

        @Schema(description = "Código único de la materia", example = "ARQ101")
        String materiaCodigo,

        @Schema(description = "Nombre de la materia", example = "Arquitectura de Software")
        String materiaNombre,

        @Schema(description = "Nota definitiva entre 0.00 y 5.00", example = "4.50")
        BigDecimal nota
) implements Serializable {
}
