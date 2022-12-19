import { createStyles, Loader, Title, Container, Button } from "@mantine/core";
import { Navigate, useNavigate } from "react-router-dom";
import { useGetProjectsQuery } from "../api/tconnectApi";
import { ProjectList } from "../components/ProjectList";
import { useAuthUser } from "../hooks/useAuthUser";

const useStyles = createStyles((theme) => ({
  header: {},
}));

export function CustomerPage() {
  const { classes, theme } = useStyles();
  const navigate = useNavigate();
  const { user } = useAuthUser();
  const { data, isLoading } = useGetProjectsQuery({});

  if (user?.type !== "customer") {
    return <Navigate to="/" />;
  }

  if (isLoading) {
    return <Loader />;
  } else if (!data) {
    return <Title>No projects found</Title>;
  }

  return (
    <Container>
      <Button onClick={() => navigate("/project/create")} mb="lg">
        Create New
      </Button>
      <ProjectList projects={data} />
    </Container>
  );
}
