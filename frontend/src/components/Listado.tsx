"use client";

import { useState } from "react";
import InputAdornment from "@mui/material/InputAdornment";
import Pagination from "@mui/material/Pagination";
import Paper from "@mui/material/Paper";
import TextField from "@mui/material/TextField";
import ToggleButton from "@mui/material/ToggleButton";
import ToggleButtonGroup from "@mui/material/ToggleButtonGroup";
import Typography from "@mui/material/Typography";
import SearchRounded from "@mui/icons-material/SearchRounded";

const POR_PAGINA = 10;

/** Paginación en el cliente; vuelve a la primera página cuando cambia la lista filtrada. */
export function usePaginacion<T>(items: T[], clave: string) {
  // Guarda la página junto a la clave de filtros: si la clave cambia, se vuelve a la 1.
  const [estado, setEstado] = useState({ clave, pagina: 1 });
  const pagina = estado.clave === clave ? estado.pagina : 1;
  const setPagina = (p: number) => setEstado({ clave, pagina: p });
  const totalPaginas = Math.max(1, Math.ceil(items.length / POR_PAGINA));
  const actual = Math.min(pagina, totalPaginas);
  return {
    visibles: items.slice((actual - 1) * POR_PAGINA, actual * POR_PAGINA),
    pagina: actual,
    totalPaginas,
    setPagina,
  };
}

export function Buscador({
  valor,
  onChange,
  placeholder,
  etiqueta,
  size = "medium",
  className,
}: {
  valor: string;
  onChange: (v: string) => void;
  placeholder: string;
  etiqueta: string;
  size?: "small" | "medium";
  className?: string;
}) {
  return (
    <TextField
      className={className}
      size={size}
      type="search"
      value={valor}
      onChange={(e) => onChange(e.target.value)}
      placeholder={placeholder}
      slotProps={{
        htmlInput: { "aria-label": etiqueta },
        input: {
          startAdornment: (
            <InputAdornment position="start">
              <SearchRounded fontSize="small" />
            </InputAdornment>
          ),
        },
      }}
    />
  );
}

export function FiltroEstado<T extends string>({
  valor,
  onChange,
  opciones,
  etiqueta,
}: {
  valor: T;
  onChange: (v: T) => void;
  opciones: { valor: T; texto: string }[];
  etiqueta: string;
}) {
  return (
    <ToggleButtonGroup
      exclusive
      size="small"
      color="primary"
      value={valor}
      onChange={(_, v: T | null) => v && onChange(v)}
      aria-label={etiqueta}
      sx={{ height: 56, "& .MuiToggleButton-root": { px: 2 } }}
    >
      {opciones.map((o) => (
        <ToggleButton key={o.valor} value={o.valor}>
          {o.texto}
        </ToggleButton>
      ))}
    </ToggleButtonGroup>
  );
}

/** Marco del listado: barra de filtros, contador, lista y paginación. */
export function MarcoListado({
  filtros,
  total,
  totalSinFiltrar,
  sustantivo,
  pagina,
  totalPaginas,
  onPagina,
  children,
}: {
  filtros: React.ReactNode;
  total: number;
  totalSinFiltrar: number;
  sustantivo: [string, string];
  pagina: number;
  totalPaginas: number;
  onPagina: (p: number) => void;
  children: React.ReactNode;
}) {
  const [singular, plural] = sustantivo;
  return (
    <>
      <div className="mb-4 flex flex-col gap-3 lg:flex-row lg:items-center">{filtros}</div>
      <Typography variant="body2" color="text.secondary" className="mb-2 px-1" aria-live="polite">
        {total === totalSinFiltrar
          ? `${total} ${total === 1 ? singular : plural}`
          : `${total} de ${totalSinFiltrar} ${plural}`}
      </Typography>
      <Paper variant="outlined" className="overflow-hidden">
        {children}
      </Paper>
      {totalPaginas > 1 && (
        <div className="mt-4 flex justify-center">
          <Pagination count={totalPaginas} page={pagina} onChange={(_, p) => onPagina(p)} color="primary" />
        </div>
      )}
    </>
  );
}
