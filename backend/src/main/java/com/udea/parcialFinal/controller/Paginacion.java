package com.udea.parcialFinal.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

final class Paginacion {

    static final String PAGINA_POR_DEFECTO = "0";
    static final int TAMANO_POR_DEFECTO = 10;
    static final int TAMANO_MAXIMO = 100;

    private Paginacion() {
    }

    static Pageable crear(int page, Integer size, Integer pageSize) {
        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El parámetro page debe ser mayor o igual a 0."
            );
        }
        if (size != null && pageSize != null && !size.equals(pageSize)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Los parámetros size y pageSize no pueden tener valores distintos."
            );
        }
        int tamano = size != null ? size : (pageSize != null ? pageSize : TAMANO_POR_DEFECTO);
        if (tamano < 1 || tamano > TAMANO_MAXIMO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tamaño de página debe estar entre 1 y " + TAMANO_MAXIMO + "."
            );
        }
        return PageRequest.of(page, tamano);
    }
}
