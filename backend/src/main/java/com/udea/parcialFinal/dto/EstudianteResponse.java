package com.udea.parcialFinal.dto;

import java.io.Serializable;

public record EstudianteResponse(
        Long id, String nombre, String apellido, String cedula, String programa
) implements Serializable {
}
