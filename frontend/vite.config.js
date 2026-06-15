import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";

export default defineConfig(() => {
  const backendPort = process.env.VITE_BACKEND_PORT || "8080";
  const backendHost = process.env.VITE_BACKEND_HOST || "localhost";
  const backendProtocol = process.env.VITE_BACKEND_PROTOCOL || "http";
  const target = `${backendProtocol}://${backendHost}:${backendPort}`;

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        "@": path.resolve(__dirname, "./src"),
      },
    },
    server: {
      port: 5173,
      proxy: {
        "/api": {
          target,
          changeOrigin: true,
        },
      },
    },
  };
});
