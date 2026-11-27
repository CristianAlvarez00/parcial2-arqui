"use client";

import IconButton from "@mui/material/IconButton";
import Tooltip from "@mui/material/Tooltip";
import { useColorScheme } from "@mui/material/styles";
import DarkModeOutlined from "@mui/icons-material/DarkModeOutlined";
import LightModeOutlined from "@mui/icons-material/LightModeOutlined";

export default function ThemeToggle() {
  const { mode, systemMode, setMode } = useColorScheme();
  // Antes de montar, mode es undefined: se reserva el espacio sin icono.
  const actual = mode === "system" ? systemMode : mode;
  const siguiente = actual === "dark" ? "light" : "dark";
  const etiqueta = siguiente === "dark" ? "Cambiar a tema oscuro" : "Cambiar a tema claro";

  return (
    <Tooltip title={etiqueta}>
      <IconButton color="inherit" aria-label={etiqueta} onClick={() => setMode(siguiente)}>
        {actual === undefined ? (
          <span className="block h-6 w-6" />
        ) : actual === "dark" ? (
          <LightModeOutlined />
        ) : (
          <DarkModeOutlined />
        )}
      </IconButton>
    </Tooltip>
  );
}
