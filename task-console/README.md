# task-console project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
mvn quarkus:dev
```
Note: Live coding of the React JS frontend application is not yet in place.

## Packaging and running the application

The application is packageable using:
```
mvn package
```
It produces the executable `task-console-8.0.0-SNAPSHOT-runner.jar` file in `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using:
```
java -jar target/task-console-8.0.0-SNAPSHOT-runner.jar
```

> Note: task-console requires keycloak server to be initialied for authentication.

## Creating a native executable

You can create a native executable using: 
```
mvn package -Dnative
```

Or you can use Docker to build the native executable using:
```
mvn package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your binary: `./target/task-console-8.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide .

## Packaging together with the React app


The application makes use of a separately developed [React UI application](../ui-packages/packages/task-console/package.json). The JS based frontend can be built as part of the build of this project by using the profile defined in dependency [ui-packages](../ui-packages/pom.xml), invoked by default. Using the property `-Dskip.ui.build` as in following command you can skip the build of UI and use what is already built in the respective package:

```
mvn package -Dskip.ui.build
```

To prepare all the dependencies needed for the build of UI, there's a maven profile activated by default. Using the `-Dskip.ui.deps` property you can skip the profile.

The single command to disable both UI build related profiles is:
```
mvn package -Dskip.ui.deps -Dskip.ui.build
```

## Creating a native executable
The native build of the application bundling in the React JS frontend does not differ from the instructions above. The only thing that's new is again the invocation of UI specific profiles.
```
mvn package -Dui -Dnative
```

## Enabling Keycloak security

### Starting and Configuring the Keycloak Server

To start a Keycloak Server you can use Docker and just run the following command:

```
docker run -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -e KEYCLOAK_IMPORT=/tmp/kogito-realm.json -v {absolute_path}/kogito-apps/config/kogito-realm.json:/tmp/kogito-realm.json -p 8280:8080 jboss/keycloak
```

You should be able to access your Keycloak Server at [localhost:8280/auth](http://localhost:8280)
and verify keycloak server is running properly: log in as the admin user to access the Keycloak Administration Console. 
Username should be admin and password admin.

The following are the users available in keycloak

| Login         | Password   | Roles               |
| ------------- | ---------- | ------------------- |
|    admin      |   admin    | *admin*, *managers* |
|    alice      |   alice    | *user*              |
|    jdoe       |   jdoe     | *managers*          |

To change any of this client configuration access to http://localhost:8280/auth/admin/master/console/#/realms/kogito.

### Working with Task Console features

The task console shows a list of user tasks which are available for a process. Each column contains detailed information about the user task which are - *Name*, *Process*, *Priority*, *Status*, *Started* and *Last update*. The columns are sortable.

![Task Console](./docs/taskconsole.png?raw=true "TaskConsole")

The task console consist of filters, which can be used to narrow down the search on a user task. There are two filters available

- A filter based on the status(dropdown)
- A filter based on the task name(text search)

![Filters](./docs/filters.png?raw=true "Filters")

The *Status* filter can be dropped down to view the and select the states available

![States](./docs/states.png?raw=true "States")

A *refresh* button is available to refresh the list of user tasks

A *Reset to default* button is available to reset the filters to its initial state.

The user task list also supports pagination.

Clicking on the name of the user task will navigate to another screen, which consist of the auto generated forms.

### Task details

The task details page consist of an auto generated forms and buttons to perform corresponding action on the user tasks.

![Forms](./docs/forms.png?raw=true "Forms")

The task details page also contains a *View details* button, to view more details about the task.

![Details](./docs/details.png?raw=true "Details")

### Starting Kogito Task Console in dev mode

Start the task console at port 8380, (the keycloak client 'kogito-console-quarkus' is configured to use that port )
and enabling auth:

```
mvn clean compile quarkus:dev -Dquarkus.http.port=8380
```
