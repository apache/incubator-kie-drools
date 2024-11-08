## drools-test-coverage-jars
This project is to build jars that are used in `drools-test-coverage` project. So we can avoid having jar binaries in the codebase.

### How to add a jar project
If the jar is not a kjar, you can simply add the jar project under this project. `surf` project is an example.

If the jar has a fixed version while requires the current version for dependency or plugin (e.g. jar version is `1.0.0`, but requires `999-SNAPSHOT` kie-maven-plugin to build the kjar), use `drools-test-coverage-jars-with-invoker` to build the jar with maven-invoker-plugin. Place the jar project under `src/it`. `kie-poject-simple` is an example.

In both cases, you would need to copy the jar file to the target test project using `copy-rename-maven-plugin`.