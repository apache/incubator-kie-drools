# Generate Kogito Project based on Spring Boot runtimes

To generate a new project based on Spring Boot, perform the following command:

```shell
mvn archetype:generate \
    -DarchetypeGroupId=org.kie.kogito \
    -DarchetypeArtifactId=kogito-springboot-archetype \
    -DarchetypeVersion=2.0.0-SNAPSHOT \
    -DgroupId=com.company \
    -DartifactId=sample-kogito
```

To customize the generation of your Spring Boot project, see the following sections.

## Adding Kogito Spring Boot Starters

You can generate the project with additional Spring Boot Starters provided by Kogito.

<!-- Include Starters Table or link to the README -->

Run the following command with the property `starters` with every starter you want to be added to the project as a comma
separated list:

```shell
mvn archetype:generate \
    -DarchetypeGroupId=org.kie.kogito \
    -DarchetypeArtifactId=kogito-springboot-archetype \
    -DarchetypeVersion=2.0.0-SNAPSHOT \
    -DgroupId=com.company \
    -DartifactId=sample-kogito
    -Dstarters=decisions,rules,processes
```

The list of valid Kogito Spring Boot Starters are:

1. [`decisions`](../../springboot/starters/kogito-decisions-spring-boot-starter)
2. [`processes`](../../springboot/starters/kogito-processes-spring-boot-starter)
3. [`rules`](../../springboot/starters/kogito-rules-spring-boot-starter)
4. [`serverless-workflows`](../../springboot/starters/kogito-serverless-workflow-spring-boot-starter)
5. [`predictions`](../../springboot/starters/kogito-predictions-spring-boot-starter)

## Adding Kogito Add-Ons

You can add any Kogito Add-Ons available for Spring Boot during the project creation.

Run the following command with the property `addons` with every addon you want to be added to the project as a comma
separated list:

```shell
mvn archetype:generate \
    -DarchetypeGroupId=org.kie.kogito \
    -DarchetypeArtifactId=kogito-springboot-archetype \
    -DarchetypeVersion=2.0.0-SNAPSHOT \
    -DgroupId=com.company \
    -DartifactId=sample-kogito
    -Daddons=monitoring-prometheus,persistence-infinispan
```

For a list of valid add-ons, see these listings:

- [Spring Boot Add-Ons](../../springboot/addons)
- [Runtime Independent Add-Ons](../../addons)

Please note that you don't need to add the suffix `kogito-addons-springboot` to the name of the addon. For example, to
include `kogito-addons-springboot-monitoring-prometheus` just use `monitoring-prometheus`. For runtime independent
add-ons, just suppress the `kogito-addons` suffix. So for `kogito-addons-persistence-infinispan`
use `persistence-infinispan`.

> You can use `starters` and `addons` together to create a Kogito project that meets your requirements.
