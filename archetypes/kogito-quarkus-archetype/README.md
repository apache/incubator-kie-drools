# Generate KaaS project based on Quarkus runtime

To generate new project for KaaS (Kjar as a Service) based on [Quarkus](https://quarkus.io/) use following command

    ```
     mvn archetype:generate \
    -DarchetypeGroupId=org.kie.kogito \
    -DarchetypeArtifactId=kogito-quarkus-archetype \
    -DarchetypeVersion=1.0.0-SNAPSHOT \
    -DgroupId=com.company \
    -DartifactId=sample-kogito
    ```