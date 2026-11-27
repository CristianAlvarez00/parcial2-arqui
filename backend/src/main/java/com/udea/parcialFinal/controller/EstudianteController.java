package com.udea.parcialFinal.controller;

import com.udea.parcialFinal.dto.EstudianteRequest;
import com.udea.parcialFinal.dto.EstudianteResponse;
import com.udea.parcialFinal.dto.PageResponse;
import com.udea.parcialFinal.service.EstudianteService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/estudiantes")
public class EstudianteController {

    private final EstudianteService estudiantes;

    public EstudianteController(EstudianteService estudiantes) {
        this.estudiantes = estudiantes;
    }

    @GetMapping
    public PageResponse<EstudianteResponse> consultar(
            @RequestParam(name = "page", defaultValue = Paginacion.PAGINA_POR_DEFECTO) int page,
            @Parameter(schema = @Schema(defaultValue = "10"))
            @RequestParam(name = "size", required = false) Integer size,
            @Parameter(hidden = true)
            @RequestParam(name = "pageSize", required = false) Integer pageSize
    ) {
        return estudiantes.consultar(Paginacion.crear(page, size, pageSize));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstudianteResponse registrar(@RequestBody EstudianteRequest solicitud) {
        return estudiantes.registrar(solicitud);
    }
}
