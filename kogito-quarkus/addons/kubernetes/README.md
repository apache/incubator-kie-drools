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

# Kogito Quarkus Kubernetes Add-On

See the [main README](../../../addons/common/kubernetes) for a full description and examples of this add-on.

## Kubernetes Client

This add-on relies on the Quarkus Kubernetes Client extension to integrate with the Kubernetes API. It should work fine
out of the box without any extra setup. Please refer to the [Quarkus guide](https://quarkus.io/guides/kubernetes-client)
for more information about any needed customization.

## Caching

To avoid round trips to the Kubernetes Core API, this implementation uses the [Quarkus Application Data Caching extension](https://quarkus.io/guides/cache)
with the default configuration.

The default configuration should be enough for most use cases, but if you need to fine tune the cache for your needs,
please refer to the [Quarkus guide](https://quarkus.io/guides/cache#configuring-the-underlying-caching-provider).

You can configure the internal caches by their names: `endpoint-by-name` and `endpoint-by-labels`. 

## Usage

This extension exposes the bean [`CachedServiceAndThenRouteEndpointDiscovery`](runtime/src/main/java/org/kie/kogito/addons/quarkus/k8s/CachedServiceAndThenRouteEndpointDiscovery.java).
You can inject it into your custom Kogito service and start using it:

```java
import java.util.Optional;

import org.kie.kogito.addons.k8s.Endpoint;
import org.kie.kogito.addons.k8s.EndpointDiscovery;

@ApplicationScoped
public class EndpointFetcher {

    @Inject
    EndpointDiscovery endpointDiscovery;

    public void queryEndpoint(String namespace, String name) {
        final Optional<Endpoint> endpoint = endpointDiscovery.findEndpoint(namespace, name);
        if (endpoint.isEmpty()) {
            System.out.println("Endpoint not found :(");
        } else {
            System.out.println("This is the url for the service " + name + ": " + endpoint.get().getURL());
        }
    }
}
```


## Kubernetes Service Discovery

The Service discovery documentation can be found at this [link](https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/cloud/kubernetes-service-discovery.html).


