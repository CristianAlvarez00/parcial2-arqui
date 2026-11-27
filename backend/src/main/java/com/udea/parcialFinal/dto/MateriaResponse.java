package com.udea.parcialFinal.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de una materia del catálogo académico")
public record MateriaResponse(
        @Schema(description = "Identificador interno de la materia", example = "2")
        Long id,

        @Schema(description = "Código único de la materia", example = "ARQ101")
        String codigo,

        @Schema(description = "Nombre de la materia", example = "Arquitectura de Software")
        String nombre
) implements Serializable {
}
