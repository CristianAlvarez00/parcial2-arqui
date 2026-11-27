package com.udea.parcialFinal.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un estudiante registrado")
public record EstudianteResponse(
        @Schema(description = "Identificador interno del estudiante", example = "1")
        Long id,

        @Schema(description = "Nombres del estudiante", example = "Ana María")
        String nombre,

        @Schema(description = "Apellidos del estudiante", example = "Gómez Pérez")
        String apellido,

        @Schema(description = "Número de cédula del estudiante", example = "1001234567")
        String cedula,

        @Schema(description = "Programa académico", example = "Ingeniería de Sistemas")
        String programa
) implements Serializable {
}
