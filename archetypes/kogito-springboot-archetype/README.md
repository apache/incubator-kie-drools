# Generate KaaS project based on Spring Boot runtime

To generate new project for KaaS (Kjar as a Service) based on Spring Boot use following command

    ```
     mvn archetype:generate \
    -DarchetypeGroupId=org.kie.kogito \
    -DarchetypeArtifactId=kogito-springboot-archetype \
    -DarchetypeVersion=1.0.0-SNAPSHOT \
    -DgroupId=com.company \
    -DartifactId=sample-kogito
    ```