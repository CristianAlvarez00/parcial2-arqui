package com.udea.parcialFinal.repository;

import java.util.Optional;

import com.udea.parcialFinal.model.Estudiante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByCedula(String cedula);

    Page<Estudiante> findAllByOrderByIdAsc(Pageable pageable);
}
