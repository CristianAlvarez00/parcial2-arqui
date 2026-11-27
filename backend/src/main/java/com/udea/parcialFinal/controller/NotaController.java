package com.udea.parcialFinal.controller;

import java.util.List;

import com.udea.parcialFinal.dto.NotaRequest;
import com.udea.parcialFinal.dto.NotaResponse;
import com.udea.parcialFinal.service.NotaService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/estudiantes/{cedula}/notas")
public class NotaController {

    private final NotaService notas;

    public NotaController(NotaService notas) {
        this.notas = notas;
    }

    @GetMapping
    public List<NotaResponse> consultar(@PathVariable("cedula") String cedula) {
        return notas.consultar(cedula, Pageable.unpaged()).getContent();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotaResponse registrar(@PathVariable("cedula") String cedula, @RequestBody NotaRequest solicitud) {
        return notas.registrar(cedula, solicitud);
    }
}
