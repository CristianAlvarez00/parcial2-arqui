package com.udea.parcialFinal.controller;

import com.udea.parcialFinal.dto.EstudianteResponse;
import com.udea.parcialFinal.dto.MateriaResponse;
import com.udea.parcialFinal.dto.NotaResponse;
import com.udea.parcialFinal.dto.PageResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

final class EnlacesHateoas {

    private EnlacesHateoas() {
    }

    static EntityModel<EstudianteResponse> estudiante(EstudianteResponse estudiante) {
        String cedula = estudiante.cedula();
        return EntityModel.of(
                estudiante,
                linkTo(EstudianteController.class).slash(cedula).withSelfRel(),
                enlacePaginado(
                        linkTo(NotaController.class, cedula),
                        0, Paginacion.TAMANO_POR_DEFECTO, LinkRelation.of("notas")
                ),
                linkTo(NotaController.class, cedula).withRel("registrar-nota"),
                enlacePaginado(
                        linkTo(EstudianteController.class),
                        0, Paginacion.TAMANO_POR_DEFECTO, LinkRelation.of("estudiantes")
                ),
                enlacePaginado(
                        linkTo(MateriaController.class),
                        0, Paginacion.TAMANO_POR_DEFECTO, LinkRelation.of("materias")
                )
        );
    }

    static PageResponse<EntityModel<EstudianteResponse>> paginaEstudiantes(
            PageResponse<EstudianteResponse> pagina
    ) {
        PageResponse<EntityModel<EstudianteResponse>> respuesta = pagina.map(EnlacesHateoas::estudiante);
        agregarEnlacesPaginacion(respuesta, linkTo(EstudianteController.class));
        respuesta.add(linkTo(EstudianteController.class).withRel("registrar"));
        respuesta.add(enlacePaginado(
                linkTo(MateriaController.class),
                0, Paginacion.TAMANO_POR_DEFECTO, LinkRelation.of("materias")
        ));
        return respuesta;
    }

    static EntityModel<NotaResponse> nota(NotaResponse nota) {
        String cedula = nota.cedula();
        return EntityModel.of(
                nota,
                linkTo(NotaController.class, cedula).slash(nota.materiaCodigo()).withSelfRel(),
                enlacePaginado(
                        linkTo(NotaController.class, cedula),
                        0, Paginacion.TAMANO_POR_DEFECTO, LinkRelation.of("notas")
                ),
                linkTo(EstudianteController.class).slash(cedula).withRel("estudiante"),
                linkTo(MateriaController.class).slash(nota.materiaCodigo()).withRel("materia"),
                enlacePaginado(
                        linkTo(EstudianteController.class),
                        0, Paginacion.TAMANO_POR_DEFECTO, LinkRelation.of("estudiantes")
                ),
                enlacePaginado(
                        linkTo(MateriaController.class),
                        0, Paginacion.TAMANO_POR_DEFECTO, LinkRelation.of("materias")
                )
        );
    }

    static PageResponse<EntityModel<NotaResponse>> paginaNotas(
            String cedula, PageResponse<NotaResponse> pagina
    ) {
        String cedulaLimpia = cedula == null ? "" : cedula.strip();
        PageResponse<EntityModel<NotaResponse>> respuesta = pagina.map(EnlacesHateoas::nota);
        agregarEnlacesPaginacion(respuesta, linkTo(NotaController.class, cedulaLimpia));
        respuesta.add(linkTo(NotaController.class, cedulaLimpia).withRel("registrar"));
        respuesta.add(linkTo(EstudianteController.class).slash(cedulaLimpia).withRel("estudiante"));
        respuesta.add(enlacePaginado(
                linkTo(EstudianteController.class),
                0, Paginacion.TAMANO_POR_DEFECTO, LinkRelation.of("estudiantes")
        ));
        respuesta.add(enlacePaginado(
                linkTo(MateriaController.class),
                0, Paginacion.TAMANO_POR_DEFECTO, LinkRelation.of("materias")
        ));
        return respuesta;
    }

    static EntityModel<MateriaResponse> materia(MateriaResponse materia) {
        return EntityModel.of(
                materia,
                linkTo(MateriaController.class).slash(materia.codigo()).withSelfRel(),
                enlacePaginado(
                        linkTo(MateriaController.class),
                        0, Paginacion.TAMANO_POR_DEFECTO, LinkRelation.of("materias")
                ),
                enlacePaginado(
                        linkTo(EstudianteController.class),
                        0, Paginacion.TAMANO_POR_DEFECTO, LinkRelation.of("estudiantes")
                )
        );
    }

    static PageResponse<EntityModel<MateriaResponse>> paginaMaterias(
            PageResponse<MateriaResponse> pagina
    ) {
        PageResponse<EntityModel<MateriaResponse>> respuesta = pagina.map(EnlacesHateoas::materia);
        agregarEnlacesPaginacion(respuesta, linkTo(MateriaController.class));
        respuesta.add(enlacePaginado(
                linkTo(EstudianteController.class),
                0, Paginacion.TAMANO_POR_DEFECTO, LinkRelation.of("estudiantes")
        ));
        return respuesta;
    }

    private static void agregarEnlacesPaginacion(PageResponse<?> pagina, WebMvcLinkBuilder base) {
        int actual = pagina.getPage();
        int tamano = pagina.getSize();
        int ultima = Math.max(0, pagina.getTotalPages() - 1);

        pagina.add(enlacePaginado(base, actual, tamano, IanaLinkRelations.SELF));
        pagina.add(enlacePaginado(base, 0, tamano, IanaLinkRelations.FIRST));
        if (actual > 0) {
            pagina.add(enlacePaginado(base, actual - 1, tamano, IanaLinkRelations.PREV));
        }
        if (!pagina.isLast() && pagina.getTotalPages() > 0) {
            pagina.add(enlacePaginado(base, actual + 1, tamano, IanaLinkRelations.NEXT));
        }
        pagina.add(enlacePaginado(base, ultima, tamano, IanaLinkRelations.LAST));
    }

    private static Link enlacePaginado(WebMvcLinkBuilder base, int page, int size, LinkRelation rel) {
        String uri = base.toUriComponentsBuilder()
                .queryParam("page", page)
                .queryParam("size", size)
                .build()
                .toUriString();
        return Link.of(uri, rel);
    }
}
