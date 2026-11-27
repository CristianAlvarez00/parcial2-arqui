package com.udea.parcialFinal.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos requeridos para registrar la nota de un estudiante en una materia")
public record NotaRequest(
        @Schema(description = "Código único de la materia", example = "ARQ101", maxLength = 20,
                requiredMode = Schema.RequiredMode.REQUIRED)
        String materiaCodigo,

        @Schema(description = "Calificación obtenida entre 0.00 y 5.00 (máximo dos decimales)",
                example = "4.50", minimum = "0", maximum = "5", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal nota
) {
}
