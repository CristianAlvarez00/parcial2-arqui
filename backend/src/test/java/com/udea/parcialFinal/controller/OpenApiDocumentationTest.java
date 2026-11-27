package com.udea.parcialFinal.controller;

import com.udea.parcialFinal.config.MetricsSecurityConfig;
import com.udea.parcialFinal.config.OpenApiConfig;
import com.udea.parcialFinal.service.EstudianteService;
import com.udea.parcialFinal.service.MateriaService;
import com.udea.parcialFinal.service.NotaService;
import org.junit.jupiter.api.Test;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.configuration.SpringDocHateoasConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({EstudianteController.class, NotaController.class, MateriaController.class})
@Import({OpenApiConfig.class, MetricsSecurityConfig.class})
@ImportAutoConfiguration({
        SpringDocConfiguration.class,
        SpringDocConfigProperties.class,
        SpringDocHateoasConfiguration.class,
        SpringDocWebMvcConfiguration.class,
        SwaggerConfig.class,
        SwaggerUiConfigProperties.class,
        SwaggerUiOAuthProperties.class
})
@TestPropertySource(properties = "metrics.password=test-secret")
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstudianteService estudianteService;

    @MockitoBean
    private NotaService notaService;

    @MockitoBean
    private MateriaService materiaService;

    @Test
    void documentacionOpenApiEsPublicaYDescribeTodosLosEndpoints() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("API de Gestión Académica - Parcial Final Arquisoft"))
                .andExpect(jsonPath("$.paths['/api/v1/estudiantes'].get.summary").value("Listar estudiantes paginados"))
                .andExpect(jsonPath("$.paths['/api/v1/estudiantes'].post.summary").value("Registrar un nuevo estudiante"))
                .andExpect(jsonPath("$.paths['/api/v1/estudiantes/{cedula}'].get.summary")
                        .value("Consultar estudiante por cédula"))
                .andExpect(jsonPath("$.paths['/api/v1/estudiantes/{cedula}/notas'].get.summary")
                        .value("Listar notas de un estudiante"))
                .andExpect(jsonPath("$.paths['/api/v1/estudiantes/{cedula}/notas'].post.summary")
                        .value("Registrar nota a un estudiante"))
                .andExpect(jsonPath("$.paths['/api/v1/estudiantes/{cedula}/notas/{materiaCodigo}'].get.summary")
                        .value("Consultar la nota de un estudiante en una materia"))
                .andExpect(jsonPath("$.paths['/api/v1/materias'].get.summary").value("Listar materias paginadas"))
                .andExpect(jsonPath("$.paths['/api/v1/materias/{codigo}'].get.summary")
                        .value("Consultar materia por código"));
    }

    @Test
    void interfazSwaggerUiEsPublicaSinAutenticacion() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isFound());

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void metricasDeActuatorSiguenProtegidasConCredenciales() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isUnauthorized());
    }
}
