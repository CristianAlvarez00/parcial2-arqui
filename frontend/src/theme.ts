"use client";

import { createTheme } from "@mui/material/styles";

// Verde institucional como color primario. Las variables CSS permiten cambiar
// de tema sin parpadeo: MUI pone la clase .light / .dark en <html>.
export const theme = createTheme({
  cssVariables: { colorSchemeSelector: "class" },
  colorSchemes: {
    light: {
      palette: {
        primary: { main: "#2E7D32", light: "#4CAF50", dark: "#1B5E20", contrastText: "#ffffff" },
        background: { default: "#F4F7F4", paper: "#ffffff" },
      },
    },
    dark: {
      palette: {
        primary: { main: "#66BB6A", light: "#81C784", dark: "#2E7D32", contrastText: "#0A1F0C" },
        background: { default: "#0C130E", paper: "#131D16" },
      },
    },
  },
  shape: { borderRadius: 10 },
  typography: {
    fontFamily: "var(--font-geist-sans), system-ui, sans-serif",
    h1: { fontWeight: 600 },
    h2: { fontWeight: 600 },
    h3: { fontWeight: 600 },
    h4: { fontWeight: 600 },
    h5: { fontWeight: 600 },
    h6: { fontWeight: 600 },
    button: { textTransform: "none", fontWeight: 500 },
  },
  components: {
    MuiButton: { defaultProps: { disableElevation: true } },
    MuiPaper: { defaultProps: { elevation: 0 } },
    MuiAccordion: {
      defaultProps: { disableGutters: true, elevation: 0, square: true },
      styleOverrides: {
        root: ({ theme }) => ({
          backgroundColor: "transparent",
          borderTop: `1px solid ${theme.vars.palette.divider}`,
          "&::before": { display: "none" },
          "&:first-of-type": { borderTop: "none" },
        }),
      },
    },
  },
});
