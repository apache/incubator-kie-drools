# Deprecated Add-Ons

These add-ons are kept for backward compatibility only and will be removed in future releases. Please **do not** include
them in your project.

## List of Deprecated Add-ons

### Version 1.6.x

The add-on `knative-eventing-addon` has been deprecated, please remove it from your dependencies and use these instead:

```xml

<dependencies>
  <dependency>
    <groupId>org.kie.kogito</groupId>
    <artifactId>kogito-addons-quarkus-cloudevents</artifactId>
  </dependency>
  <dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-reactive-messaging-http</artifactId>
  </dependency>
</dependencies>
```

Please refer to
the [official documentation](https://docs.jboss.org/kogito/release/latest/html_single/#con-knative-eventing_kogito-developing-process-services)
for a detailed example of how to configure your project.

### Version 1.8.x

The artifact IDs for the Kogito Add-ons have changed since version 1.8.x. Please refer to the table below to the new
add-on names to update your project `pom.xml` dependencies section.

Classes and namespaces remains the same, you won't need to make any change in your code base.

#### Quarkus Add-ons

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

#### Spring Boot Add-ons

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

#### Common Add-ons

> [1] Commons Add-ons that are not supposed to be used by end user's projects.

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
