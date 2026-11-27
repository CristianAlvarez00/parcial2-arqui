package com.udea.parcialFinal.repository;

import java.util.Optional;

import com.udea.parcialFinal.model.Matricula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    @EntityGraph(attributePaths = "materia")
    Page<Matricula> findByEstudiante_IdOrderByIdAsc(Long estudianteId, Pageable pageable);

    @EntityGraph(attributePaths = "materia")
    Optional<Matricula> findByEstudiante_IdAndMateria_Codigo(Long estudianteId, String codigo);
}
