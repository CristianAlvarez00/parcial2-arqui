package com.udea.parcialFinal.controller;

import com.udea.parcialFinal.dto.NotaRequest;
import com.udea.parcialFinal.dto.NotaResponse;
import com.udea.parcialFinal.dto.PageResponse;
import com.udea.parcialFinal.service.NotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/estudiantes/{cedula}/notas")
@Tag(name = "Notas", description = "Registro y consulta paginada de notas por estudiante")
public class NotaController {

    private final NotaService notas;

    public NotaController(NotaService notas) {
        this.notas = notas;
    }

    @GetMapping
    @Operation(
            summary = "Listar notas de un estudiante",
            description = "Consulta de forma paginada las calificaciones de un estudiante por su cédula "
                    + "(con soporte de caché en Redis y enlaces HATEOAS)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de notas obtenida exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cédula o parámetros de paginación inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Estudiante no encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public PageResponse<EntityModel<NotaResponse>> consultar(
            @Parameter(description = "Número de cédula del estudiante (solo dígitos)", example = "1001234567")
            @PathVariable("cedula") String cedula,
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(name = "page", defaultValue = Paginacion.PAGINA_POR_DEFECTO) int page,
            @Parameter(description = "Cantidad de registros por página (entre 1 y 100)",
                    schema = @Schema(defaultValue = "10", minimum = "1", maximum = "100"))
            @RequestParam(name = "size", required = false) Integer size,
            @Parameter(hidden = true)
            @RequestParam(name = "pageSize", required = false) Integer pageSize
    ) {
        return EnlacesHateoas.paginaNotas(
                cedula, notas.consultar(cedula, Paginacion.crear(page, size, pageSize))
        );
    }

    @GetMapping("/{materiaCodigo}")
    @Operation(
            summary = "Consultar la nota de un estudiante en una materia",
            description = "Retorna la calificación del estudiante en la materia indicada, con sus enlaces HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nota encontrada"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato de cédula inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Estudiante no encontrado o sin nota en la materia",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public EntityModel<NotaResponse> consultarPorMateria(
            @Parameter(description = "Número de cédula del estudiante (solo dígitos)", example = "1001234567")
            @PathVariable("cedula") String cedula,
            @Parameter(description = "Código de la materia", example = "ARQ01")
            @PathVariable("materiaCodigo") String materiaCodigo
    ) {
        return EnlacesHateoas.nota(notas.consultarPorMateria(cedula, materiaCodigo));
    }

    @PostMapping
    @Operation(
            summary = "Registrar nota a un estudiante",
            description = "Registra la calificación de un estudiante en una materia (entre 0.00 y 5.00, "
                    + "máximo dos decimales) e invalida las entradas de caché asociadas."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Nota registrada exitosamente",
                    headers = @Header(
                            name = "Location",
                            description = "URI de la nota registrada",
                            schema = @Schema(type = "string")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o nota fuera del rango 0..5 con hasta dos decimales",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Estudiante o materia no encontrados",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El estudiante ya tiene una nota registrada para esta materia",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<EntityModel<NotaResponse>> registrar(
            @Parameter(description = "Número de cédula del estudiante (solo dígitos)", example = "1001234567")
            @PathVariable("cedula") String cedula,
            @RequestBody NotaRequest solicitud
    ) {
        EntityModel<NotaResponse> recurso = EnlacesHateoas.nota(notas.registrar(cedula, solicitud));
        return ResponseEntity
                .created(recurso.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(recurso);
    }
}
