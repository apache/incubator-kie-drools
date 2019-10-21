/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.cloud.workitems.service.discovery;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cz.xtf.builder.builders.ServiceBuilder;
import cz.xtf.core.config.OpenShiftConfig;
import cz.xtf.core.openshift.OpenShift;
import cz.xtf.core.openshift.OpenShifts;
import cz.xtf.core.waiting.SimpleWaiter;
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.cloud.kubernetes.client.DefaultKogitoKubeClient;
import org.kie.kogito.cloud.kubernetes.client.KogitoKubeConfig;
import org.kie.kogito.cloud.workitems.ServiceInfo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 
 */
public class KubernetesServiceDiscoveryIntegrationTest {

    private static final String SERVICE1_NAME = "service1";
    private static final String SERVICE2_NAME = "service2";

    private static final String DEFAULT_LABEL_KEY = "keyLabel1";
    private static final String DEFAULT_LABEL_VALUE = "valueLabel1";

    private static KubernetesServiceDiscovery kubernetesServiceDiscovery;
    private static OpenShift openshift;

    private ServiceManager svcManager;

    @BeforeAll
    public static void setupProject() throws UnsupportedEncodingException {
        // Create project
        String projectName = "kogito-discovery-" + RandomStringUtils.randomAlphanumeric(4).toLowerCase();
        OpenShifts.master().createProjectRequest(projectName);
        openshift = OpenShifts.master(projectName);

        // give default serviceaccount view rights
        openshift.addRoleToServiceAccount("view", "default");

        String tokenSecretName = openshift.getServiceAccount("default").getSecrets().stream().map(ObjectReference::getName).filter(secretName -> secretName.contains("token")).findFirst().get();
        String tokenBase64 = openshift.getSecret(tokenSecretName).getData().get("token");
        String token = new String(Base64.decodeBase64(tokenBase64), "UTF-8");

        // Setup Kubernetes service discovery  
        Config config = new ConfigBuilder().withMasterUrl(OpenShiftConfig.url())
                .withOauthToken(token)
                .withTrustCerts(true)
                .build();
        kubernetesServiceDiscovery = new KubernetesServiceDiscovery(new DefaultKogitoKubeClient().withConfig(new KogitoKubeConfig(new DefaultKubernetesClient(config))));
    }

    @AfterAll
    public static void tearDownProject() {
        openshift.deleteProject();
    }

    @BeforeEach
    public void setUp() {
        svcManager = new ServiceManager(openshift);
    }

    @AfterEach
    public void tearDown() {
        svcManager.clearServices();
        svcManager = null;
    }

    @Test
    public void findSingleEndpoint() {
        Service service = svcManager.createSimpleService(SERVICE1_NAME, DEFAULT_LABEL_KEY, DEFAULT_LABEL_VALUE);

        assertExistingEndpoint(DEFAULT_LABEL_KEY, DEFAULT_LABEL_VALUE);
        assertEndpointUrlWithServices(DEFAULT_LABEL_KEY, DEFAULT_LABEL_VALUE, service);
    }

    @Test
    public void findEndpointFrom2ServicesWithSameLabel() {
        Service service1 = svcManager.createSimpleService(SERVICE1_NAME, DEFAULT_LABEL_KEY, DEFAULT_LABEL_VALUE);
        Service service2 = svcManager.createSimpleService(SERVICE2_NAME, DEFAULT_LABEL_KEY, DEFAULT_LABEL_VALUE);

        assertExistingEndpoint(DEFAULT_LABEL_KEY, DEFAULT_LABEL_VALUE);
        assertEndpointUrlWithServices(DEFAULT_LABEL_KEY, DEFAULT_LABEL_VALUE, service1, service2);
    }

