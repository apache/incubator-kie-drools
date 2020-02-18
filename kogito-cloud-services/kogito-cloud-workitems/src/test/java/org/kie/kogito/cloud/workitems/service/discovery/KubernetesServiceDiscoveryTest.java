/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.cloud.workitems.service.discovery;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.cloud.kubernetes.client.DefaultKogitoKubeClient;
import org.kie.kogito.cloud.kubernetes.client.KogitoKubeConfig;
import org.kie.kogito.cloud.workitems.ServiceInfo;

import static org.assertj.core.api.Assertions.assertThat;

public class KubernetesServiceDiscoveryTest {

    public static final String NAMESPACE = "mockns";

    public static final String SERVICE_PROTOCOL = "http";
    public static final int SERVICE_PORT = 8080;

    public KubernetesServer server = new KubernetesServer(true, true);

    public KubernetesServiceDiscovery kubernetesServiceDiscovery;

    @BeforeEach
    public void before() {
        server.before();
        kubernetesServiceDiscovery = new KubernetesServiceDiscovery(new DefaultKogitoKubeClient().withConfig(new KogitoKubeConfig(server.getClient())));
    }

    @AfterEach
    public void after() {
        server.after();
    }

    @Test
    public void findSingleEndpoint() {
        String serviceLabelName = "test-kogito";
        String serviceLabelValue = "service";
        String serviceName = "test-kogito-service";
        String serviceIp = "172.30.158.30";
        String serviceUrl = getServiceUrl(serviceIp) + "/" + serviceLabelValue;

        createServiceInMockServer(serviceName, serviceIp, Collections.singletonMap(serviceLabelName, serviceLabelValue));

        Optional<ServiceInfo> endpoint = kubernetesServiceDiscovery.findEndpoint(NAMESPACE, serviceLabelName, serviceLabelValue);
        assertThat(endpoint).hasValueSatisfying(serviceInfo -> {
            assertThat(serviceInfo.getUrl()).isEqualTo(serviceUrl);
        });
    }

    @Test
    public void findEndpointFromTwoServicesWithSameLabel() {
        String sharedServiceLabelName = "test-kogito";
        String sharedServiceLabelValue = "service";
        String serviceOneName = "test-kogito-service";
        String serviceOneIp = "172.30.158.31";
        String serviceOneUrl = getServiceUrl(serviceOneIp) + "/" + sharedServiceLabelValue;
        String serviceTwoName = "test-kogito-service-two";
        String serviceTwoIp = "172.30.158.32";
        String serviceTwoUrl = getServiceUrl(serviceTwoIp) + "/" + sharedServiceLabelValue;

        createServiceInMockServer(serviceOneName, serviceOneIp, Collections.singletonMap(sharedServiceLabelName, sharedServiceLabelValue));
        createServiceInMockServer(serviceTwoName, serviceTwoIp, Collections.singletonMap(sharedServiceLabelName, sharedServiceLabelValue));

        Optional<ServiceInfo> endpoint = kubernetesServiceDiscovery.findEndpoint(NAMESPACE, sharedServiceLabelName, sharedServiceLabelValue);
        // Returns one of the endpoints (order is not specified)
        assertThat(endpoint).hasValueSatisfying(serviceInfo -> {
            assertThat(serviceInfo.getUrl()).isIn(serviceOneUrl, serviceTwoUrl);
        });
    }

    @Test
    public void findEndpointFromTwoServicesWithSameLabelsButDifferentValues() {
        String sharedServiceLabelName = "test-kogito";
        String serviceOneLabelValue = "service";
        String serviceOneName = "test-kogito-service";
        String serviceOneIp = "172.30.158.31";
        String serviceOneUrl = getServiceUrl(serviceOneIp) + "/" + serviceOneLabelValue;
        String serviceTwoLabelValue = "servicetwo";
        String serviceTwoName = "test-kogito-service-two";
        String serviceTwoIp = "172.30.158.32";
        String serviceTwoUrl = getServiceUrl(serviceTwoIp) + "/" + serviceTwoLabelValue;

        createServiceInMockServer(serviceOneName, serviceOneIp, Collections.singletonMap(sharedServiceLabelName, serviceOneLabelValue));
        createServiceInMockServer(serviceTwoName, serviceTwoIp, Collections.singletonMap(sharedServiceLabelName, serviceTwoLabelValue));

        Optional<ServiceInfo> endpoint = kubernetesServiceDiscovery.findEndpoint(NAMESPACE, sharedServiceLabelName, serviceOneLabelValue);
        assertThat(endpoint).hasValueSatisfying(serviceInfo -> {
            assertThat(serviceInfo.getUrl()).isEqualTo(serviceOneUrl);
        });
        Optional<ServiceInfo> endpointTwo = kubernetesServiceDiscovery.findEndpoint(NAMESPACE, sharedServiceLabelName, serviceTwoLabelValue);
        assertThat(endpointTwo).hasValueSatisfying(serviceInfo -> {
            assertThat(serviceInfo.getUrl()).isEqualTo(serviceTwoUrl);
        });
    }

    @Test
    public void findEndpointNoServiceDeployed() {
        String serviceLabelName = "test-kogito";
        String serviceLabelValue = "service";

        Optional<ServiceInfo> endpoint = kubernetesServiceDiscovery.findEndpoint(NAMESPACE, serviceLabelName, serviceLabelValue);
        assertThat(endpoint).isEmpty();
    }

    @Test
    public void findEndpointServiceDeployedWithDifferentValue() {
        String sharedServiceLabelName = "test-kogito";
        String serviceOneLabelValue = "service";
        String serviceOneName = "test-kogito-service";
        String serviceOneIp = "172.30.158.31";
        String serviceTwoLabelValue = "servicetwo";

        createServiceInMockServer(serviceOneName, serviceOneIp, Collections.singletonMap(sharedServiceLabelName, serviceOneLabelValue));

        Optional<ServiceInfo> endpoint = kubernetesServiceDiscovery.findEndpoint(NAMESPACE, sharedServiceLabelName, serviceTwoLabelValue);
        assertThat(endpoint).isEmpty();
    }

    private void createServiceInMockServer(String name, String serviceIp, Map<String,String> labels) {
        final ServicePort port = new ServicePort(SERVICE_PROTOCOL, 0, SERVICE_PORT, SERVICE_PROTOCOL, new IntOrString(SERVICE_PORT));
        final Service service = new ServiceBuilder().withNewMetadata()
                                                        .withName(name)
                                                        .withLabels(labels)
                                                    .endMetadata()
                                                    .withNewSpec()
                                                        .withClusterIP(serviceIp)
                                                        .withType("ClusterIP")
                                                        .withSessionAffinity("ClientIP")
                                                        .withPorts(port)
                                                    .endSpec()
                                                    .build();
        server.getClient().services().inNamespace(NAMESPACE).create(service);
    }

    private String getServiceUrl(String serviceIp) {
        return SERVICE_PROTOCOL + "://" + serviceIp + ":" + SERVICE_PORT;
    }
}
