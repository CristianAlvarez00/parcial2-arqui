package com.udea.parcialFinal.controller;

import com.udea.parcialFinal.dto.MateriaResponse;
import com.udea.parcialFinal.dto.PageResponse;
import com.udea.parcialFinal.service.MateriaService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/materias")
public class MateriaController {

    private final MateriaService materias;

    public MateriaController(MateriaService materias) {
        this.materias = materias;
    }

    @GetMapping
    public PageResponse<EntityModel<MateriaResponse>> consultar(
            @RequestParam(name = "page", defaultValue = Paginacion.PAGINA_POR_DEFECTO) int page,
            @Parameter(schema = @Schema(defaultValue = "10"))
            @RequestParam(name = "size", required = false) Integer size,
            @Parameter(hidden = true)
            @RequestParam(name = "pageSize", required = false) Integer pageSize
    ) {
        return EnlacesHateoas.paginaMaterias(
                materias.consultar(Paginacion.crear(page, size, pageSize))
        );
    }

    @GetMapping("/{codigo}")
    public EntityModel<MateriaResponse> consultarPorCodigo(@PathVariable("codigo") String codigo) {
        return EnlacesHateoas.materia(materias.consultarPorCodigo(codigo));
    }
}
