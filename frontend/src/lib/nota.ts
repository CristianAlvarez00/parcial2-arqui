// Misma regla que Validacion.nota del backend: 0 a 5, hasta dos decimales.
const FORMATO_NOTA = /^\d(?:[.,]\d{1,2})?$/;

export function validarNota(valor: string): string | null {
  const texto = valor.trim();
  if (texto === "") return "Escribe la nota.";
  if (!FORMATO_NOTA.test(texto)) return "Usa un número de 0.00 a 5.00 con máximo dos decimales.";
  const numero = Number(texto.replace(",", "."));
  if (numero < 0 || numero > 5) return "La nota debe estar entre 0.00 y 5.00.";
  return null;
}

export function aNumero(valor: string): number {
  return Number(valor.trim().replace(",", "."));
}

export function formatearNota(nota: number): string {
  return Number(nota).toFixed(2);
}

export function promedio(notas: number[]): number | null {
  if (notas.length === 0) return null;
  return notas.reduce((suma, n) => suma + Number(n), 0) / notas.length;
}

// Nota aprobatoria en la escala colombiana.
export const NOTA_APROBATORIA = 3;
