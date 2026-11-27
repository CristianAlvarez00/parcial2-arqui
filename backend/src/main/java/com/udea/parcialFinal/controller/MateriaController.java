package com.udea.parcialFinal.controller;

import com.udea.parcialFinal.dto.MateriaResponse;
import com.udea.parcialFinal.dto.PageResponse;
import com.udea.parcialFinal.service.MateriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/materias")
@Tag(name = "Materias", description = "Consulta paginada e individual del catálogo de materias")
public class MateriaController {

    private final MateriaService materias;

    public MateriaController(MateriaService materias) {
        this.materias = materias;
    }

    @GetMapping
    @Operation(
            summary = "Listar materias paginadas",
            description = "Retorna el catálogo paginado de materias disponibles junto con sus enlaces HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de materias obtenida exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de paginación inválidos (page < 0 o size fuera de 1..100)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public PageResponse<EntityModel<MateriaResponse>> consultar(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(name = "page", defaultValue = Paginacion.PAGINA_POR_DEFECTO) int page,
            @Parameter(description = "Cantidad de registros por página (entre 1 y 100)",
                    schema = @Schema(defaultValue = "10", minimum = "1", maximum = "100"))
            @RequestParam(name = "size", required = false) Integer size,
            @Parameter(hidden = true)
            @RequestParam(name = "pageSize", required = false) Integer pageSize
    ) {
        return EnlacesHateoas.paginaMaterias(
                materias.consultar(Paginacion.crear(page, size, pageSize))
        );
    }

    @GetMapping("/{codigo}")
    @Operation(
            summary = "Consultar materia por código",
            description = "Busca una materia a partir de su código único e incluye enlaces HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Materia encontrada"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Código de materia inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Materia no encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public EntityModel<MateriaResponse> consultarPorCodigo(
            @Parameter(description = "Código único de la materia", example = "ARQ101")
            @PathVariable("codigo") String codigo
    ) {
        return EnlacesHateoas.materia(materias.consultarPorCodigo(codigo));
    }
}
