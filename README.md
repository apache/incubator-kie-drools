Contributing to Kogito
--------------------

All contributions are welcome! Before you start please read the [Developing Drools and jBPM](https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/README.md) guide.

Building from source
--------------------

Check out the source:
```
git clone git@github.com:kiegroup/kogito-apps.git
```

> If you don't have a GitHub account use this command instead:
> ```
> git clone https://github.com/kiegroup/kogito-apps.git
> ```

Build with Yarn:
```bash
cd kogito-apps
yarn run init

#prod
yarn run build:prod

# dev
yarn run build # skips integration tests and production packing
yarn run build:fast # skips lint and unit tests
```

> Final artifacts will be on `packages/*/dist` directories.


### Starting and Configuring the Keycloak Server

To start a Keycloak Server you can use Docker and just run the following command:

```
docker run -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin  -e KEYCLOAK_IMPORT=/tmp/kogito-realm.json -v <kogito-apps_absolutepath>/config/kogito-realm.json:/tmp/kogito-realm.json -p 8280:8080  jboss/keycloak
```

You should be able to access your Keycloak Server at [localhost:8280/auth](http://localhost:8280).
and verify keycloak server is running properly: log in as the admin user to access the Keycloak Administration Console. 
Username should be admin and password admin.


### Enabling security
 
Two new scripts have been added
Run to work with dev server and authorization enabled:
```bash
yarn run dev-auth
```

To work with remote kodito dataindex service and authorization enabled:
```bash
yarn run dev-remote-dataindex-auth
```

