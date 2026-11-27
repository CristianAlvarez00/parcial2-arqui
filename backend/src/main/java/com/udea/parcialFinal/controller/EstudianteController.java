package com.udea.parcialFinal.controller;

import com.udea.parcialFinal.dto.EstudianteRequest;
import com.udea.parcialFinal.dto.EstudianteResponse;
import com.udea.parcialFinal.service.EstudianteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/estudiantes")
public class EstudianteController {

    private final EstudianteService estudiantes;

    public EstudianteController(EstudianteService estudiantes) {
        this.estudiantes = estudiantes;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstudianteResponse registrar(@RequestBody EstudianteRequest solicitud) {
        return estudiantes.registrar(solicitud);
    }
}
