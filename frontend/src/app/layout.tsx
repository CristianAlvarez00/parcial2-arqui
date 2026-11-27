import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import InitColorSchemeScript from "@mui/material/InitColorSchemeScript";
import Providers from "@/components/Providers";
import Header from "@/components/Header";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: { default: "Notas", template: "%s · Notas" },
  description: "Consulta de materias y estudiantes, y registro de notas.",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html
      lang="es"
      className={`${geistSans.variable} ${geistMono.variable}`}
      suppressHydrationWarning
    >
      <body>
        <InitColorSchemeScript attribute="class" defaultMode="system" />
        <Providers>
          <Header />
          <main className="mx-auto w-full max-w-5xl px-4 py-8 sm:px-6 sm:py-10">{children}</main>
        </Providers>
      </body>
    </html>
  );
}
