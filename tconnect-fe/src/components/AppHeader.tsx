import {
  Container,
  Button,
  Header,
  Text,
  Title,
  Flex,
  Box,
} from "@mantine/core";
import React from "react";
import { useAuthUser } from "../hooks/useAuthUser";

export default function AppHeader() {
  const { user: { name: displayName } = {} } = useAuthUser();

  return (
    <Container size="md">
      <Header height={{ base: 50, md: 70 }} p="md">
        <Box pl={140} pr={140}>
          <Flex justify="space-between" align="center">
            <Title order={1} variant="gradient">
              Tradie Connect
            </Title>
            <Text> {displayName}</Text>
          </Flex>
        </Box>
      </Header>
    </Container>
  );
}
