import { createStyles, Loader, Title, Container, Button } from "@mantine/core";
import { Navigate, useNavigate } from "react-router-dom";
import { useGetBidsQuery, useGetProjectsQuery } from "../api/tconnectApi";
import { ProjectList } from "../components/ProjectList";
import { useAuthUser } from "../hooks/useAuthUser";

const useStyles = createStyles((theme) => ({
  header: {},
}));

export function TradiePage() {
  const { classes, theme } = useStyles();
  const navigate = useNavigate();
  const { user } = useAuthUser();

  const { data: projects, isLoading: isProjectsLoading } = useGetProjectsQuery(
    {}
  );
  const { data: bids, isLoading: isBidsLoading } = useGetBidsQuery({});

  if (user?.type !== "tradie") {
    return <Navigate to="/" />;
  }

  if (isBidsLoading || isProjectsLoading) {
    return <Loader />;
  } else if (!projects || !bids) {
    return <Title>No bids found</Title>;
  }

  return (
    <Container>
      <Button.Group>
        <Button onClick={() => navigate("/")} mb="lg" data-disabled>
          Explore Projects
        </Button>
        <Button onClick={() => navigate("/bids")} mb="lg">
          View Bids
        </Button>
      </Button.Group>
      <ProjectList projects={projects} />
    </Container>
  );
}
