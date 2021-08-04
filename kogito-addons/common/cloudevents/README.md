# Kogito CloudEvents Add-on

Use this add-on to make the events produced and consumed by a Kogito project accept
the [CloudEvents](https://cloudevents.io) format. There are many use cases where you can use this add-on.
See the following sections in our documentation to find out more:

- [Knative Eventing in Kogito services](https://docs.jboss.org/kogito/release/latest/html_single/#con-knative-eventing_kogito-developing-process-services)
- [Serverless Workflow definitions](https://docs.jboss.org/kogito/release/latest/html_single/#con-serverless-workflow-definitions_kogito-orchestrating-serverless)
- [Kogito runtime events](https://docs.jboss.org/kogito/release/latest/html_single/#con-knative-eventing_kogito-developing-process-services)
- [Enabling Kafka messaging for Kogito services](https://docs.jboss.org/kogito/release/latest/html_single/#proc-messaging-enabling_kogito-configuring)
- [Kogito Data Index Service](https://docs.jboss.org/kogito/release/latest/html_single/#con-data-index-service_kogito-configuring)
- [Kogito Trusty Service and Explainability Service](https://docs.jboss.org/kogito/release/latest/html_single/#con-trusty-service_kogito-configuring)

## Examples

Please check the following examples to explore more about this capability:

- [kogito-travel-agency](https://github.com/kiegroup/kogito-examples/tree/stable/kogito-travel-agency)
- [process-kafka-multi-quarkus](https://github.com/kiegroup/kogito-examples/tree/stable/process-kafka-multi-quarkus)
- [process-kafka-multi-springboot](https://github.com/kiegroup/kogito-examples/tree/stable/process-kafka-multi-springboot)
- [process-kafka-quickstart-quarkus](https://github.com/kiegroup/kogito-examples/tree/stable/process-kafka-quickstart-quarkus)
- [process-kafka-quickstart-springboot](https://github.com/kiegroup/kogito-examples/tree/stable/process-kafka-quickstart-springboot)
- [process-knative-quickstart-quarkus](https://github.com/kiegroup/kogito-examples/tree/stable/process-knative-quickstart-quarkus)
- [serverless-workflow-github-showcase](https://github.com/kiegroup/kogito-examples/tree/stable/serverless-workflow-github-showcase)
- [serverless-workflow-service-calls-quarkus](https://github.com/kiegroup/kogito-examples/tree/stable/serverless-workflow-service-calls-quarkus)
- [serverless-workflow-temperature-conversion](https://github.com/kiegroup/kogito-examples/tree/stable/serverless-workflow-temperature-conversion)

## Message Payload Decorator

The [`MessagePayloadDecorator`](common/src/main/java/org/kie/kogito/addon/cloudevents/message/MessagePayloadDecorator.java)
can be implemented by any other dependant add-on. In order to do that, create a file
named `META-INF/services/org.kie.kogito.addon.cloudevents.message.MessagePayloadDecorator`
in your classpath. The content of this file must be the full name of your implementation class.

The `MessagePayloadDecoratorProvider` will load it upon the application startup and add it to the decoration chain. This
means that once
the [`MessagePayloadDecoratorProvider#decorate`](common/src/main/java/org/kie/kogito/addon/cloudevents/message/MessagePayloadDecoratorProvider.java)
is called, your implementation will be part of the decoration algorithm.
