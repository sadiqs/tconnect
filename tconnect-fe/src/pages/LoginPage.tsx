import {
  Alert,
  Button,
  Container,
  createStyles,
  Paper,
  PasswordInput,
  Text,
  TextInput,
  Title,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import { useLoginMutation } from "../api/tconnectApi";
import { UserCredentials } from "../store/authSlice";
import { Navigate, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { IconAlertCircle } from "@tabler/icons";
import { useAuthUser } from "../hooks/useAuthUser";

const useStyles = createStyles((theme) => ({
  header: {},
}));

export function LoginPage() {
  const { classes, theme } = useStyles();
  const [error, setError] = useState<string>();
  const navigate = useNavigate();
  const user = useAuthUser();
  const [doLogin, { isLoading }] = useLoginMutation();

  const form = useForm<UserCredentials>({
    initialValues: {
      username: "",
      password: "",
    },
  });

  const clearError = () => {
    if (error) {
      setError(undefined);
    }
  };

  if (user?.user) {
    return <Navigate to="/" />;
  }

  return (
    <Container size="md">
      <Title
        align="center"
        sx={(theme) => ({
          fontFamily: `${theme.fontFamily}`,
          fontWeight: 900,
        })}
      >
        Login
      </Title>
      <Text color="dimmed" size="sm" align="center" mt={5}>
        Login with your username and password
      </Text>

      <Paper withBorder shadow="md" p={30} mt={30} radius="md">
        {error && (
          <Alert mb="md" icon={<IconAlertCircle size={16} />} color="red">
            {error}
          </Alert>
        )}
        <form
          onSubmit={form.onSubmit(async (values) => {
            try {
              const details = await doLogin(values).unwrap();
              console.log("Auth details", details);
              navigate("/");
            } catch (err) {
              const { status } = err as { status: number };
              if (status == 401) {
                setError("Invalid username or password. Please try again.");
              }
            }
          })}
        >
          <TextInput
            label="Username"
            placeholder="username"
            required
            value={form.values.username}
            onChange={(event) => {
              clearError();
              form.setFieldValue("username", event.currentTarget.value);
            }}
            error={form.errors.username && "Invalid username"}
          />
          <PasswordInput
            label="Password"
            placeholder="Your password"
            required
            mt="md"
            value={form.values.password}
            onChange={(event) => {
              clearError();
              form.setFieldValue("password", event.currentTarget.value);
            }}
            error={form.errors.password && "Invalid password"}
          />

          <Button fullWidth mt="md" type="submit">
            Sign in
          </Button>
        </form>
      </Paper>
    </Container>
  );
}
