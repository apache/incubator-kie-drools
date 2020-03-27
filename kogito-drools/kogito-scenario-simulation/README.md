Kogito Test Scenario runner
===========================

This module is a wrapper for Kogito of test scenario runner from `drools` repo.

It contains a JUnit runner to make it possible to run `*.scesim` files via Maven build.

NOTE: it works with JUnit 5 vintange engine

How to use
----------

If you have one or more `*.scesim` files in your project to execute, add this dependency to project POM

```xml
<dependency>
    <groupId>org.kie.kogito</groupId>
    <artifactId>kogito-scenario-simulation</artifactId>
    <version>${kogito.version}</version>
</dependency>
```

And then create `KogitoScenarioJunitActivatorTest.java` file in `src/test/java/testscenario` with this content

```java
package testscenario;

/**
 * KogitoJunitActivator is a custom JUnit runner that enables the execution of Test Scenario files (*.scesim).
 * This activator class, when executed, will load all scesim files available in the project and run them.
 * Each row of the scenario will generate a test JUnit result. */
@org.junit.runner.RunWith(org.kogito.scenariosimulation.runner.KogitoJunitActivator.class)
public class KogitoScenarioJunitActivatorTest {
}
```

After that execute `mvn clean test` to execute it (you can also execute `KogitoScenarioJunitActivatorTest` in your IDE)