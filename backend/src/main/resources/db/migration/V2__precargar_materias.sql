INSERT INTO tbla_materia (codigo, nombre) VALUES
    ('ARQ01', 'Arquitectura de Software'),
    ('BD01', 'Bases de Datos'),
    ('ED01', 'Estructuras de Datos'),
    ('IS01', 'Ingeniería de Software'),
    ('RC01', 'Redes de Computadores')
ON CONFLICT (codigo) DO NOTHING;
