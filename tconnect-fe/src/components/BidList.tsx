import { Button, Container, Loader, SimpleGrid, Title } from "@mantine/core";
import { FC } from "react";
import { useNavigate } from "react-router-dom";
import { useGetBidsQuery, useGetProjectsQuery } from "../api/tconnectApi";
import { BidCard } from "./BidCard";

export const BidList: FC = () => {
  const { data: bids, isLoading: areBidsLoading } = useGetBidsQuery({});
  const { data: projects, isLoading: areProjectsLoading } = useGetProjectsQuery(
    {}
  );
  const navigate = useNavigate();

  if (!bids || !projects) {
    return <Title>No bids, explore projects and place bids to view</Title>;
  } else if (areProjectsLoading || areBidsLoading) {
    return <Loader />;
  }

  const projectMap = new Map(projects.map((p) => [p.id, p]));

  return (
    <Container>
      <Button.Group>
        <Button onClick={() => navigate("/")} mb="lg">
          Explore Projects
        </Button>
        <Button onClick={() => navigate("/bids")} mb="lg" data-disabled>
          View Bids
        </Button>
      </Button.Group>
      <SimpleGrid cols={1}>
        {bids.map((bid) => {
          console.log(bid);
          const [projectId, projectTitle] =
            typeof bid.project == "string"
              ? [
                  projectMap.get(bid.project)?.id || "",
                  projectMap.get(bid.project)?.title || "",
                ]
              : [bid.project.id, bid.project.title];
          return (
            <BidCard
              projectId={projectId}
              projectTitle={projectTitle}
              key={bid.id}
              {...bid}
            />
          );
        })}
      </SimpleGrid>
    </Container>
  );
};
