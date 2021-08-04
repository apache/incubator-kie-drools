# Kogito CloudEvents Add-on

In this module you will find the shared libraries for CloudEvents handling.

## Message Payload Decorator

The [`MessagePayloadDecorator`](common/src/main/java/org/kie/kogito/addon/cloudevents/message/MessagePayloadDecorator.java)
can be implemented by any other dependant add-on. In order to do that, create a file
named `META-INF/services/org.kie.kogito.addon.cloudevents.message.MessagePayloadDecorator`
in your classpath. The content of this file must be the full name of your implementation class.

The `MessagePayloadDecoratorProvider` will load it upon the application startup and add it to the decoration chain. This
means that once
the [`MessagePayloadDecoratorProvider#decorate`](common/src/main/java/org/kie/kogito/addon/cloudevents/message/MessagePayloadDecoratorProvider.java)
is called, your implementation will be part of the decoration algorithm.
