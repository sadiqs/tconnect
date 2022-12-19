import {
  Badge,
  Button,
  Card,
  Container,
  Flex,
  Grid,
  Group,
  Paper,
  SimpleGrid,
  Text,
  TextInput,
} from "@mantine/core";
import { IconArrowNarrowRight } from "@tabler/icons";
import { FC } from "react";
import { Project, BidSeed } from "../model";
import { Navigate, useParams, useNavigate } from "react-router-dom";
import { useAuthUser } from "../hooks/useAuthUser";
import { normalizeProjects } from "../utils/responseNormalizers";
import { useCreateBidMutation, useGetProjectsQuery } from "../api/tconnectApi";
import { useForm } from "@mantine/form";
import { Title } from "@mantine/core";

export const ProjectView: FC<{}> = () => {
  const { projectId } = useParams();
  const { user } = useAuthUser();
  const role = user?.type;
  const { data } = useGetProjectsQuery({});
  const [placeBid] = useCreateBidMutation();
  const navigate = useNavigate();

  if (!role) {
    return <Navigate to="/" />;
  }

  const form = useForm<BidSeed>({
    initialValues: {
      projectId: "",
      amount: 0,
    },
  });

  if (!data) {
    return <Navigate to="/" />;
  }

  const project = data.find((p) => p.id === projectId)!!;

  const { id, title, description, biddingEndTime, expectedHours } = project;

  return (
    <Container>
      <Grid gutterMd={40}>
        <Grid.Col span={4}>
          <Text align="right" fw={600} italic>
            Project Title
          </Text>
        </Grid.Col>
        <Grid.Col span={8}>
          <Text>{title}</Text>
        </Grid.Col>
        <Grid.Col span={4}>
          <Text align="right" fw={600} italic>
            Description
          </Text>
        </Grid.Col>
        <Grid.Col span={8}>
          <Text>{description}</Text>
        </Grid.Col>
        <Grid.Col span={4}>
          <Text align="right" fw={600} italic>
            Bidding end time
          </Text>
        </Grid.Col>
        <Grid.Col span={8}>
          <Text>{biddingEndTime}</Text>
        </Grid.Col>
        <Grid.Col span={4}>
          <Text align="right" fw={600} italic>
            Estimated hours
          </Text>
        </Grid.Col>
        <Grid.Col span={8}>
          <Text>{expectedHours.toFixed(2)} Hrs</Text>
        </Grid.Col>

        {role == "tradie" && (
          <>
            <Grid.Col span={12} offset={2} mt="lg">
              <Title order={3}>Interested? Place your bid</Title>
            </Grid.Col>
            <Grid.Col span={3} offset={2}>
              <form
                onSubmit={form.onSubmit(async (bidSeed) => {
                  bidSeed.projectId = id;
                  await placeBid(bidSeed);
                  navigate("/bids");
                })}
              >
                {/* <Flex justify="center"> */}
                <TextInput
                  withAsterisk
                  label="Amount"
                  required
                  value={form.values.amount}
                  onChange={(event) => {
                    form.setFieldValue(
                      "amount",
                      Number(event.currentTarget.value)
                    );
                  }}
                  {...(form.getInputProps("amount"), { type: "number" })}
                />
                <Button type="submit" mt="md">
                  Submit
                </Button>
                {/* </Flex> */}
              </form>
            </Grid.Col>
          </>
        )}
      </Grid>
    </Container>
  );
};
