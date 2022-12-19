import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import {
  UserDetails,
  ProjectSeed,
  Project,
  Bid,
  BidSeed,
  BidOrId,
} from "../model";
import type { UserCredentials } from "../store/authSlice";
import { normalizeBids, normalizeProjects } from "../utils/responseNormalizers";
import { ProjectOrId } from "../model";

export const tconnectApi = createApi({
  reducerPath: "tconnectApi",
  baseQuery: fetchBaseQuery({ baseUrl: "/api/" }),
  tagTypes: ["projects", "bids"],
  endpoints: (builder) => ({
    login: builder.mutation<UserDetails, UserCredentials>({
      query: ({ username, password }) => ({
        url: `user`,
        method: "GET",
        headers: {
          Authorization: "Basic " + btoa(`${username}:${password}`),
        },
      }),
      invalidatesTags: ["projects", "bids"],
    }),
    getProjects: builder.query<Project[], {}>({
      query: () => ({
        url: `projects`,
        method: "GET",
      }),
      providesTags: ["projects"],
      transformResponse: (response: ProjectOrId[], meta, arg) => {
        return normalizeProjects(response);
      },
    }),
    getBids: builder.query<Bid[], {}>({
      query: () => ({
        url: `bids`,
        method: "GET",
      }),
      providesTags: ["bids"],
      transformResponse: (response: BidOrId[], meta, arg) => {
        return normalizeBids(response);
      },
    }),
    createProject: builder.mutation<Project, ProjectSeed>({
      query: (projectSeed) => ({
        url: `projects`,
        method: "POST",
        body: projectSeed,
      }),
      invalidatesTags: ["projects"],
    }),
    createBid: builder.mutation<Bid, BidSeed>({
      query: (bidSeed) => ({
        url: `bids`,
        method: "POST",
        body: bidSeed,
      }),
      invalidatesTags: ["bids"],
    }),
  }),
});

export const {
  useLoginMutation,
  useCreateProjectMutation,
  useCreateBidMutation,
  useGetProjectsQuery,
  useGetBidsQuery,
} = tconnectApi;
