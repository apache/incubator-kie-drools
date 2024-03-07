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

## jBPM with Drools Spring Boot Starter

The `jbpm-with-drools-spring-boot-starter` is an all-in-one descriptor for projects that needs every Business Automation engine. 
It includes Decisions, Rules, Process, Predictions and the Serverless Workflow implementation.

If your project has all these assets or you want a quick way of getting started, this is the
starter you need. For a more granular approach, consider the specific starters (or a combination of them). See below in
the following sections how to use them.

To add this starter to your project:

```xml

<dependencies>
  <dependency>
    <groupId>org.jbpm</groupId>
    <artifactId>jbpm-with-drools-spring-boot-starter</artifactId>
  </dependency>
</dependencies>
```

## Drools Decisions Spring Boot Starter

Starter only for Decisions (DMN) support. To add it to your project, use:

```xml

<dependencies>
  <dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-decisions-spring-boot-starter</artifactId>
  </dependency>
</dependencies>
```

## Kogito Predictions Spring Boot Starter

Adds Predictions (PMML) to your Kogito Spring Boot project:

```xml

<dependencies>
  <dependency>
    <groupId>org.kie</groupId>
    <artifactId>kie-predictions-spring-boot-starter</artifactId>
  </dependency>
</dependencies>
```

## jBPM Spring Boot Starter

To add jBPM engine support (BPMN) to your project, use:

```xml

<dependencies>
  <dependency>
    <groupId>org.jbpm</groupId>
    <artifactId>jbpm-spring-boot-starter</artifactId>
  </dependency>
</dependencies>
```

## Drools Rules Spring Boot Starter

Adds the Drools Rules engine support (DRLs) to your project:

```xml

<dependencies>
  <dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-rules-spring-boot-starter</artifactId>
  </dependency>
</dependencies>
```
