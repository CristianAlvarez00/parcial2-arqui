package com.udea.parcialFinal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos requeridos para registrar un estudiante")
public record EstudianteRequest(
        @Schema(description = "Nombres del estudiante", example = "Ana María", maxLength = 120,
                requiredMode = Schema.RequiredMode.REQUIRED)
        String nombre,

        @Schema(description = "Apellidos del estudiante", example = "Gómez Pérez", maxLength = 120,
                requiredMode = Schema.RequiredMode.REQUIRED)
        String apellido,

        @Schema(description = "Número de cédula (solo dígitos)", example = "1001234567",
                pattern = "[0-9]{1,20}", maxLength = 20, requiredMode = Schema.RequiredMode.REQUIRED)
        String cedula,

        @Schema(description = "Programa académico al que pertenece", example = "Ingeniería de Sistemas",
                maxLength = 120, requiredMode = Schema.RequiredMode.REQUIRED)
        String programa
) {
}
