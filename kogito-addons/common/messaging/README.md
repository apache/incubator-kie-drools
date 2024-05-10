<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

# Kogito Messaging Add-on

These add-ons provide a default implementation in our supported target platforms for EventEmitter and EventReceiver interfaces.
EventEmitter and EventReceiver interfaces are used for enabling messaging by process, for serverless workflow events and for event decision handling.
See the following sections in our documentation to find out more:

- [Knative Eventing in Kogito services](https://docs.jboss.org/kogito/release/latest/html_single/#con-knative-eventing_kogito-developing-process-services)
- [Serverless Workflow definitions](https://docs.jboss.org/kogito/release/latest/html_single/#con-serverless-workflow-definitions_kogito-orchestrating-serverless)
- [Enabling Kafka messaging for Kogito services](https://docs.jboss.org/kogito/release/latest/html_single/#proc-messaging-enabling_kogito-configuring)

## Examples

Please check the following examples to explore more about this capability:

- [kogito-travel-agency](https://github.com/kiegroup/kogito-examples/tree/stable/kogito-quarkus-examples/kogito-travel-agency)
- [process-kafka-multi-quarkus](https://github.com/kiegroup/kogito-examples/tree/stable/kogito-quarkus-examples/process-kafka-multi-quarkus)
- [process-kafka-multi-springboot](https://github.com/kiegroup/kogito-examples/tree/stable/kogito-springboot-examples/process-kafka-multi-springboot)
- [process-kafka-quickstart-quarkus](https://github.com/kiegroup/kogito-examples/tree/stable/kogito-quarkus-examples/process-kafka-quickstart-quarkus)
- [process-kafka-quickstart-springboot](https://github.com/kiegroup/kogito-examples/tree/stable/kogito-springboot-examples/process-kafka-quickstart-springboot)
- [process-knative-quickstart-quarkus](https://github.com/kiegroup/kogito-examples/tree/stable/kogito-quarkus-examples/process-knative-quickstart-quarkus)
- [serverless-workflow-github-showcase](https://github.com/kiegroup/kogito-examples/tree/stable/kogito-quarkus-examples/serverless-workflow-github-showcase)
- [serverless-workflow-service-calls-quarkus](https://github.com/kiegroup/kogito-examples/tree/stable/kogito-quarkus-examples/serverless-workflow-service-calls-quarkus)
- [serverless-workflow-temperature-conversion](https://github.com/kiegroup/kogito-examples/tree/stable/kogito-quarkus-examples/serverless-workflow-temperature-conversion)

## Message Payload Decorator

The [`MessagePayloadDecorator`](common/src/main/java/org/kie/kogito/addon/cloudevents/message/MessagePayloadDecorator.java)
can be implemented by any other dependant add-on. In order to do that, create a file
named `META-INF/services/org.kie.kogito.addon.cloudevents.message.MessagePayloadDecorator`
in your classpath. The content of this file must be the full name of your implementation class.

The `MessagePayloadDecoratorProvider` will load it upon the application startup and add it to the decoration chain. This
means that once
the [`MessagePayloadDecoratorProvider#decorate`](common/src/main/java/org/kie/kogito/addon/cloudevents/message/MessagePayloadDecoratorProvider.java)
is called, your implementation will be part of the decoration algorithm.
