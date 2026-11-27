package com.udea.parcialFinal.service;

import java.math.BigDecimal;

import com.udea.parcialFinal.dto.NotaRequest;
import com.udea.parcialFinal.dto.NotaResponse;
import com.udea.parcialFinal.dto.PageResponse;
import com.udea.parcialFinal.model.Estudiante;
import com.udea.parcialFinal.model.Materia;
import com.udea.parcialFinal.model.Matricula;
import com.udea.parcialFinal.repository.MateriaRepository;
import com.udea.parcialFinal.repository.MatriculaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NotaService {

    private final EstudianteService estudiantes;
    private final MateriaRepository materias;
    private final MatriculaRepository matriculas;

    public NotaService(EstudianteService estudiantes, MateriaRepository materias, MatriculaRepository matriculas) {
        this.estudiantes = estudiantes;
        this.materias = materias;
        this.matriculas = matriculas;
    }

    @Transactional
    public NotaResponse registrar(String cedula, NotaRequest solicitud) {
        String codigoMateria = Validacion.texto(solicitud.materiaCodigo(), "materiaCodigo", 20);
        BigDecimal nota = Validacion.nota(solicitud.nota());
        Estudiante estudiante = estudiantes.buscarPorCedula(cedula);
        Materia materia = materias.findByCodigo(codigoMateria)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada."));

        Matricula guardada = matriculas.saveAndFlush(new Matricula(estudiante, materia, nota));
        return representarNota(guardada, estudiante.getCedula());
    }

    @Transactional(readOnly = true)
    public PageResponse<NotaResponse> consultar(String cedula, Pageable pagina) {
        Estudiante estudiante = estudiantes.buscarPorCedula(cedula);
        Page<NotaResponse> resultado = matriculas.findByEstudiante_IdOrderByIdAsc(estudiante.getId(), pagina)
                .map(matricula -> representarNota(matricula, estudiante.getCedula()));
        return PageResponse.de(resultado);
    }

    private NotaResponse representarNota(Matricula matricula, String cedula) {
        Materia materia = matricula.getMateria();
        return new NotaResponse(matricula.getId(), cedula, materia.getCodigo(), materia.getNombre(), matricula.getNota());
    }
}
