"use client";

import { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import AppBar from "@mui/material/AppBar";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Toolbar from "@mui/material/Toolbar";
import MenuIcon from "@mui/icons-material/Menu";
import SchoolOutlined from "@mui/icons-material/SchoolOutlined";
import ThemeToggle from "@/components/ThemeToggle";

export const SECCIONES = [
  { href: "/", etiqueta: "Inicio" },
  { href: "/materias", etiqueta: "Materias" },
  { href: "/estudiantes", etiqueta: "Estudiantes" },
] as const;

function esActiva(pathname: string, href: string) {
  return href === "/" ? pathname === "/" : pathname.startsWith(href);
}

export default function Header() {
  const pathname = usePathname();
  const [ancla, setAncla] = useState<HTMLElement | null>(null);

  return (
    <AppBar position="sticky" color="primary" elevation={0} enableColorOnDark>
      <Toolbar className="mx-auto w-full max-w-5xl gap-2 px-4 sm:px-6" disableGutters>
        <Link href="/" className="mr-auto flex items-center gap-2 text-lg font-semibold">
          <SchoolOutlined aria-hidden />
          Notas
        </Link>

        <nav aria-label="Secciones" className="hidden items-center gap-1 md:flex">
          {SECCIONES.map(({ href, etiqueta }) => {
            const activa = esActiva(pathname, href);
            return (
              <Button
                key={href}
                component={Link}
                href={href}
                color="inherit"
                aria-current={activa ? "page" : undefined}
                sx={{
                  px: 1.5,
                  opacity: activa ? 1 : 0.8,
                  bgcolor: activa ? "rgba(255,255,255,0.16)" : "transparent",
                  "&:hover": { bgcolor: "rgba(255,255,255,0.12)", opacity: 1 },
                }}
              >
                {etiqueta}
              </Button>
            );
          })}
        </nav>

        <ThemeToggle />

        <IconButton
          color="inherit"
          className="md:hidden"
          aria-label="Abrir menú"
          onClick={(e) => setAncla(e.currentTarget)}
        >
          <MenuIcon />
        </IconButton>
        <Menu anchorEl={ancla} open={Boolean(ancla)} onClose={() => setAncla(null)}>
          {SECCIONES.map(({ href, etiqueta }) => (
            <MenuItem
              key={href}
              component={Link}
              href={href}
              selected={esActiva(pathname, href)}
              onClick={() => setAncla(null)}
            >
              {etiqueta}
            </MenuItem>
          ))}
        </Menu>
      </Toolbar>
    </AppBar>
  );
}
