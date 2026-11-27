package com.udea.parcialFinal.controller;

import java.math.BigDecimal;
import java.util.List;

import com.udea.parcialFinal.config.MetricsSecurityConfig;
import com.udea.parcialFinal.config.OpenApiConfig;
import com.udea.parcialFinal.dto.EstudianteRequest;
import com.udea.parcialFinal.dto.EstudianteResponse;
import com.udea.parcialFinal.dto.MateriaResponse;
import com.udea.parcialFinal.dto.NotaRequest;
import com.udea.parcialFinal.dto.NotaResponse;
import com.udea.parcialFinal.dto.PageResponse;
import com.udea.parcialFinal.service.EstudianteService;
import com.udea.parcialFinal.service.MateriaService;
import com.udea.parcialFinal.service.NotaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({EstudianteController.class, NotaController.class, MateriaController.class})
@Import({OpenApiConfig.class, MetricsSecurityConfig.class})
@TestPropertySource(properties = "metrics.password=test-secret")
class PaginacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstudianteService estudianteService;

    @MockitoBean
    private NotaService notaService;

    @MockitoBean
    private MateriaService materiaService;

    @Test
    void consultaEstudiantesConPaginacionYEnlacesHateoas() throws Exception {
        PageRequest pageable = PageRequest.of(0, 10);
        EstudianteResponse estudiante = new EstudianteResponse(1L, "Ana", "Gómez", "1001", "Ingeniería de Sistemas");
        PageResponse<EstudianteResponse> respuesta = PageResponse.de(new PageImpl<>(List.of(estudiante), pageable, 1));
        when(estudianteService.consultar(pageable)).thenReturn(respuesta);

        mockMvc.perform(get("/api/v1/estudiantes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cedula").value("1001"))
                .andExpect(jsonPath("$.content[0]._links.self.href", endsWith("/api/v1/estudiantes/1001")))
                .andExpect(jsonPath("$.content[0]._links.notas.href",
                        endsWith("/api/v1/estudiantes/1001/notas?page=0&size=10")))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/v1/estudiantes?page=0&size=10")))
                .andExpect(jsonPath("$._links.first.href", endsWith("/api/v1/estudiantes?page=0&size=10")))
                .andExpect(jsonPath("$._links.last.href", endsWith("/api/v1/estudiantes?page=0&size=10")))
                .andExpect(jsonPath("$._links.materias.href", endsWith("/api/v1/materias?page=0&size=10")));

        verify(estudianteService).consultar(pageable);
    }

    @Test
    void consultaNotasConPageSizeYEnlacesPrevLast() throws Exception {
        PageRequest pageable = PageRequest.of(1, 5);
        NotaResponse nota = new NotaResponse(6L, "1001", "ARQ101", "Arquitectura de Software", new BigDecimal("4.50"));
        PageResponse<NotaResponse> respuesta = PageResponse.de(new PageImpl<>(List.of(nota), pageable, 6));
        when(notaService.consultar("1001", pageable)).thenReturn(respuesta);

        mockMvc.perform(get("/api/v1/estudiantes/1001/notas")
                        .param("page", "1")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].materiaCodigo").value("ARQ101"))
                .andExpect(jsonPath("$.content[0]._links.estudiante.href", endsWith("/api/v1/estudiantes/1001")))
                .andExpect(jsonPath("$.content[0]._links.materia.href", endsWith("/api/v1/materias/ARQ101")))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/v1/estudiantes/1001/notas?page=1&size=5")))
                .andExpect(jsonPath("$._links.prev.href", endsWith("/api/v1/estudiantes/1001/notas?page=0&size=5")))
                .andExpect(jsonPath("$._links.last.href", endsWith("/api/v1/estudiantes/1001/notas?page=1&size=5")))
                .andExpect(jsonPath("$._links.estudiante.href", endsWith("/api/v1/estudiantes/1001")));

        verify(notaService).consultar("1001", pageable);
    }

    @Test
    void consultaMateriasConEnlaceNextYParametroPageSize() throws Exception {
        PageRequest pageable = PageRequest.of(0, 3);
        MateriaResponse materia = new MateriaResponse(2L, "BD201", "Bases de Datos");
        PageResponse<MateriaResponse> respuesta = PageResponse.de(new PageImpl<>(List.of(materia), pageable, 5));
        when(materiaService.consultar(pageable)).thenReturn(respuesta);

        mockMvc.perform(get("/api/v1/materias")
                        .param("page", "0")
                        .param("pageSize", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].codigo").value("BD201"))
                .andExpect(jsonPath("$.content[0]._links.self.href", endsWith("/api/v1/materias/BD201")))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/v1/materias?page=0&size=3")))
                .andExpect(jsonPath("$._links.next.href", endsWith("/api/v1/materias?page=1&size=3")))
                .andExpect(jsonPath("$._links.last.href", endsWith("/api/v1/materias?page=1&size=3")))
                .andExpect(jsonPath("$._links.estudiantes.href", endsWith("/api/v1/estudiantes?page=0&size=10")));

        verify(materiaService).consultar(pageable);
    }

    @Test
    void endpointsPostYDetalleIncluyenEnlacesHateoas() throws Exception {
        EstudianteRequest reqEst = new EstudianteRequest("Ana", "Gómez", "1001", "Ingeniería de Sistemas");
        EstudianteResponse respEst = new EstudianteResponse(1L, "Ana", "Gómez", "1001", "Ingeniería de Sistemas");
        when(estudianteService.registrar(reqEst)).thenReturn(respEst);
        when(estudianteService.consultarPorCedula("1001")).thenReturn(respEst);

        NotaRequest reqNota = new NotaRequest("ARQ101", new BigDecimal("4.80"));
        NotaResponse respNota = new NotaResponse(10L, "1001", "ARQ101", "Arquitectura de Software", new BigDecimal("4.80"));
        when(notaService.registrar("1001", reqNota)).thenReturn(respNota);
        when(notaService.consultarPorMateria("1001", "ARQ101")).thenReturn(respNota);

        MateriaResponse respMat = new MateriaResponse(2L, "ARQ101", "Arquitectura de Software");
        when(materiaService.consultarPorCodigo("ARQ101")).thenReturn(respMat);

        mockMvc.perform(post("/api/v1/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","apellido":"Gómez","cedula":"1001","programa":"Ingeniería de Sistemas"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v1/estudiantes/1001")))
                .andExpect(jsonPath("$.cedula").value("1001"))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/v1/estudiantes/1001")))
                .andExpect(jsonPath("$._links.notas.href", endsWith("/api/v1/estudiantes/1001/notas?page=0&size=10")));

        mockMvc.perform(post("/api/v1/estudiantes/1001/notas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {"materiaCodigo":"ARQ101","nota":4.80}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/v1/estudiantes/1001/notas/ARQ101")))
                .andExpect(jsonPath("$.materiaCodigo").value("ARQ101"))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/v1/estudiantes/1001/notas/ARQ101")))
                .andExpect(jsonPath("$._links.notas.href", endsWith("/api/v1/estudiantes/1001/notas?page=0&size=10")))
                .andExpect(jsonPath("$._links.estudiante.href", endsWith("/api/v1/estudiantes/1001")))
                .andExpect(jsonPath("$._links.materia.href", endsWith("/api/v1/materias/ARQ101")));

        mockMvc.perform(get("/api/v1/estudiantes/1001").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cedula").value("1001"))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/v1/estudiantes/1001")));

        mockMvc.perform(get("/api/v1/estudiantes/1001/notas/ARQ101").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nota").value(4.80))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/v1/estudiantes/1001/notas/ARQ101")));

        mockMvc.perform(get("/api/v1/materias/ARQ101").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("ARQ101"))
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/v1/materias/ARQ101")));
    }

    @Test
    void rechazaParametrosDePaginacionInvalidos() throws Exception {
        mockMvc.perform(get("/api/v1/estudiantes").param("page", "-1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/materias").param("size", "0"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/estudiantes/1001/notas").param("pageSize", "101"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/v1/materias").param("size", "5").param("pageSize", "10"))
                .andExpect(status().isBadRequest());
    }
}
