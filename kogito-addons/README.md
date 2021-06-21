# Kogito Add-Ons

In this package you will find the modules responsible to extend the Kogito Core capabilities. They are responsible to
give persistence, monitoring, messaging, and many other features on top of a plain Kogito service.

## Modules organization

At the root of this project you will find three sub-modules:

1. `common` - contains the core features for a specific add-on, not tied to a particular runtime
2. `quarkus` - contains the add-ons for the Quarkus Runtime. If your Kogito project is built with Quarkus, use the
   dependencies listed in this sub-module
3. `springboot` - contains the add-ons for SpringBoot. These dependencies are meant to use only with Kogito projects
   built with SpringBoot

Inside each of these modules, you will find the add-ons organized by its capabilities.

If you don't find a specific capability in a given runtime module, it's because the runtime doesn't support the
capability yet. Please [open a JIRA](https://issues.redhat.com/projects/KOGITO/issues/)
or [start a thread on Zulip](https://kie.zulipchat.com/#)
if you're a looking for a specific capability implementation.

## How to use a Kogito Add-on

Generally speaking, the usage of an add-on is pretty straightforward, you just have to add the dependency to
your `pom.xml` file:

```xml

<dependencies>
  <dependency>
    <groupId>org.kie.kogito</groupId>
    <artifactId>kogito-addons-quarkus-process-management</artifactId>
    <version>${kogito.version}</version>
  </dependency>
</dependencies>
```

> Replace `${kogito.version}` with the desired version

For specific usage, please check the documentation in each add-on module.

Notice that **every** Kogito Add-on starts with `kogito-addons-` prefix. This facilitates your search using IDE:

![](../docsimg/add-on-ide.gif)

In general, the add-on name is composed like `kogito-addons-{runtime}-{capability}-{implementation}`:

- `{runtime}` - can be either `quarkus` or `springboot`.
- `{capability}` - the capability of the add-on such as `persistence`, `eventing`, `cloudevents` and so forth. It's ok
  if your capability have a composite name like `jobs-management`.
- `{implementation}` - (optional) the specific implementation of the given capability. For example, `persistence` can
  have `mongodb` or `postgresql`. This is not a formality, since after the `{runtime}` prefix everything is related to
  the capability. It's more a way to differentiate the implementations.

The core add-ons don't have a `runtime` prefix either because they are the bases for the implementation by a runtime, or
it is a generic implementation that can work either on Quarkus or SpringBoot. Please, check the documentation of the
add-on to find out.

## Creating a new Kogito Add-on

You can either add a brand-new capability to Kogito, or a new implementation for an already supported capability.

### Add a new Capability to Kogito

In this case, you will have to create a new capability from the scratch. To do that, be aware that you should have
knowledge of the Kogito engine internals. Then follow these steps:

1. Create a new sub-module under `common` with a meaningful name. This module can be a parent module if you foresee a
   complex scenario for your new add-on. See [`cloudevents`](common/cloudevents) as an example.
2. Do not use any dependencies from Quarkus or SpringBoot in `common` module. Your add-on must only have code to support
   your capability. New dependencies must be added to the [kogito-build](../kogito-build/kogito-build-parent) BOM.
3. Create the same capability under the runtime you wish to add support (either `quarkus` or `springboot`). Make sure
   that your capability imports either `quarkus-bom` or `springboot-dependencies` BOMs. You can choose to give support
   to only one of them, just make it clear why and discuss this decision with the community.
4. If your capability can have multiple implementations, add at least one flavor under `{runtime}/{capability}` module.
   See [`monitoring`](quarkus/monitoring) as an example
5. Document each top-level module with a `README.md` and make it clear what your add-on is capable to do
6. Create an example of usage in the [`kogito-examples`](https://github.com/kiegroup/kogito-examples) repository

### Add a new Capability Implementation to Kogito

Sometimes, a capability requires a specific implementation. For example, `persistence`. It can be implemented by many
persistence technologies that can their specific details. Each implementation can be handled differently based on the
runtimes supported by Kogito:

1. Start with the runtime you wish to add the implementation. Create a new sub-module under `{runtime}/{capability}`
2. Try to code with the runtime in mind and leverage their libraries. For Quarkus, see
   the [Quarkus Guides](https://quarkus.io/guides/) page to figure how to interact with the given technology. SpringBoot
   also has a comprehensive list of [guides](https://spring.io/guides)
3. Document the implementation with a `README.md` and make it clear how to use it
4. Create an example of usage in the [`kogito-examples`](https://github.com/kiegroup/kogito-examples) repository

If you have questions, feel free to reach out to us at the [KIE Zulip Channel](https://kie.zulipchat.com/#).

## Migration Guide From Kogito 1.9.x

The artifact names (GAV) for the Kogito Add-ons have changed since version 1.9.x. Please refer to the table below to the
new add-on names to update your project `pom.xml` dependencies section.

Classes and namespaces remains the same, you won't need to make any change in your code base.

### Quarkus Add-ons

| Add-On Description  | Old Artifact ID | New Artifact ID |
|---------------------|-----------------|-----------------|
| Cloud Events         | kogito-cloudevents-quarkus-addon | kogito-addons-quarkus-cloudevents | 
| Cloud Events Common  | kogito-cloudevents-quarkus-common-addon | kogito-addons-quarkus-cloudevents-common |
| Cloud Events Multi   | kogito-cloudevents-quarkus-multi-addon | kogito-addons-quarkus-cloudevents-multi | 
| Events Decisions     | kogito-event-driven-decisions-quarkus-addon | kogito-addons-quarkus-events-decisions | 
| Events Smallrye      | kogito-events-reactive-messaging-addon | kogito-addons-quarkus-events-smallrye | 
| Explainability       | explainability-quarkus-addon | kogito-addons-quarkus-explainability | 
| Jobs Management      | jobs-management-quarkus-addon | kogito-addons-quarkus-jobs-management | 
| Mail                 | mail-quarkus-addon | kogito-addons-quarkus-mail |
| Monitoring Core      | monitoring-core-quarkus-addon | kogito-addons-quarkus-monitoring-core | 
| Monitoring Elastic   | monitoring-elastic-quarkus-addon | kogito-addons-quarkus-monitoring-elastic | 
| Monitoring Prometheus | monitoring-prometheus-quarkus-addon | kogito-addons-quarkus-monitoring-prometheus | 
| Persistence ISPN Health | infinispan-quarkus-health-addon | kogito-addons-quarkus-persistence-infinispan-health | 
| Persistence Kafka       | kafka-persistence-addon | kogito-addons-quarkus-persistence-kafka | 
| Process Management      | process-management-addon | kogito-addons-quarkus-process-management | 
| Process SVG             | process-svg-quarkus-addon | kogito-addons-quarkus-process-svg | 
| REST Exception Handler  | kogito-rest-exception-handler-quarkus | kogito-addons-quarkus-rest-exception-handler | 
| Task Management         | task-management-quarkus-addon | kogito-addons-quarkus-task-management | 
| Task Notification       | task-notification-quarkus-addon | kogito-addons-quarkus-task-notification | 
| Tracing Decision        | tracing-decision-quarkus-addon | kogito-addons-quarkus-tracing-decision | 

### SpringBoot Add-ons

| Add-On Description  | Old Artifact ID | New Artifact ID |
|---------------------|-----------------|-----------------|
| Cloud Events         | kogito-cloudevents-spring-boot-addon | kogito-addons-springboot-cloudevents | 
| Events Decisions     | kogito-event-driven-decisions-springboot-addon | kogito-addons-springboot-events-decisions |  
| Events Kafka         | kogito-events-spring-boot-addon | kogito-addons-springboot-events-kafka | 
| Explainability       | explainability-springboot-addon | kogito-addons-springboot-explainability | 
| Jobs Management      | jobs-management-springboot-addon | kogito-addons-springboot-jobs-management | 
| Mail                 | mail-springboot-addon | kogito-addons-springboot-mail |
| Monitoring Core      | monitoring-core-springboot-addon | kogito-addons-springboot-monitoring-core | 
| Monitoring Elastic   | monitoring-elastic-springboot-addon | kogito-addons-springboot-monitoring-elastic | 
| Monitoring Prometheus | monitoring-prometheus-springboot-addon | kogito-addons-springboot-monitoring-prometheus |
| Process Management      | process-management-springboot-addon | kogito-addons-springboot-process-management | 
| Process SVG             | process-svg-springboot-addon | kogito-addons-springboot-process-svg | 
| REST Exception Handler  | kogito-rest-exception-handler-springboot | kogito-addons-springboot-rest-exception-handler | 
| Task Management         | task-management-springboot-addon | kogito-addons-springboot-task-management | 
| Task Notification       | task-notification-springboot-addon | kogito-addons-springboot-task-notification | 
| Tracing Decision        | tracing-decision-springboot-addon | kogito-addons-springboot-tracing-decision | 

### Common Add-ons

> [1] Commons Addon-ons that are not supposed to be used by end user's projects.

| Add-On Description  | Old Artifact ID | New Artifact ID |
|---------------------|-----------------|-----------------|
| Cloud Events Utils [1]  | cloudevents-utils | kogito-addons-cloudevents-utils |
| Cloud Events [1]       | kogito-cloudevents-common-addon | kogito-addons-cloudevents |
| Events Decisions [1]    | kogito-event-driven-decisions-common | kogito-addons-events-decisions |
| Explainability [1]      | explainability-addon-common | kogito-addons-explainability |
| Human Task Prediction API        | kogito-task-prediction-api       | kogito-addons-human-task-prediction-api |
| Human Task Prediction Smile      | kogito-task-prediction-smile-addon | kogito-addons-human-task-prediction-smile |
| Jobs Api [1]            | jobs-api | kogito-addons-jobs-api |
| Jobs Management [1]     | jobs-management-common | kogito-addons-jobs-management-common |
| Mail [1]                | mail-common-addon      | kogito-addons-mail |
| Monitoring [1]          | monitoring-core-common | kogito-addons-monitoring-core |
| Monitoring Elastic [1]  | monitoring-elastic-common | kogito-addons-monitoring-elastic |
| Monitoring Prometheus [1]  | monitoring-prometheus-common | kogito-addons-monitoring-core |
| Persistence Filesystem | filesystem-persistence-addon | kogito-addons-persistence-filesystem | 
| Persistence Infinispan | infinispan-persistence-addon | kogito-addons-persistence-infinispan |
| Persistence JDBC       | jdbc-persistence-addon | kogito-addons-persistence-jdbc |
| Persistence MongoDB    | mongodb-persistence-addon | kogito-addons-persistence-mongodb | 
| Persistence PostgreSQL | postgresql-persistence-addon | kogito-addons-persistence-postgresql |
| Process Management [1]    | process-management-common      | kogito-addons-process-management | 
| Process SVG [1]            | process-svg-common             | kogito-addons-process-svg | 
| REST Exception Handler [1] | kogito-rest-exception-handler-common | kogito-addons-rest-exception-handler | 
| Task Management [1]        | task-management-common | kogito-addons-task-management | 
| Tracing Decision API [1]   | tracing-decision-api   | kogito-addons-tracing-decision-api | 
| Tracing Decision Common [1] | tracing-decision-common | kogito-addons-tracing-decision-common | 
| Tracing TypedValue API [1] | typedvalue-api | kogito-addons-tracing-typedvalue-api |
