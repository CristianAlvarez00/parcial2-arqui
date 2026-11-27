import type { Metadata } from "next";
import EstudiantesVista from "@/components/EstudiantesVista";
import { Encabezado } from "@/components/ui";

export const metadata: Metadata = { title: "Estudiantes" };

export default function EstudiantesPage() {
  return (
    <>
      <Encabezado
        titulo="Listado de estudiantes"
        descripcion="Abre un estudiante para ver sus materias matriculadas y agregar notas."
      />
      <EstudiantesVista />
    </>
  );
}
