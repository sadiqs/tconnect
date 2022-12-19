import { AppShell, createStyles } from "@mantine/core";
import { Outlet } from "react-router-dom";
import AppHeader from "../components/AppHeader";

const useStyles = createStyles((theme) => ({
  header: {},
}));

export function Layout() {
  const { classes, theme } = useStyles();

  return (
    <AppShell padding="md" header={<AppHeader />}>
      <Outlet />
    </AppShell>
  );
}
