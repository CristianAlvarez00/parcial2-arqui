package com.udea.parcialFinal.dto;

import java.math.BigDecimal;

public record NotaResponse(Long id, String cedula, String materiaCodigo, String materiaNombre, BigDecimal nota) {
}
