import { Card, Group, Text, Title, SimpleGrid, Grid } from "@mantine/core";
import { FC } from "react";
import { useNavigate } from "react-router-dom";

export interface BidCardProps {
  id: string;
  projectId: string;
  projectTitle: string;
  amount: number;
}

export const BidCard: FC<BidCardProps> = ({
  id,
  projectId,
  projectTitle,
  amount,
}) => {
  const navigate = useNavigate();

  return (
    <Card
      shadow="sm"
      p="lg"
      radius="md"
      withBorder
      onClick={() => navigate(`/project/${projectId}`)}
    >
      <Grid>
        <Grid.Col span={2}>
          <Text>
            <b>{amount.toFixed(2)}</b>
          </Text>
        </Grid.Col>
        <Grid.Col span={10}>
          <Title order={4} weight={500}>
            {projectTitle}
          </Title>
        </Grid.Col>
      </Grid>
    </Card>
  );
};
