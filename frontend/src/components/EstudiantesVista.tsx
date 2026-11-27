"use client";

import { useMemo, useState } from "react";
import Accordion from "@mui/material/Accordion";
import AccordionDetails from "@mui/material/AccordionDetails";
import AccordionSummary from "@mui/material/AccordionSummary";
import Avatar from "@mui/material/Avatar";
import MenuItem from "@mui/material/MenuItem";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import ExpandMoreRounded from "@mui/icons-material/ExpandMoreRounded";
import AgregarNota from "@/components/AgregarNota";
import { Buscador, FiltroEstado, MarcoListado, usePaginacion } from "@/components/Listado";
import { Codigo, ConDatos, NotaChip, SinResultados } from "@/components/ui";
import type { Estudiante, Materia, Nota } from "@/lib/api";
import { nombreCompleto, normalizar, useDatos } from "@/lib/data";
import { formatearNota, promedio } from "@/lib/nota";

type FiltroNotas = "todos" | "con" | "sin";

export default function EstudiantesVista() {
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
  const [programa, setPrograma] = useState("");
  const [filtroNotas, setFiltroNotas] = useState<FiltroNotas>("todos");

  const datos = estado.tipo === "listo" ? estado.datos : null;
  const estudiantes = datos?.estudiantes ?? VACIO;
  const notasPorCedula = datos?.notasPorCedula;

  const programas = useMemo(
    () => [...new Set(estudiantes.map((e) => e.programa))].sort((a, b) => a.localeCompare(b, "es")),
    [estudiantes],
  );

  const filtrados = useMemo(() => {
    const q = normalizar(busqueda);
    return estudiantes.filter((e) => {
      const cantidad = notasPorCedula?.get(e.cedula)?.length ?? 0;
      if (filtroNotas === "con" && cantidad === 0) return false;
      if (filtroNotas === "sin" && cantidad > 0) return false;
      if (programa && e.programa !== programa) return false;
      if (!q) return true;
      return normalizar(`${nombreCompleto(e)} ${e.cedula}`).includes(q);
    });
  }, [estudiantes, notasPorCedula, busqueda, programa, filtroNotas]);

  const { visibles, pagina, totalPaginas, setPagina } = usePaginacion(
    filtrados,
    `${busqueda}|${programa}|${filtroNotas}`,
  );

  if (!datos) return null;

  return (
    <MarcoListado
      total={filtrados.length}
      totalSinFiltrar={estudiantes.length}
      sustantivo={["estudiante", "estudiantes"]}
      pagina={pagina}
      totalPaginas={totalPaginas}
      onPagina={setPagina}
      filtros={
        <>
          <Buscador
            className="flex-1"
            valor={busqueda}
            onChange={setBusqueda}
            etiqueta="Buscar estudiantes"
            placeholder="Buscar por nombre, apellido o cédula"
          />
          <TextField
            select
            label="Programa"
            value={programa}
            onChange={(e) => setPrograma(e.target.value)}
            className="lg:w-64"
          >
            <MenuItem value="">Todos los programas</MenuItem>
            {programas.map((p) => (
              <MenuItem key={p} value={p}>
                {p}
              </MenuItem>
            ))}
          </TextField>
          <FiltroEstado
            etiqueta="Filtrar por notas"
            valor={filtroNotas}
            onChange={setFiltroNotas}
            opciones={[
              { valor: "todos", texto: "Todos" },
              { valor: "con", texto: "Con notas" },
              { valor: "sin", texto: "Sin notas" },
            ]}
          />
        </>
      }
    >
      {visibles.length === 0 ? (
        <SinResultados texto="Ningún estudiante coincide con los filtros." />
      ) : (
        visibles.map((e) => (
          <FilaEstudiante
            key={e.cedula}
            estudiante={e}
            notas={notasPorCedula?.get(e.cedula) ?? []}
            materias={datos.materias}
          />
        ))
      )}
    </MarcoListado>
  );
}

function FilaEstudiante({
  estudiante,
  notas,
  materias,
}: {
  estudiante: Estudiante;
  notas: Nota[];
  materias: Materia[];
}) {
  const { registrarNota } = useDatos();
  const prom = promedio(notas.map((n) => n.nota));
  const calificadas = new Set(notas.map((n) => n.materiaCodigo));
  const pendientes = materias
    .filter((m) => !calificadas.has(m.codigo))
    .map((m) => ({ id: m.codigo, etiqueta: `${m.codigo} · ${m.nombre}` }));
  const iniciales = `${estudiante.nombre[0] ?? ""}${estudiante.apellido[0] ?? ""}`.toUpperCase();

  return (
    <Accordion slotProps={{ transition: { unmountOnExit: true } }}>
      <AccordionSummary expandIcon={<ExpandMoreRounded />} sx={{ px: 2 }}>
        <div className="flex w-full min-w-0 items-center gap-3 pr-2">
          <Avatar sx={{ width: 36, height: 36, fontSize: 14, bgcolor: "primary.main", color: "primary.contrastText" }}>
            {iniciales}
          </Avatar>
          <div className="min-w-0 flex-1">
            <Typography className="truncate" sx={{ fontWeight: 600 }}>
              {nombreCompleto(estudiante)}
            </Typography>
            <Typography variant="body2" color="text.secondary" className="truncate">
              CC {estudiante.cedula} · {estudiante.programa}
            </Typography>
          </div>
          <div className="hidden text-right sm:block">
            <Typography variant="body2" color="text.secondary">
              {notas.length} {notas.length === 1 ? "materia" : "materias"}
            </Typography>
            <Typography variant="body2" sx={{ fontWeight: 600, fontVariantNumeric: "tabular-nums" }}>
              {prom === null ? "Sin notas" : `Promedio ${formatearNota(prom)}`}
            </Typography>
          </div>
        </div>
      </AccordionSummary>
      <AccordionDetails sx={{ px: 2, pb: 2, bgcolor: "action.hover" }}>
        <Typography variant="overline" color="text.secondary">
          Materias matriculadas
        </Typography>
        {notas.length === 0 ? (
          <Typography variant="body2" color="text.secondary" className="py-2">
            Aún no tiene materias con nota.
          </Typography>
        ) : (
          <ul className="divide-y divide-[var(--mui-palette-divider)]">
            {notas.map((n) => (
              <li key={n.materiaCodigo} className="flex items-center gap-3 py-2.5">
                <Codigo>{n.materiaCodigo}</Codigo>
                <span className="flex-1 text-sm">{n.materiaNombre}</span>
                <NotaChip nota={n.nota} />
              </li>
            ))}
          </ul>
        )}
        <AgregarNota
          etiquetaOpcion="Materia"
          opciones={pendientes}
          textoSinOpciones="Ya tiene nota en todas las materias."
          onGuardar={async (codigo, nota) => {
            await registrarNota(estudiante.cedula, codigo, nota);
          }}
        />
      </AccordionDetails>
    </Accordion>
  );
}
