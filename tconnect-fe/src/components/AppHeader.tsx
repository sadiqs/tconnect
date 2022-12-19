import {
  Container,
  Button,
  Header,
  Text,
  Title,
  Flex,
  Box,
  Group,
} from "@mantine/core";
import React from "react";
import { useLogoutMutation } from "../api/tconnectApi";
import { useAuthUser } from "../hooks/useAuthUser";
import { useNavigate } from "react-router-dom";

export default function AppHeader() {
  const { user: { name: displayName } = {} } = useAuthUser();
  const [logout] = useLogoutMutation();
  const navigate = useNavigate();

  return (
    <Container size="md">
      <Header height={{ base: 50, md: 70 }} p="md">
        <Box pl={140} pr={140}>
          <Flex justify="space-between" align="center">
            <Title order={1} variant="gradient">
              Tradie Connect
            </Title>
            <Group>
              <Text> {displayName}</Text>
              {displayName && (
                <Button
                  onClick={async () => {
                    await logout({});
                    navigate("/");
                  }}
                >
                  Logout
                </Button>
              )}
            </Group>
          </Flex>
        </Box>
      </Header>
    </Container>
  );
}
