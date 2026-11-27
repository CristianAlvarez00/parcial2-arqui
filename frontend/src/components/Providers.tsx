"use client";

import CssBaseline from "@mui/material/CssBaseline";
import { ThemeProvider } from "@mui/material/styles";
import { AppRouterCacheProvider } from "@mui/material-nextjs/v16-appRouter";
import { theme } from "@/theme";
import { DataProvider } from "@/lib/data";
import { NotifierProvider } from "@/components/Notifier";

export default function Providers({ children }: { children: React.ReactNode }) {
  return (
    <AppRouterCacheProvider options={{ enableCssLayer: true }}>
      <ThemeProvider theme={theme} defaultMode="system">
        <CssBaseline />
        <NotifierProvider>
          <DataProvider>{children}</DataProvider>
        </NotifierProvider>
      </ThemeProvider>
    </AppRouterCacheProvider>
  );
}
