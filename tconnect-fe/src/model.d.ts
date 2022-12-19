export type UserDetails = CustomerDetails | TradieDetails;

export interface CustomerDetails {
  id: string;
  username: string;
  name: string;

  projects: ProjectOrId[];
  type: "customer";
}

export interface TradieDetails {
  id: string;
  username: string;
  name: string;
  trade: string;
  experience: number;
  activeBids: BidOrId[];
  type: "tradie";
}

export interface ProjectSeed {
  title: string;
  description: string;
  expectedHours: number;
  biddingEndTime: Date;
}

export type AppRole = "customer" | "tradie";
export type BidOrId = Bid | string;
export type ProjectOrId = Project | string;

export interface Project {
  id: string;
  title: string;
  description: string;
  expectedHours: number;
  biddingEndTime: string;
  bids: BidOrId[];
}

export interface Bid {
  id: string;
  amount: number;
  project: ProjectOrId;
}

export interface BidSeed {
  amount: number;
  projectId: string;
}
