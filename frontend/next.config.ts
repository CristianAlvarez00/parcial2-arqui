import type { NextConfig } from "next";

// El backend (Spring Boot) no expone CORS, así que el frontend lo consume a
// través de un proxy: /api/v1/* se reenvía a BACKEND_URL sin tocar el backend.
const backendUrl = process.env.BACKEND_URL ?? "http://localhost:8080";

const nextConfig: NextConfig = {
  // Imagen de Docker mínima: .next/standalone trae server.js y solo las dependencias necesarias.
  output: "standalone",
  cacheComponents: true,
  partialPrefetching: true,
  async rewrites() {
    return [
      {
        source: "/api/v1/:path*",
        destination: `${backendUrl}/api/v1/:path*`,
      },
    ];
  },
};

export default nextConfig;
