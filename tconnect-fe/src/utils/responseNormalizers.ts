import { Bid, BidOrId, Project, ProjectOrId } from "../model";

export const normalizeProjects = (projectOrIds: ProjectOrId[]): Project[] => {
  const innerProjects: Project[] = [];

  const projects = projectOrIds
    .filter((p) => typeof p !== "string")
    .map((p) => p as Project)
    .map((p) => {
      innerProjects.push(
        ...(p.bids
          .filter((b) => typeof b !== "string")
          .map((b) => b as Bid)
          .map((b) => b.project)
          .filter((p) => typeof p !== "string") as Project[])
      );
      return p;
    });

  projects.push(...innerProjects);

  return projects;
};

export const normalizeBids = (bidOrIds: BidOrId[]): Bid[] => {
  const innerBids: Bid[] = [];

  const bids = bidOrIds
    .filter((b) => typeof b !== "string")
    .map((b) => b as Bid)
    .map((b) => {
      if (typeof b.project !== "string") {
        innerBids.push(
          ...(b.project.bids.filter((ib) => typeof ib !== "string") as Bid[])
        );
      }
      return b;
    });

  bids.push(...innerBids);

  return bids;
};
