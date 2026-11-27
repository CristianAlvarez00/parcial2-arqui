import type { Metadata } from "next";
import MateriasVista from "@/components/MateriasVista";
import { Encabezado } from "@/components/ui";

export const metadata: Metadata = { title: "Materias" };

export default function MateriasPage() {
  return (
    <>
      <Encabezado
        titulo="Listado de materias"
        descripcion="Abre una materia para ver sus estudiantes matriculados y agregar notas."
      />
      <MateriasVista />
    </>
  );
}
