"use client";

import { useState } from "react";
import Autocomplete from "@mui/material/Autocomplete";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import AddRounded from "@mui/icons-material/AddRounded";
import { useNotificar } from "@/components/Notifier";
import NotaField from "@/components/NotaField";
import { aNumero, validarNota } from "@/lib/nota";

export interface Opcion {
  id: string;
  etiqueta: string;
}

interface Props {
  /** Opciones sin nota todavía (materias del estudiante o estudiantes de la materia). */
  opciones: Opcion[];
  etiquetaOpcion: string;
  textoSinOpciones: string;
  onGuardar: (id: string, nota: number) => Promise<void>;
}

/** Fila para agregar una nota dentro de un sublistado. */
export default function AgregarNota({ opciones, etiquetaOpcion, textoSinOpciones, onGuardar }: Props) {
  const notificar = useNotificar();
  const [seleccion, setSeleccion] = useState<Opcion | null>(null);
  const [nota, setNota] = useState("");
  const [errorOpcion, setErrorOpcion] = useState<string | null>(null);
  const [errorNota, setErrorNota] = useState<string | null>(null);
  const [guardando, setGuardando] = useState(false);

  if (opciones.length === 0) {
    return (
      <Typography variant="body2" color="text.secondary" className="px-1 pt-3">
        {textoSinOpciones}
      </Typography>
    );
  }

  async function guardar(e: React.FormEvent) {
    e.preventDefault();
    const fallaOpcion = seleccion ? null : `Elige ${etiquetaOpcion.toLowerCase()}.`;
    const fallaNota = validarNota(nota);
    setErrorOpcion(fallaOpcion);
    setErrorNota(fallaNota);
    if (fallaOpcion || fallaNota || !seleccion) return;

    setGuardando(true);
    try {
      await onGuardar(seleccion.id, aNumero(nota));
      notificar(`Nota registrada para ${seleccion.etiqueta}.`);
      setSeleccion(null);
      setNota("");
    } catch (error) {
      notificar(error instanceof Error ? error.message : "No se pudo registrar la nota.", "error");
    } finally {
      setGuardando(false);
    }
  }

  return (
    <form onSubmit={guardar} noValidate className="flex flex-col gap-3 pt-4 sm:flex-row sm:items-start">
      <Autocomplete
        className="flex-1"
        size="small"
        options={opciones}
        value={seleccion}
        onChange={(_, valor) => {
          setSeleccion(valor);
          setErrorOpcion(null);
        }}
        getOptionLabel={(o) => o.etiqueta}
        isOptionEqualToValue={(a, b) => a.id === b.id}
        noOptionsText="Sin coincidencias"
        renderInput={(params) => (
          <TextField
            {...params}
            label={etiquetaOpcion}
            error={Boolean(errorOpcion)}
            helperText={errorOpcion ?? " "}
          />
        )}
      />
      <NotaField
        size="small"
        value={nota}
        error={errorNota}
        onChange={(v) => {
          setNota(v);
          setErrorNota(null);
        }}
        className="sm:w-44"
      />
      <Button
        type="submit"
        variant="contained"
        startIcon={<AddRounded />}
        loading={guardando}
        sx={{ height: 40, flexShrink: 0 }}
      >
        Agregar nota
      </Button>
    </form>
  );
}
