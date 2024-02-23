# Kogito Quarkus Extension SPI

SPI module that works as a bridge between [`BuildItem`](https://quarkus.io/guides/writing-extensions#build-items) producers and consumers.

In this scenario, producer is one of the Quarkus extensions such as `jbpm-quarkus`. A consumer can be an add-on such as `kogito-quarkus-addon-knative-eventing`.

This module is meant to be used internally.
