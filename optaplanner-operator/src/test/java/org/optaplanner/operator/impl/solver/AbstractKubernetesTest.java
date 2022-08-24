package org.optaplanner.operator.impl.solver;

import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

@WithKubernetesTestServer
abstract public class AbstractKubernetesTest {

    @KubernetesTestServer
    private KubernetesServer mockServer;

    protected KubernetesServer getMockServer() {
        return mockServer;
    }
}
