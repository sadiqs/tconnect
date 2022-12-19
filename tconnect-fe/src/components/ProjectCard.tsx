import {
  Badge,
  Button,
  Card,
  Flex,
  Group,
  SimpleGrid,
  Text,
  Title,
} from "@mantine/core";
import { IconArrowNarrowRight } from "@tabler/icons";
import { FC } from "react";
import { Project } from "../model";
import { useNavigate } from "react-router-dom";
import {
  format,
  formatDistanceStrict,
  formatRelative,
  subDays,
  parseJSON,
} from "date-fns";

export const ProjectCard: FC<Project> = ({
  id,
  title,
  description,
  expectedHours,
  biddingEndTime,
}) => {
  const navigate = useNavigate();

  return (
    <Card
      shadow="sm"
      p="lg"
      radius="md"
      withBorder
      onClick={() => navigate(`/project/${id}`)}
    >
      <Title order={3} pb="md" weight={500}>
        {title}
      </Title>
      <Text lineClamp={2}>{description}</Text>
      <Flex justify="space-between" pt="md">
        <Badge>{expectedHours.toFixed(2)} Hrs</Badge>
        <Text weight={500}>
          bidding ends in {formatDistanceStrict(parseJSON(biddingEndTime), new Date())}
        </Text>
      </Flex>
    </Card>
  );
};
