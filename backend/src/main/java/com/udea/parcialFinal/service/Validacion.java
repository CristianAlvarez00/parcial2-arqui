package com.udea.parcialFinal.service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

final class Validacion {

    private static final Pattern CEDULA = Pattern.compile("[0-9]{1,20}");
    private static final BigDecimal NOTA_MAXIMA = BigDecimal.valueOf(5);

    private Validacion() {
    }

    static String texto(String valor, String campo, int longitudMaxima) {
        if (valor == null || valor.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo " + campo + " es obligatorio.");
        }
        String texto = valor.strip();
        if (texto.length() > longitudMaxima) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El campo " + campo + " admite hasta " + longitudMaxima + " caracteres."
            );
        }
        return texto;
    }

    static String cedula(String valor) {
        String cedula = texto(valor, "cedula", 20);
        if (!CEDULA.matcher(cedula).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cédula debe contener solo dígitos.");
        }
        return cedula;
    }

    static BigDecimal nota(BigDecimal valor) {
        if (valor == null || valor.signum() < 0 || valor.compareTo(NOTA_MAXIMA) > 0
                || valor.stripTrailingZeros().scale() > 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La nota debe estar entre 0 y 5 y tener hasta dos decimales."
            );
        }
        return valor.setScale(2);
    }
}
