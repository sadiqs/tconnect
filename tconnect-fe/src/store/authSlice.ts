import { createSlice } from "@reduxjs/toolkit";
import { UserDetails } from "../model";
import { tconnectApi } from "../api/tconnectApi";
import type { RootState } from "./store";

export interface UserCredentials {
  username: string;
  password: string;
}

interface AuthState {
  user?: UserDetails;
}

const initialState: AuthState = {};

export const authSlice = createSlice({
  name: "authSlice",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder.addMatcher(
      tconnectApi.endpoints.login.matchFulfilled,
      (state, { payload, meta, type }) => {
        console.log("In AuthSlice", payload, meta, type);
        state.user = payload;
      }
    );
    builder.addMatcher(
      tconnectApi.endpoints.logout.matchFulfilled,
      (state, { payload, meta, type }) => {
        console.log("In AuthSlice logout", payload, meta, type);
        state.user = undefined;
      }
    );
  },
});

export const {} = authSlice.actions;

export const selectCurrentUser = (state: RootState) => state.auth?.user;

export default authSlice.reducer;
