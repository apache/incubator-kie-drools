# Kogito Cloud Kubernetes Client

This library is designed to be used with Kogito Cloud module, but also as a standalone library as an alternative Kubernetes client on [native images](https://www.graalvm.org/docs/reference-manual/aot-compilation/).

It's a wrapper around the [Fabric8 Kubernetes Client API](https://github.com/fabric8io/kubernetes-client), with some tweaks:

1. Removes the Kube Config file parsing as an alternative to connect to the cluster because of [its reflection usage](https://github.com/fabric8io/kubernetes-client/issues/1591).
2. Parse JSON API responses as simple Maps to avoid using reflection to build domain objects during runtime like Jackson does. As an alternative, uses Yasson to parse JSON responses and a simple utility to walk into the resulted map.

It's meant to be simple to support simple use cases.