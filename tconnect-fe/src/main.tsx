import React from "react";
import ReactDOM from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import "./index.css";
import App from "./App";
import ErrorPage from "./ErrorPage";
import { MantineProvider } from "@mantine/core";
import { ReactKeycloakProvider } from "@react-keycloak/web";

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <MantineProvider
      withGlobalStyles
      withNormalizeCSS
      theme={{
        colorScheme: "light",
      }}
    >
      <App />
    </MantineProvider>
  </React.StrictMode>
);
