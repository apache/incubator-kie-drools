# Kogito Spring Boot Starters

In this module you will find all
the [Spring Boot Starters](https://github.com/spring-projects/spring-boot/tree/main/spring-boot-project/spring-boot-starters)
provided by the Kogito community.

Before jumping into the starters, consider adding the Kogito Spring Boot BOM to your project:

```xml

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.kie.kogito</groupId>
      <artifactId>kogito-spring-boot-bom</artifactId>
      <version>${version.kogito}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

Replace `${version.kogito}` with the [current version of Kogito](https://github.com/kiegroup/kogito-runtimes/releases).

## Kogito Spring Boot Starter

The `kogito-spring-boot-starter` is an all-in-one descriptor for projects that needs every Business Automation engine
provided by Kogito. It includes Decisions, Rules, Process, Predictions and the Serverless Workflow implementation.

If your project has all these assets, or you're just trying Kogito and want a quick way of getting started, this is the
starter you need. For a more granular approach, consider the specific starters (or a combination of them). See below in
the following sections how to use them.

To add this starter to your project:

```xml

<dependencies>
  <dependency>
    <groupId>org.kie.kogito</groupId>
    <artifactId>kogito-spring-boot-starter</artifactId>
  </dependency>
</dependencies>
```

## Kogito Decisions Spring Boot Starter

Starter only for Decisions (DMN) support. To add it to your project, use:

```xml

<dependencies>
  <dependency>
    <groupId>org.kie.kogito</groupId>
    <artifactId>kogito-decisions-spring-boot-starter</artifactId>
  </dependency>
</dependencies>
```

## Kogito Predictions Spring Boot Starter

Adds Predictions (PMML) to your Kogito Spring Boot project:

```xml

<dependencies>
  <dependency>
    <groupId>org.kie.kogito</groupId>
    <artifactId>kogito-predictions-spring-boot-starter</artifactId>
  </dependency>
</dependencies>
```

## Kogito Processes Spring Boot Starter

To add Kogito Process engine support (BPMN) to your project, use:

```xml

<dependencies>
  <dependency>
    <groupId>org.kie.kogito</groupId>
    <artifactId>kogito-processes-spring-boot-starter</artifactId>
  </dependency>
</dependencies>
```

## Kogito Rules Spring Boot Starter

Adds the Kogito Rules engine support (DRLs) to your project:

```xml

<dependencies>
  <dependency>
    <groupId>org.kie.kogito</groupId>
    <artifactId>kogito-rules-spring-boot-starter</artifactId>
  </dependency>
</dependencies>
```
