import { FC } from "react";
import { ProjectCard } from "./ProjectCard";
import { Button, Container, SimpleGrid } from "@mantine/core";
import { ProjectOrId, Project } from "../model";
import { useNavigate } from "react-router-dom";

export interface ProjectListProps {
  projects: ProjectOrId[];
}

export const ProjectList: FC<ProjectListProps> = ({
  projects: projectOrIds,
}) => {
  const projectMap = new Map(
    projectOrIds
      .filter((p) => typeof p !== "string")
      .map((p) => p as Project)
      .map((p) => [p.id, p])
  );

  const projects = projectOrIds.map((p) => {
    if (typeof p === "string") {
      return projectMap.get(p)!!;
    } else {
      return p;
    }
  });

  return (
    <SimpleGrid cols={1}>
      {projects.map((project) => (
        <ProjectCard key={project.id} {...project} />
      ))}
    </SimpleGrid>
  );
};
