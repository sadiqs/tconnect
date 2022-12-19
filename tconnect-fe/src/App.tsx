import { createStyles } from "@mantine/core";
import { useState } from "react";
import { Provider } from "react-redux";
import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
  Routes,
} from "react-router-dom";
import "./App.css";
import { ProjectCreate } from "./components/ProjectCreate";
import { ProjectView } from "./components/ProjectView";
import { HomePage } from "./pages/HomePage";
import { Layout } from "./pages/Layout";
import { LoginPage } from "./pages/LoginPage";
import { store } from "./store/store";
import { BidList } from "./components/BidList";

// const router = createBrowserRouter(
//   createRoutesFromElements(
//     <Routes>
//       <Route path="/" element={<Layout />}>
//         <Route index element={<HomePage />} />
//         <Route path="login" element={<LoginPage />} />
//       </Route>
//     </Routes>
//   )
// );
const router2 = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        index: true,
        element: <HomePage />,
      },
      {
        path: "login",
        element: <LoginPage />,
      },
      {
        path: "project/create",
        element: <ProjectCreate />,
      },
      {
        path: "bids",
        element: <BidList />,
      },
      {
        path: "project/:projectId",
        element: <ProjectView />,
      },
    ],
  },
]);

function App() {
  return (
    <Provider store={store}>
      <RouterProvider router={router2} />
    </Provider>
  );
}

export default App;
