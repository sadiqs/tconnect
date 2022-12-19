import { useLocation } from "react-router-dom";
import { Navigate, useNavigate } from "react-router-dom";
import { useAuthUser } from "../hooks/useAuthUser";
import { CustomerPage } from "./CustomerPage";
import { TradiePage } from "./TradiePage";

export const HomePage = () => {
  const auth = useAuthUser();
  const location = useLocation();

  const { user: { type } = {} } = useAuthUser();

  if (!type) {
    return <Navigate to="/login" state={{ from: location.pathname }} />;
  } else if (type === "customer") {
    return <CustomerPage />;
  } else return <TradiePage />;
};
