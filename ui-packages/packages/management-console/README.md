# Management Console

## Local Development using mock backend server

This will start the frontend and backend using a mock server. The GraphiQL UI for the mock server will be available on http://localhost:4000/graphql
if you would like to explore the data.  

```bash
pnpm run dev
```

## Local Development using [Data Index](https://github.com/kiegroup/kogito-runtimes/wiki/Data-Index-Service) server

Instead of starting a mock backend, you can connect to a running Data Index server which by default will be running on
http://localhost:8180.

```bash
pnpm run dev-remote-dataindex
```

## Executing tests

```bash
pnpm run test
```

## GraphQL Codegen

To generate new GraphQL hook queries using [graphql-code-generator](https://graphql-code-generator.com/docs/plugins/typescript-react-apollo),
please edit [queries.tsx](./src/graphql/queries.tsx) adding you new query. Then start the Data Index service and run:

```bash
pnpm run codegen
```

That will update the [types.tsx](./src/graphql/types.tsx) file, including the new GraphQL query hooks.

## Security

### Starting and Configuring the Keycloak Server

To start a Keycloak Server you can use Docker and just run the following command:

```
docker run -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin  -e KEYCLOAK_IMPORT=/tmp/kogito-realm.json -v <kogito-apps_absolutepath>/config/kogito-realm.json:/tmp/kogito-realm.json -p 8280:8080  jboss/keycloak
```

You should be able to access your Keycloak Server at http://localhost:8280/auth.
and verify keycloak server is running properly: log in as the admin user to access the Keycloak Administration Console. 
Username should be admin and password admin.


### Enabling security
 
Two new scripts have been added
Run to work with dev server and authorization enabled:
```bash
pnpm run dev-auth
```

To work with remote Kogito Data Index service and authorization enabled:
```bash
pnpm run dev-remote-dataindex-auth
```

