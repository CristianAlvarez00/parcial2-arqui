"use client";

import { createContext, useCallback, useContext, useState } from "react";
import Alert, { type AlertColor } from "@mui/material/Alert";
import Snackbar from "@mui/material/Snackbar";

type Notificar = (mensaje: string, tipo?: AlertColor) => void;

const Contexto = createContext<Notificar>(() => {});

export function NotifierProvider({ children }: { children: React.ReactNode }) {
  const [aviso, setAviso] = useState<{ mensaje: string; tipo: AlertColor; id: number } | null>(null);
  const [abierto, setAbierto] = useState(false);

  const notificar = useCallback<Notificar>((mensaje, tipo = "success") => {
    setAviso({ mensaje, tipo, id: Date.now() });
    setAbierto(true);
  }, []);

  return (
    <Contexto.Provider value={notificar}>
      {children}
      <Snackbar
        key={aviso?.id}
        open={abierto}
        autoHideDuration={4000}
        onClose={(_, razon) => razon !== "clickaway" && setAbierto(false)}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert
          severity={aviso?.tipo ?? "success"}
          variant="filled"
          onClose={() => setAbierto(false)}
          sx={{ width: "100%" }}
        >
          {aviso?.mensaje}
        </Alert>
      </Snackbar>
    </Contexto.Provider>
  );
}

export function useNotificar(): Notificar {
  return useContext(Contexto);
}
