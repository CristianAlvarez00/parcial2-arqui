// Cliente del backend Spring Boot. Las rutas /api/v1/* pasan por el proxy de
// next.config.ts, así que no hace falta CORS.

export interface Estudiante {
  id: number;
  nombre: string;
  apellido: string;
  cedula: string;
  programa: string;
}

export interface Materia {
  id: number;
  codigo: string;
  nombre: string;
}

export interface Nota {
  id: number;
  cedula: string;
  materiaCodigo: string;
  materiaNombre: string;
  nota: number;
}

interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export class ApiError extends Error {
  constructor(
    message: string,
    readonly status: number,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

// Tamaño máximo de página que acepta el backend.
const TAMANO_PAGINA = 100;

async function pedir<T>(ruta: string, init?: RequestInit): Promise<T> {
  let respuesta: Response;
  try {
    respuesta = await fetch(ruta, {
      ...init,
      headers: { "Content-Type": "application/json", ...init?.headers },
      cache: "no-store",
    });
  } catch {
    throw new ApiError("No se pudo conectar con el backend.", 0);
  }

  if (!respuesta.ok) {
    // El backend responde errores como ProblemDetail ({ detail, status, ... }).
    let detalle = `Error ${respuesta.status}`;
    try {
      const cuerpo = await respuesta.json();
      if (typeof cuerpo?.detail === "string") detalle = cuerpo.detail;
    } catch {
      if (respuesta.status >= 500) detalle = "El backend no está disponible.";
    }
    throw new ApiError(detalle, respuesta.status);
  }
  return respuesta.json() as Promise<T>;
}

// El backend no filtra ni busca: se traen todas las páginas y se filtra en el cliente.
async function traerTodo<T>(ruta: string): Promise<T[]> {
  const elementos: T[] = [];
  for (let pagina = 0; ; pagina++) {
    const separador = ruta.includes("?") ? "&" : "?";
    const resultado = await pedir<PageResponse<T>>(
      `${ruta}${separador}page=${pagina}&size=${TAMANO_PAGINA}`,
    );
    elementos.push(...resultado.content);
    if (resultado.last || resultado.content.length === 0) return elementos;
  }
}

export const api = {
  materias: () => traerTodo<Materia>("/api/v1/materias"),
  estudiantes: () => traerTodo<Estudiante>("/api/v1/estudiantes"),
  notas: (cedula: string) =>
    traerTodo<Nota>(`/api/v1/estudiantes/${encodeURIComponent(cedula)}/notas`),
  registrarNota: (cedula: string, materiaCodigo: string, nota: number) =>
    pedir<Nota>(`/api/v1/estudiantes/${encodeURIComponent(cedula)}/notas`, {
      method: "POST",
      body: JSON.stringify({ materiaCodigo, nota }),
    }),
};
