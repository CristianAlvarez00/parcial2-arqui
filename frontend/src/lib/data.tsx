"use client";

import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { api, type Estudiante, type Materia, type Nota } from "@/lib/api";

// El backend no tiene "estudiantes de una materia" ni filtros, así que se
// cargan estudiantes, materias y las notas de cada estudiante una sola vez y
// todo lo demás se deriva en el cliente.

interface Datos {
  estudiantes: Estudiante[];
  materias: Materia[];
  /** Notas por cédula. */
  notasPorCedula: Map<string, Nota[]>;
}

type Estado =
  | { tipo: "cargando" }
  | { tipo: "error"; mensaje: string }
  | { tipo: "listo"; datos: Datos };

interface ContextoDatos {
  estado: Estado;
  recargar: () => Promise<void>;
  /** Registra la nota y actualiza el estado local sin recargar todo. */
  registrarNota: (cedula: string, materiaCodigo: string, nota: number) => Promise<Nota>;
}

const Contexto = createContext<ContextoDatos | null>(null);

// Limita las peticiones simultáneas al traer las notas de cada estudiante.
async function enLotes<T, R>(items: T[], tamano: number, fn: (item: T) => Promise<R>): Promise<R[]> {
  const resultados: R[] = [];
  for (let i = 0; i < items.length; i += tamano) {
    resultados.push(...(await Promise.all(items.slice(i, i + tamano).map(fn))));
  }
  return resultados;
}

async function cargar(): Promise<Datos> {
  const [estudiantes, materias] = await Promise.all([api.estudiantes(), api.materias()]);
  const notas = await enLotes(estudiantes, 8, (e) => api.notas(e.cedula));
  const notasPorCedula = new Map(estudiantes.map((e, i) => [e.cedula, notas[i]]));
  return { estudiantes, materias, notasPorCedula };
}

export function DataProvider({ children }: { children: React.ReactNode }) {
  const [estado, setEstado] = useState<Estado>({ tipo: "cargando" });

  const recargar = useCallback(async () => {
    setEstado({ tipo: "cargando" });
    try {
      setEstado({ tipo: "listo", datos: await cargar() });
    } catch (error) {
      setEstado({ tipo: "error", mensaje: error instanceof Error ? error.message : "Error inesperado." });
    }
  }, []);

  useEffect(() => {
    let activo = true;
    cargar()
      .then((datos) => activo && setEstado({ tipo: "listo", datos }))
      .catch(
        (error) =>
          activo &&
          setEstado({ tipo: "error", mensaje: error instanceof Error ? error.message : "Error inesperado." }),
      );
    return () => {
      activo = false;
    };
  }, []);

  const registrarNota = useCallback(async (cedula: string, materiaCodigo: string, nota: number) => {
    const creada = await api.registrarNota(cedula, materiaCodigo, nota);
    setEstado((previo) => {
      if (previo.tipo !== "listo") return previo;
      const notasPorCedula = new Map(previo.datos.notasPorCedula);
      notasPorCedula.set(cedula, [...(notasPorCedula.get(cedula) ?? []), creada]);
      return { tipo: "listo", datos: { ...previo.datos, notasPorCedula } };
    });
    return creada;
  }, []);

  const valor = useMemo(() => ({ estado, recargar, registrarNota }), [estado, recargar, registrarNota]);
  return <Contexto.Provider value={valor}>{children}</Contexto.Provider>;
}

export function useDatos(): ContextoDatos {
  const contexto = useContext(Contexto);
  if (!contexto) throw new Error("useDatos debe usarse dentro de DataProvider");
  return contexto;
}

export function nombreCompleto(e: Estudiante): string {
  return `${e.nombre} ${e.apellido}`;
}

/** Compara textos ignorando mayúsculas y tildes. */
export function normalizar(texto: string): string {
  return texto.normalize("NFD").replace(/\p{Diacritic}/gu, "").toLowerCase().trim();
}
