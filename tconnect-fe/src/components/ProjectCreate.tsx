import {
  Box,
  Button,
  Group,
  Textarea,
  TextInput,
  Container,
  Title,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import { FC, useState } from "react";
import { Navigate, useParams, useNavigate } from "react-router-dom";
import { useAuthUser } from "../hooks/useAuthUser";
import { normalizeProjects } from "../utils/responseNormalizers";
import { DatePicker, TimeInput } from "@mantine/dates";
import { createApi } from "@reduxjs/toolkit/query/react";
import { Project, ProjectSeed } from "../model";
import { useCreateProjectMutation } from "../api/tconnectApi";

export const ProjectCreate: FC<{}> = () => {
  const { user } = useAuthUser();
  const [createProject] = useCreateProjectMutation();
  const navigate = useNavigate();

  if (user?.type !== "customer") {
    return <Navigate to="/" />;
  }

  const form = useForm({
    initialValues: {
      title: "",
      description: "",
      expectedHours: 40,
      biddingEndDate: new Date(),
      biddingEndTime: new Date(),
    },
  });

  const submitNewProject = async (projectSeed: ProjectSeed) => {
    try {
      const p = await createProject(projectSeed).unwrap();
      console.log("New project", p);
      navigate("/");
    } catch (err) {
      console.error("Error creating project", err);
    }
  };

  return (
    <Container>
      <Title>Create a new project</Title>
      <form
        onSubmit={form.onSubmit((values) => {
          values.biddingEndTime.setMonth(values.biddingEndDate.getMonth());
          values.biddingEndTime.setDate(values.biddingEndDate.getDate());
          submitNewProject({ ...values });
        })}
      >
        <TextInput
          withAsterisk
          label="Title"
          placeholder="project title"
          mt="md"
          {...form.getInputProps("title")}
        />
        <Textarea
          placeholder="A brief description of the project"
          label="Description"
          withAsterisk
          mt="md"
          {...form.getInputProps("description")}
        />

        <Group mt={"md"}>
          <TextInput
            withAsterisk
            label="Expected hours"
            {...(form.getInputProps("expectedHours"), { type: "number" })}
          />
          <DatePicker
            label="Bidding end time"
            withAsterisk
            {...form.getInputProps("biddingEndDate")}
          />
          <TimeInput
            label="Bidding end time"
            withAsterisk
            {...form.getInputProps("biddingEndTime")}
          />
        </Group>
        <Group position="right" mt="md">
          <Button type="submit">Submit</Button>
        </Group>
      </form>
    </Container>
  );
};
