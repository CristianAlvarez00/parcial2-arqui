import Link from "next/link";
import ArrowForwardRounded from "@mui/icons-material/ArrowForwardRounded";
import MenuBookOutlined from "@mui/icons-material/MenuBookOutlined";
import PeopleAltOutlined from "@mui/icons-material/PeopleAltOutlined";

const HERRAMIENTAS = [
  {
    href: "/materias",
    titulo: "Listado de materias",
    descripcion: "Consulta las materias y los estudiantes que tienen nota en cada una.",
    Icono: MenuBookOutlined,
  },
  {
    href: "/estudiantes",
    titulo: "Listado de estudiantes",
    descripcion: "Busca estudiantes y revisa las materias y notas de cada uno.",
    Icono: PeopleAltOutlined,
  },
];

export default function Inicio() {
  return (
    <div className="py-4 sm:py-10">
      <h1 className="text-3xl font-semibold leading-tight sm:text-4xl">Materias, estudiantes y notas</h1>
      <p className="mt-3 text-[var(--mui-palette-text-secondary)]">
        Elige un listado para empezar. Desde cada uno puedes agregar notas de 0.00 a 5.00.
      </p>

      <ul className="mt-10 grid gap-4 sm:grid-cols-2">
        {HERRAMIENTAS.map(({ href, titulo, descripcion, Icono }) => (
          <li key={href}>
            <Link
              href={href}
              className="group flex h-full flex-col rounded-xl border border-[var(--mui-palette-divider)] bg-[var(--mui-palette-background-paper)] p-6 transition-colors hover:border-[var(--mui-palette-primary-main)] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[var(--mui-palette-primary-main)]"
            >
              <Icono className="text-[var(--mui-palette-primary-main)]" sx={{ fontSize: 32 }} aria-hidden />
              <span className="mt-4 text-lg font-semibold">{titulo}</span>
              <span className="mt-1 flex-1 text-sm text-[var(--mui-palette-text-secondary)]">
                {descripcion}
              </span>
              <span className="mt-5 inline-flex items-center gap-1 text-sm font-medium text-[var(--mui-palette-primary-main)]">
                Abrir
                <ArrowForwardRounded
                  fontSize="small"
                  className="transition-transform group-hover:translate-x-0.5"
                  aria-hidden
                />
              </span>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
