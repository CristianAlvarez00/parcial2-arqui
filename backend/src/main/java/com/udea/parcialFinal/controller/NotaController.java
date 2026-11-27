package com.udea.parcialFinal.controller;

import com.udea.parcialFinal.dto.NotaRequest;
import com.udea.parcialFinal.dto.NotaResponse;
import com.udea.parcialFinal.dto.PageResponse;
import com.udea.parcialFinal.service.NotaService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public PageResponse<NotaResponse> consultar(
            @PathVariable("cedula") String cedula,
            @RequestParam(name = "page", defaultValue = Paginacion.PAGINA_POR_DEFECTO) int page,
            @Parameter(schema = @Schema(defaultValue = "10"))
            @RequestParam(name = "size", required = false) Integer size,
            @Parameter(hidden = true)
            @RequestParam(name = "pageSize", required = false) Integer pageSize
    ) {
        return notas.consultar(cedula, Paginacion.crear(page, size, pageSize));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotaResponse registrar(@PathVariable("cedula") String cedula, @RequestBody NotaRequest solicitud) {
        return notas.registrar(cedula, solicitud);
    }
}
