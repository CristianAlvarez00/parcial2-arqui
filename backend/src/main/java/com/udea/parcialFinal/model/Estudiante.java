package com.udea.parcialFinal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tbla_estudiante", uniqueConstraints =
        @UniqueConstraint(name = "uq_estudiante_cedula", columnNames = "cedula"))
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 120)
    private String apellido;

    @Column(nullable = false, length = 20)
    private String cedula;

    @Column(nullable = false, length = 120)
    private String programa;

    protected Estudiante() {
    }

    public Estudiante(String nombre, String apellido, String cedula, String programa) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.programa = programa;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getCedula() {
        return cedula;
    }

    public String getPrograma() {
        return programa;
    }
}
