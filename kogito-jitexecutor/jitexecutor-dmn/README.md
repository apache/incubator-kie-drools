# Kogito JIT DMN Executor

## Log configuration

Rest endpoints have log configuration to eventually print out received payload, if log is set to DEBUG level (default is INFO).
This is managed in the application.properties, that is under filtered-resources.
The `quarkus.log.category."org.kie.kogito".level` is set from `${jitexecutor.dmn.log.level}`, that by default is "INFO" in the pom.xml.

It can be overridden with commandline parameter, e.g.

`mvn clean package -Djitexecutor.dmn.log.level=DEBUG`
