package com.udea.parcialFinal.dto;

import java.io.Serializable;

public record MateriaResponse(Long id, String codigo, String nombre) implements Serializable {
}
