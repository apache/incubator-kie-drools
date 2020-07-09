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

## Working with task-console features
