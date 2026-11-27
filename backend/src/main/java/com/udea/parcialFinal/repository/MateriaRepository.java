package com.udea.parcialFinal.repository;

import java.util.Optional;

import com.udea.parcialFinal.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaRepository extends JpaRepository<Materia, Long> {

    Optional<Materia> findByCodigo(String codigo);
}
