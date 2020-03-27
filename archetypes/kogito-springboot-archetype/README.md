# Generate KaaS project based on Quarkus runtime

To generate new project for KaaS (Kjar as a Service) based on Quarkus use following command

    ```
     mvn archetype:generate \
    -DarchetypeGroupId=org.kie.kogito \
    -DarchetypeArtifactId=kogito-springboot-archetype \
    -DarchetypeVersion=8.0.0-SNAPSHOT \
    -DgroupId=com.company \
    -DartifactId=sample-kogito
    ```