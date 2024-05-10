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

# Kogito Kubernetes Add-On

This add-on allows integration with the Kubernetes API and adds Service discovery capabilities to the Kogito Core
engine. Underneath, the add-on uses the [Fabric8 Kubernetes Client](https://github.com/fabric8io/kubernetes-client)
and [Caffeine](https://github.com/ben-manes/caffeine) as the cache implementation.

## Discovery Service

The Kogito Kubernetes Add-on exposes an [`EndpointDiscovery`](src/main/java/org/kie/kogito/addons/k8s/EndpointDiscovery.java) service in the engine. This
means that you can inject this bean in your custom Kogito Service to interact with Kubernetes services deployed in the
same cluster as your service.

An example of usage could be a custom process Service Task to invoke the discovery service to resolve a URL to a
specific service, turning it possible to make HTTP calls to this service in runtime.

Although an approach like this is possible, it's not recommended. The Discovery Service is meant to be used internally
by other components such as the [Rest Work Item handler](https://github.com/kiegroup/kogito-runtimes/tree/main/kogito-workitems/kogito-rest-workitem) or
the [Open API](https://github.com/kiegroup/kogito-runtimes/tree/main/kogito-workitems/kogito-openapi-workitem) one.

### Service Discovery Cache

To avoid round trips to the Kubernetes server every time, each runtime implementation adds a cache layer in between
calls. This cache is highly customizable since its configuration is exposed by the target runtime. For more details
please see the [Quarkus](../../../quarkus/addons/kubernetes) and [Spring Boot](../../../springboot/addons/kubernetes) add-on implementations.

### Usage

Please refer the target runtime add-on implementation for the specific exposed bean. In general, the interaction with
the Discovery Service is pretty straightforward. You can either fetch the endpoint exposed by any [Kubernetes Service](https://kubernetes.io/docs/concepts/services-networking/service/)
or [Knative Route](https://github.com/knative/specs/blob/main/specs/serving/knative-api-specification-1.0.md#route).

The default implementation first tries to find a Kubernetes Service with the given parameters, for example namespace and
service name. If not found, then it falls back to Knative Routes if Knative is available.

You can override this behavior by overriding the `EndpointDiscoveryComposite` class. Or if you're interested only on
Knative routes, for example, you can use the `KnativeRouteEndpointDiscovery` directly. There are many implementations
available that can be used. Just bear in mind that the most optional is the one exposed by the runtime.

### Ports precedence

Sometimes a given Kubernetes service has more than one port exposed. To define which one to create the endpoint URL, the
Discovery Service follows these steps:

1. Uses the value of the `primary-port-name` service label
2. Tries to find a port named `https`
3. Tries to find a port named `http`
4. If none of these ports are found, it takes the first one defined in the list

If you have a service that exposes more than one port it's important to define which one is the primary by adding a
label to your service. Otherwise, the service might return an undesired endpoint URL.

### Kubernetes Permissions

For the service to work, it **requires** `get` permissions on `pods` and `services`. For Knative service, it
requires `get` on `knativeroutes`.

For quick tests, you can add the `ClusterRole` `view` role to your Kogito service pod's `ServiceAccount`. For example:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: my-kogito-service-view
roleRef:
  kind: ClusterRole
  apiGroup: rbac.authorization.k8s.io
  name: view
subjects:
  - kind: ServiceAccount
    name: my-kogito-service
```

## Examples

### Discovery Service with custom WIH

- [The onboarding Example](https://github.com/kiegroup/kogito-examples/tree/stable/kogito-quarkus-examples/onboarding-example) uses this add-on
  to discover other Kogito Process deployed in the same Kubernetes cluster via a custom Work Item Handler.
