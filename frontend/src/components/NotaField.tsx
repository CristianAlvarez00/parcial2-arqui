"use client";

import TextField from "@mui/material/TextField";
import { aNumero, formatearNota, validarNota } from "@/lib/nota";

interface Props {
  value: string;
  onChange: (valor: string) => void;
  error: string | null;
  size?: "small" | "medium";
  className?: string;
}

/** Campo de nota 0.00–5.00. Al salir del campo normaliza a dos decimales. */
export default function NotaField({ value, onChange, error, size = "medium", className }: Props) {
  return (
    <TextField
      label="Nota"
      placeholder="4.25"
      size={size}
      className={className}
      value={value}
      error={Boolean(error)}
      helperText={error ?? "De 0.00 a 5.00"}
      onChange={(e) => {
        // Solo dígitos y un separador decimal; el resto se descarta al escribir.
        const limpio = e.target.value.replace(/[^\d.,]/g, "").slice(0, 4);
        onChange(limpio);
      }}
      onBlur={() => {
        if (value !== "" && validarNota(value) === null) onChange(formatearNota(aNumero(value)));
      }}
      slotProps={{ htmlInput: { inputMode: "decimal" } }}
    />
  );
}
