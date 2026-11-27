WITH estudiantes_nuevos AS (
    INSERT INTO tbla_estudiante (nombre, apellido, cedula, programa) VALUES
        ('Ana', 'Gómez', '990000001', 'Ingeniería de Sistemas'),
        ('Mateo', 'Ruiz', '990000002', 'Ingeniería de Sistemas')
    ON CONFLICT (cedula) DO NOTHING
    RETURNING id, cedula
), notas (cedula, codigo, nota) AS (
    VALUES
        ('990000001', 'ARQ01', 4.50),
        ('990000001', 'BD01', 4.10),
        ('990000001', 'ED01', 4.30),
        ('990000002', 'IS01', 4.20),
        ('990000002', 'RC01', 3.80)
)
INSERT INTO tbla_matricula (id_estudiante, id_materia, nota)
SELECT estudiante.id, materia.id, notas.nota
FROM notas
JOIN estudiantes_nuevos estudiante ON estudiante.cedula = notas.cedula
JOIN tbla_materia materia ON materia.codigo = notas.codigo;
