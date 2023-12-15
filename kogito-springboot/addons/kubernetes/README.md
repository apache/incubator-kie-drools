# Kogito Spring Boot Kubernetes Add-On

See the [main README](../../../addons/common/kubernetes) for a full description and examples of this add-on.

## Kubernetes Client

This add-on relies on
the [Spring Cloud Kubernetes client](https://docs.spring.io/spring-cloud-kubernetes/docs/current/reference/html/) to
integrate with the Kubernetes API. It should work fine out of the box without any extra setup. Please refer to
the [Spring Boot guide](https://docs.spring.io/spring-cloud-kubernetes/docs/current/reference/html/#discoveryclient-for-kubernetes)
for more information about any needed customization.

When using this add-on is important to set the following properties in your test environment:

```properties
spring.main.cloud-platform=KUBERNETES
spring.cloud.bootstrap.enabled=true
```

This will guarantee that he right `KubernetesClient` bean is created for you. See more at [Kubernetes Ecosystem Awareness](https://docs.spring.io/spring-cloud-kubernetes/docs/current/reference/html/#kubernetes-ecosystem-awareness).

## Caching

To avoid round trips to the Kubernetes Core API, this implementation uses the [Spring Boot Cache feature](https://docs.spring.io/spring-boot/docs/2.1.6.RELEASE/reference/html/boot-features-caching.html)
with the default configuration.

The default configuration should be enough for most use cases, but if you need to fine tune the cache for your needs,
please refer to the [Spring Boot Caffeine documentation](https://docs.spring.io/spring-boot/docs/2.1.6.RELEASE/reference/html/boot-features-caching.html#boot-features-caching-provider-caffeine).

You can configure the internal caches by their names: `endpoint-by-name` and `endpoint-by-labels`.

## Usage

This extension exposes the
bean [`CachedServiceAndThenRouteEndpointDiscovery`](runtime/src/main/java/org/kie/kogito/addons/quarkus/k8s/CachedServiceAndThenRouteEndpointDiscovery.java)
. You can inject it into your custom Kogito service and start using it:

````java

import java.util.Optional;

import org.kie.kogito.addons.k8s.Endpoint;
import org.kie.kogito.addons.k8s.EndpointDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EndpointFetcher {

    @Autowired
    EndpointDiscovery endpointDiscovery;

    public void queryEndpoint(String namespace, String name) {
        final Optional<Endpoint> endpoint = endpointDiscovery.findEndpoint(namespace, name);
        if (endpoint.isEmpty()) {
            System.out.println("Endpoint not found :(");
        } else {
            System.out.println("This is the url for the service " + name + ": " + endpoint.get().getUrl());
        }
    }
}
````
