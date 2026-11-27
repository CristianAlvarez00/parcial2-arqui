package com.udea.parcialFinal.service;

import com.udea.parcialFinal.dto.MateriaResponse;
import com.udea.parcialFinal.dto.PageResponse;
import com.udea.parcialFinal.model.Materia;
import com.udea.parcialFinal.repository.MateriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MateriaService {

    private final MateriaRepository materias;

    public MateriaService(MateriaRepository materias) {
        this.materias = materias;
    }

    @Transactional(readOnly = true)
    public PageResponse<MateriaResponse> consultar(Pageable pagina) {
        Page<MateriaResponse> resultado = materias.findAllByOrderByIdAsc(pagina)
                .map(this::representarMateria);
        return PageResponse.de(resultado);
    }

    @Transactional(readOnly = true)
    public MateriaResponse consultarPorCodigo(String codigo) {
        String codigoValidado = Validacion.texto(codigo, "codigo", 20);
        Materia materia = materias.findByCodigo(codigoValidado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada."));
        return representarMateria(materia);
    }

    private MateriaResponse representarMateria(Materia materia) {
        return new MateriaResponse(materia.getId(), materia.getCodigo(), materia.getNombre());
    }
}
