# tconnect
 
The `tconnect` app connects tradies with customers. The app implemented with frontend in React and backend using Spring Boot.

## Organization
The project is organized into two modules,
- `tconnect-fe` -- The frontend react app.
- `tconnect-be` -- The backend service, provides APIs used by frontend.

## Run
The supporting infrastructure, backend and frontend needs to started individually.
- Start the database and kafka by using supplied `docker-compose.yml` file in `tconnect-be`.
    ```
    cd tconnect-be
    docker compose up
    ```
- Start the backend service. The app has two types of users, a "customer" and "tradie". The backend service will seed two users, a customer user `customer1` and a tradie user `tradie1` and as well a project when it starts up. The password for these users is same as username.
    ```
    cd tconnect-be
    ./mvnw clean install && ./mvnw spring-boot:run
    ```
- Start the frontend service. `pnpm` tool is required and `pnpm install` needs to be run only the first time.
    ```
    cd tconnect-fe
    pnpm install
    pnpm dev
    ```

