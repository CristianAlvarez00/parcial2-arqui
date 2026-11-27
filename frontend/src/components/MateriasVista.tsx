"use client";

import { useMemo, useState } from "react";
import Accordion from "@mui/material/Accordion";
import AccordionDetails from "@mui/material/AccordionDetails";
import AccordionSummary from "@mui/material/AccordionSummary";
import Typography from "@mui/material/Typography";
import ExpandMoreRounded from "@mui/icons-material/ExpandMoreRounded";
import AgregarNota from "@/components/AgregarNota";
import { Buscador, FiltroEstado, MarcoListado, usePaginacion } from "@/components/Listado";
import { Codigo, ConDatos, NotaChip } from "@/components/ui";
import type { Estudiante, Materia } from "@/lib/api";
import { nombreCompleto, normalizar, useDatos } from "@/lib/data";
import { formatearNota, promedio } from "@/lib/nota";

type FiltroInscritos = "todas" | "con" | "sin";

interface Inscrito {
  estudiante: Estudiante;
  nota: number;
}

export default function MateriasVista() {
  return (
    <ConDatos>
      <Contenido />
    </ConDatos>
  );
}

const VACIO: never[] = [];

function Contenido() {
  const { estado } = useDatos();
  const [busqueda, setBusqueda] = useState("");
  const [filtro, setFiltro] = useState<FiltroInscritos>("todas");

  const datos = estado.tipo === "listo" ? estado.datos : null;

  // No hay endpoint de "estudiantes por materia": se arma a partir de las notas de cada estudiante.
  const inscritosPorMateria = useMemo(() => {
    const mapa = new Map<string, Inscrito[]>();
    if (!datos) return mapa;
    for (const estudiante of datos.estudiantes) {
      for (const n of datos.notasPorCedula.get(estudiante.cedula) ?? []) {
        const lista = mapa.get(n.materiaCodigo) ?? [];
        lista.push({ estudiante, nota: n.nota });
        mapa.set(n.materiaCodigo, lista);
      }
    }
    return mapa;
  }, [datos]);

  const materias = datos?.materias ?? VACIO;
  const filtradas = useMemo(() => {
    const q = normalizar(busqueda);
    return materias.filter((m) => {
      const cantidad = inscritosPorMateria.get(m.codigo)?.length ?? 0;
      if (filtro === "con" && cantidad === 0) return false;
      if (filtro === "sin" && cantidad > 0) return false;
      return !q || normalizar(`${m.codigo} ${m.nombre}`).includes(q);
    });
  }, [materias, inscritosPorMateria, busqueda, filtro]);

  const { visibles, pagina, totalPaginas, setPagina } = usePaginacion(filtradas, `${busqueda}|${filtro}`);

  if (!datos) return null;

  return (
    <MarcoListado
      total={filtradas.length}
      totalSinFiltrar={materias.length}
      sustantivo={["materia", "materias"]}
      pagina={pagina}
      totalPaginas={totalPaginas}
      onPagina={setPagina}
      filtros={
        <>
          <Buscador
            className="flex-1"
            valor={busqueda}
            onChange={setBusqueda}
            etiqueta="Buscar materias"
            placeholder="Buscar por código o nombre"
          />
          <FiltroEstado
            etiqueta="Filtrar por estudiantes"
            valor={filtro}
            onChange={setFiltro}
            opciones={[
              { valor: "todas", texto: "Todas" },
              { valor: "con", texto: "Con estudiantes" },
              { valor: "sin", texto: "Sin estudiantes" },
            ]}
          />
        </>
      }
    >
      {visibles.length === 0 ? (
        <Typography color="text.secondary" className="px-4 py-10 text-center">
          Ninguna materia coincide con los filtros.
        </Typography>
      ) : (
        visibles.map((m) => (
          <FilaMateria
            key={m.codigo}
            materia={m}
            inscritos={inscritosPorMateria.get(m.codigo) ?? []}
            estudiantes={datos.estudiantes}
          />
        ))
      )}
    </MarcoListado>
  );
}

function FilaMateria({
  materia,
  inscritos,
  estudiantes,
}: {
  materia: Materia;
  inscritos: Inscrito[];
  estudiantes: Estudiante[];
}) {
  const { registrarNota } = useDatos();
  const [busqueda, setBusqueda] = useState("");
  const prom = promedio(inscritos.map((i) => i.nota));

  const q = normalizar(busqueda);
  const visibles = q
    ? inscritos.filter((i) => normalizar(`${nombreCompleto(i.estudiante)} ${i.estudiante.cedula}`).includes(q))
    : inscritos;

  const conNota = new Set(inscritos.map((i) => i.estudiante.cedula));
  const pendientes = estudiantes
    .filter((e) => !conNota.has(e.cedula))
    .map((e) => ({ id: e.cedula, etiqueta: `${nombreCompleto(e)} · CC ${e.cedula}` }));

  return (
    <Accordion slotProps={{ transition: { unmountOnExit: true } }}>
      <AccordionSummary expandIcon={<ExpandMoreRounded />} sx={{ px: 2 }}>
        <div className="flex w-full min-w-0 items-center gap-3 pr-2">
          <Codigo>{materia.codigo}</Codigo>
          <Typography className="min-w-0 flex-1 truncate" sx={{ fontWeight: 600 }}>
            {materia.nombre}
          </Typography>
          <div className="hidden text-right sm:block">
            <Typography variant="body2" color="text.secondary">
              {inscritos.length} {inscritos.length === 1 ? "estudiante" : "estudiantes"}
            </Typography>
            <Typography variant="body2" sx={{ fontWeight: 600, fontVariantNumeric: "tabular-nums" }}>
              {prom === null ? "Sin notas" : `Promedio ${formatearNota(prom)}`}
            </Typography>
          </div>
        </div>
      </AccordionSummary>
      <AccordionDetails sx={{ px: 2, pb: 2, bgcolor: "action.hover" }}>
        <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <Typography variant="overline" color="text.secondary">
            Estudiantes matriculados
          </Typography>
          {inscritos.length > 0 && (
            <Buscador
              size="small"
              className="sm:w-72"
              valor={busqueda}
              onChange={setBusqueda}
              etiqueta={`Buscar estudiantes en ${materia.nombre}`}
              placeholder="Buscar estudiante"
            />
          )}
        </div>
        {inscritos.length === 0 ? (
          <Typography variant="body2" color="text.secondary" className="py-2">
            Aún no hay estudiantes con nota en esta materia.
          </Typography>
        ) : visibles.length === 0 ? (
          <Typography variant="body2" color="text.secondary" className="py-2">
            Ningún estudiante coincide con “{busqueda}”.
          </Typography>
        ) : (
          <ul className="mt-1 divide-y divide-[var(--mui-palette-divider)]">
            {visibles.map(({ estudiante, nota }) => (
              <li key={estudiante.cedula} className="flex items-center gap-3 py-2.5">
                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-medium">{nombreCompleto(estudiante)}</p>
                  <p className="truncate text-xs text-[var(--mui-palette-text-secondary)]">
                    CC {estudiante.cedula} · {estudiante.programa}
                  </p>
                </div>
                <NotaChip nota={nota} />
              </li>
            ))}
          </ul>
        )}
        <AgregarNota
          etiquetaOpcion="Estudiante"
          opciones={pendientes}
          textoSinOpciones="Todos los estudiantes ya tienen nota en esta materia."
          onGuardar={async (cedula, nota) => {
            await registrarNota(cedula, materia.codigo, nota);
          }}
        />
      </AccordionDetails>
    </Accordion>
  );
}
