"use client";

import Alert from "@mui/material/Alert";
import Button from "@mui/material/Button";
import Chip from "@mui/material/Chip";
import CircularProgress from "@mui/material/CircularProgress";
import Typography from "@mui/material/Typography";
import { useDatos } from "@/lib/data";
import { formatearNota, NOTA_APROBATORIA } from "@/lib/nota";

export function Encabezado({ titulo, descripcion }: { titulo: string; descripcion: string }) {
  return (
    <header className="mb-6">
      <Typography variant="h4" component="h1" className="!text-2xl sm:!text-3xl">
        {titulo}
      </Typography>
      <Typography color="text.secondary" className="mt-1">
        {descripcion}
      </Typography>
    </header>
  );
}

/** Muestra carga o error mientras DataProvider trae los datos del backend. */
export function ConDatos({ children }: { children: React.ReactNode }) {
  const { estado, recargar } = useDatos();
  if (estado.tipo === "cargando") {
    return (
      <div className="flex items-center gap-3 py-16 text-sm" role="status">
        <CircularProgress size={20} />
        <Typography color="text.secondary">Cargando datos del backend…</Typography>
      </div>
    );
  }
  if (estado.tipo === "error") {
    return (
      <Alert
        severity="error"
        action={
          <Button color="inherit" size="small" onClick={recargar}>
            Reintentar
          </Button>
        }
      >
        {estado.mensaje} Revisa que el backend esté corriendo en el puerto 8080.
      </Alert>
    );
  }
  return <>{children}</>;
}

export function NotaChip({ nota }: { nota: number }) {
  const aprobada = Number(nota) >= NOTA_APROBATORIA;
  return (
    <Chip
      label={formatearNota(nota)}
      size="small"
      color={aprobada ? "success" : "error"}
      variant="outlined"
      sx={{ fontVariantNumeric: "tabular-nums", fontWeight: 600, minWidth: 56 }}
    />
  );
}

export function Codigo({ children }: { children: React.ReactNode }) {
  return (
    <span className="rounded bg-black/5 px-1.5 py-0.5 font-mono text-xs dark:bg-white/10">{children}</span>
  );
}

export function SinResultados({ texto }: { texto: string }) {
  return (
    <Typography color="text.secondary" className="px-4 py-10 text-center">
      {texto}
    </Typography>
  );
}
