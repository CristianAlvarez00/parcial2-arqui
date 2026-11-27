package com.udea.parcialFinal.repository;

import java.util.Optional;

import com.udea.parcialFinal.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByCedula(String cedula);
}
