package com.udea.parcialFinal.service;

import com.udea.parcialFinal.dto.MateriaResponse;
import com.udea.parcialFinal.dto.PageResponse;
import com.udea.parcialFinal.model.Materia;
import com.udea.parcialFinal.repository.MateriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private MateriaResponse representarMateria(Materia materia) {
        return new MateriaResponse(materia.getId(), materia.getCodigo(), materia.getNombre());
    }
}
