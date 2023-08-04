`drools-reference-examples` contains internal reference examples which can be used for smoke tests, backward compatibility tests, etc.

To run the project, enable the `reference` profile:

    cd drools-reference-examples
    mvn clean install -Preference

By default, kjar projects are built with the current project version of `kie-maven-plugin`. To build with a specific version of `kie-maven-plugin`, use `reference.kjar.drools.version` property:

    mvn clean install -Preference -Dreference.kjar.drools.version=8.39.0.Final