    @Test
    public void findEndpointFrom2ServicesWithSameLabelButDifferentValues() {
        String otherLabelValue = "otherLabelValue";
        Service service1 = svcManager.createSimpleService(SERVICE1_NAME, DEFAULT_LABEL_KEY, DEFAULT_LABEL_VALUE);
        Service service2 = svcManager.createSimpleService(SERVICE2_NAME, DEFAULT_LABEL_KEY, otherLabelValue);

        assertExistingEndpoint(DEFAULT_LABEL_KEY, DEFAULT_LABEL_VALUE);
        assertExistingEndpoint(DEFAULT_LABEL_KEY, otherLabelValue);
        assertEndpointUrlWithServices(DEFAULT_LABEL_KEY, DEFAULT_LABEL_VALUE, service1);
        assertEndpointUrlWithServices(DEFAULT_LABEL_KEY, otherLabelValue, service2);
    }

    @Test
    public void findEndpointNoServiceDeployed() {
        assertNoEndpoint(DEFAULT_LABEL_KEY, DEFAULT_LABEL_VALUE);
    }

    @Test
    public void findEndpointServiceDeployedWithDifferentValue() {
        String otherLabelValue = "otherLabelValue";
        svcManager.createSimpleService(SERVICE1_NAME, DEFAULT_LABEL_KEY, otherLabelValue);

        assertNoEndpoint(DEFAULT_LABEL_KEY, DEFAULT_LABEL_VALUE);
    }

    private void assertExistingEndpoint(String label, String value) {
        Optional<ServiceInfo> optEndpoint = getExistingEndpoint(label, value);
        assertTrue("No service with label " + label + " and value " + value + " defined", optEndpoint.isPresent());
    }

    private void assertNoEndpoint(String label, String value) {
        Optional<ServiceInfo> optEndpoint = getExistingEndpoint(label, value);
        assertFalse("There should not be any service with label " + label + " and value " + value, optEndpoint.isPresent());
    }

    private void assertEndpointUrlWithServices(String label, String value, Service... services) {
        if (services == null || services.length < 1) {
            throw new RuntimeException("No service given to check with");
        }

        String endpointUrl = getEndpointUrl(label, value);
        String errStr = "Endpoint url shoud start with " + Arrays.stream(services).map(this::buildClusterIPUrl).collect(Collectors.joining(" or "));
        errStr += ". Got: " + endpointUrl;
        assertTrue(errStr, Arrays.stream(services).map(this::buildClusterIPUrl).anyMatch(endpointUrl::startsWith));
    }

    private Optional<ServiceInfo> getExistingEndpoint(String label, String value) {
        return kubernetesServiceDiscovery.findEndpoint(openshift.getNamespace(), label, value);
    }

    private String getEndpointUrl(String label, String value) {
        Optional<ServiceInfo> optEndpoint = getExistingEndpoint(label, value);
        if (optEndpoint.isPresent()) {
            return optEndpoint.get().getUrl();
        } else {
            throw new RuntimeException("No endpoint defined with label " + label + " and value " + value);
        }

    }

    private String buildClusterIPUrl(Service service) {
        return "http://" + service.getSpec().getClusterIP() + ":8080";
    }

    static class ServiceManager {

        private OpenShift openshift;
        private Set<Service> services = new HashSet<>();

        public ServiceManager(OpenShift openshift) {
            super();
            this.openshift = openshift;
        }

        public Service createSimpleService(String name, String defaultLabelKey, String defaultLabelValue) {
            Map<String, String> labels = new HashMap<>();
            labels.put(defaultLabelKey, defaultLabelValue);
            return createSimpleService(name, labels);
        }

        public Service createSimpleService(String name, Map<String, String> labels) {
            ServiceBuilder builder = new ServiceBuilder(name).port(8080);
            labels.entrySet().forEach(e -> builder.addLabel(e.getKey(), e.getValue()));
            Service service = openshift.createService(builder.build());
            services.add(service);
            return service;
        }

        public void clearServices() {
            services.stream().forEach(service -> {
                String svcName = service.getMetadata().getName();
                assertTrue("Problem deleting service " + svcName, openshift.deleteService(service));
                // Be sure service is deleted, to avoid future conflicts
                assertTrue(new SimpleWaiter(() -> openshift.getService(svcName) == null, TimeUnit.SECONDS, 5, "Wait for service1 to be deleted").waitFor());
            });
        }
    }
}
