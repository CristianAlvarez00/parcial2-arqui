package com.udea.parcialFinal.controller;

import com.udea.parcialFinal.dto.EstudianteRequest;
import com.udea.parcialFinal.dto.EstudianteResponse;
import com.udea.parcialFinal.dto.PageResponse;
import com.udea.parcialFinal.service.EstudianteService;
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
@RequestMapping("/api/v1/estudiantes")
@Tag(name = "Estudiantes", description = "Registro y consulta de estudiantes")
public class EstudianteController {

    private final EstudianteService estudiantes;

    public EstudianteController(EstudianteService estudiantes) {
        this.estudiantes = estudiantes;
    }

    @GetMapping
    @Operation(
            summary = "Listar estudiantes paginados",
            description = "Obtiene una página de estudiantes ordenados por identificador e incluye enlaces HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de estudiantes obtenida exitosamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de paginación inválidos (page < 0 o size fuera de 1..100)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public PageResponse<EntityModel<EstudianteResponse>> consultar(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(name = "page", defaultValue = Paginacion.PAGINA_POR_DEFECTO) int page,
            @Parameter(description = "Cantidad de registros por página (entre 1 y 100)",
                    schema = @Schema(defaultValue = "10", minimum = "1", maximum = "100"))
            @RequestParam(name = "size", required = false) Integer size,
            @Parameter(hidden = true)
            @RequestParam(name = "pageSize", required = false) Integer pageSize
    ) {
        return EnlacesHateoas.paginaEstudiantes(
                estudiantes.consultar(Paginacion.crear(page, size, pageSize))
        );
    }

    @GetMapping("/{cedula}")
    @Operation(
            summary = "Consultar estudiante por cédula",
            description = "Busca un estudiante a partir de su número de cédula y retorna sus enlaces HATEOAS."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato de cédula inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Estudiante no encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public EntityModel<EstudianteResponse> consultarPorCedula(
            @Parameter(description = "Número de cédula del estudiante (solo dígitos)", example = "1001234567")
            @PathVariable("cedula") String cedula
    ) {
        return EnlacesHateoas.estudiante(estudiantes.consultarPorCedula(cedula));
    }

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo estudiante",
            description = "Crea un estudiante validando campos obligatorios y que la cédula no esté duplicada."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Estudiante registrado exitosamente",
                    headers = @Header(
                            name = "Location",
                            description = "URI del recurso del estudiante recién creado",
                            schema = @Schema(type = "string")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos del estudiante inválidos o incompletos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "La cédula ya se encuentra registrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public ResponseEntity<EntityModel<EstudianteResponse>> registrar(@RequestBody EstudianteRequest solicitud) {
        EntityModel<EstudianteResponse> recurso = EnlacesHateoas.estudiante(estudiantes.registrar(solicitud));
        return ResponseEntity
                .created(recurso.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(recurso);
    }
}
