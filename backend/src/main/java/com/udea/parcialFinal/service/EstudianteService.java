package com.udea.parcialFinal.service;

import com.udea.parcialFinal.dto.EstudianteRequest;
import com.udea.parcialFinal.dto.EstudianteResponse;
import com.udea.parcialFinal.dto.PageResponse;
import com.udea.parcialFinal.model.Estudiante;
import com.udea.parcialFinal.repository.EstudianteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EstudianteService {

    private final EstudianteRepository estudiantes;

    public EstudianteService(EstudianteRepository estudiantes) {
        this.estudiantes = estudiantes;
    }

    @Transactional(readOnly = true)
    public PageResponse<EstudianteResponse> consultar(Pageable pagina) {
        Page<EstudianteResponse> resultado = estudiantes.findAllByOrderByIdAsc(pagina)
                .map(this::representarEstudiante);
        return PageResponse.de(resultado);
    }

    @Transactional
    public EstudianteResponse registrar(EstudianteRequest solicitud) {
        Estudiante estudiante = new Estudiante(
                Validacion.texto(solicitud.nombre(), "nombre", 120),
                Validacion.texto(solicitud.apellido(), "apellido", 120),
                Validacion.cedula(solicitud.cedula()),
                Validacion.texto(solicitud.programa(), "programa", 120)
        );
        Estudiante guardado = estudiantes.saveAndFlush(estudiante);
        return representarEstudiante(guardado);
    }

    Estudiante buscarPorCedula(String valor) {
        String cedula = Validacion.cedula(valor);
        return estudiantes.findByCedula(cedula)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado."));
    }

    private EstudianteResponse representarEstudiante(Estudiante estudiante) {
        return new EstudianteResponse(
                estudiante.getId(), estudiante.getNombre(), estudiante.getApellido(),
                estudiante.getCedula(), estudiante.getPrograma()
        );
    }
}
