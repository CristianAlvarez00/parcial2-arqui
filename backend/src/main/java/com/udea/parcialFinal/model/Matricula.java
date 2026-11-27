package com.udea.parcialFinal.model;

import java.math.BigDecimal;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "tbla_matricula",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_matricula_estudiante_materia",
                columnNames = {"id_estudiante", "id_materia"}
        ),
        check = @CheckConstraint(name = "ck_matricula_nota", constraint = "nota BETWEEN 0 AND 5")
)
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal nota;

    protected Matricula() {
    }

    public Matricula(Estudiante estudiante, Materia materia, BigDecimal nota) {
        this.estudiante = estudiante;
        this.materia = materia;
        this.nota = nota;
    }

    public Long getId() {
        return id;
    }

    public Materia getMateria() {
        return materia;
    }

    public BigDecimal getNota() {
        return nota;
    }
}
