package com.udea.parcialFinal.controller;

import java.math.BigDecimal;
import java.util.List;

import com.udea.parcialFinal.dto.EstudianteResponse;
import com.udea.parcialFinal.dto.MateriaResponse;
import com.udea.parcialFinal.dto.NotaResponse;
import com.udea.parcialFinal.dto.PageResponse;
import com.udea.parcialFinal.service.EstudianteService;
import com.udea.parcialFinal.service.MateriaService;
import com.udea.parcialFinal.service.NotaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaginacionControllerTest {

    @Mock
    private EstudianteService estudianteService;

    @Mock
    private NotaService notaService;

    @Mock
    private MateriaService materiaService;

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new EstudianteController(estudianteService),
                        new NotaController(notaService),
                        new MateriaController(materiaService)
                )
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void consultaEstudiantesConPaginacionPorDefecto() throws Exception {
        PageRequest pageable = PageRequest.of(0, 10);
        EstudianteResponse estudiante = new EstudianteResponse(1L, "Ana", "Gómez", "1001", "Ingeniería de Sistemas");
        PageResponse<EstudianteResponse> respuesta = PageResponse.de(new PageImpl<>(List.of(estudiante), pageable, 1));
        when(estudianteService.consultar(pageable)).thenReturn(respuesta);

        mockMvc.perform(get("/api/v1/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cedula").value("1001"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));

        verify(estudianteService).consultar(pageable);
    }

    @Test
    void consultaNotasConPageYSizeExplicitos() throws Exception {
        PageRequest pageable = PageRequest.of(1, 5);
        NotaResponse nota = new NotaResponse(6L, "1001", "ARQ101", "Arquitectura de Software", new BigDecimal("4.50"));
        PageResponse<NotaResponse> respuesta = PageResponse.de(new PageImpl<>(List.of(nota), pageable, 6));
        when(notaService.consultar("1001", pageable)).thenReturn(respuesta);

        mockMvc.perform(get("/api/v1/estudiantes/1001/notas")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].materiaCodigo").value("ARQ101"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));

        verify(notaService).consultar("1001", pageable);
    }

    @Test
    void consultaMateriasAceptandoParametroPageSize() throws Exception {
        PageRequest pageable = PageRequest.of(0, 3);
        MateriaResponse materia = new MateriaResponse(2L, "BD201", "Bases de Datos");
        PageResponse<MateriaResponse> respuesta = PageResponse.de(new PageImpl<>(List.of(materia), pageable, 5));
        when(materiaService.consultar(pageable)).thenReturn(respuesta);

        mockMvc.perform(get("/api/v1/materias")
                        .param("page", "0")
                        .param("pageSize", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].codigo").value("BD201"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        verify(materiaService).consultar(pageable);
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
