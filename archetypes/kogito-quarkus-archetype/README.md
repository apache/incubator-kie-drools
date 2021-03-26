# Generate a Kogito project based on Quarkus runtime

To generate new project for Kogito based on [Quarkus](https://quarkus.io/) use following command

```shell
mvn archetype:generate \
    -DarchetypeGroupId=org.kie.kogito \
    -DarchetypeArtifactId=kogito-quarkus-archetype \
    -DarchetypeVersion=2.0.0-SNAPSHOT \
    -DgroupId=com.company \
    -DartifactId=sample-kogito
```