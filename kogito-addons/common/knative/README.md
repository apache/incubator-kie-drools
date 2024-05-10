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

# Kogito Knative Add-Ons

Provides the capability to integrate Kogito projects with [Knative](https://knative.dev/).

## Kogito Quarkus Knative Eventing Add-On

If your project leverage Knative Eventing as messaging platform, consider using this add-on.

The [Kogito Knative Eventing](../../../quarkus/addons/knative/eventing) addon guarantees that the Kogito project can
connect to a given [sink](https://knative.dev/docs/developer/eventing/sinks/). It process
the [`K_SINK`](https://knative.dev/development/developer/eventing/sources/sinkbinding/)
and [`K_CE_OVERRIDES`](https://knative.dev/development/developer/eventing/sources/sinkbinding/reference/#cloudevent-overrides)
environment variables injected by Knative Eventing controllers.

To wire the Kogito service with a given sink, it requires the [Kogito Messaging](../messaging)
and [Quarkus HTTP connector](https://quarkus.io/guides/reactive-messaging-http.html)
libraries. Both are dependencies of this addon.

Please check the
section "[Knative Eventing in Kogito services](https://docs.jboss.org/kogito/release/latest/html_single/#con-knative-eventing_kogito-developing-process-services)"
in our documentation to know more about this addon.

### Examples

Please check the following examples to explore more about this capability:

- [process-knative-quickstart-quarkus](https://github.com/kiegroup/kogito-examples/tree/stable/kogito-quarkus-examples/process-knative-quickstart-quarkus)
- [serverless-workflow-order-processing](https://github.com/kiegroup/kogito-examples/tree/stable/kogito-quarkus-examples/serverless-workflow-order-processing)
