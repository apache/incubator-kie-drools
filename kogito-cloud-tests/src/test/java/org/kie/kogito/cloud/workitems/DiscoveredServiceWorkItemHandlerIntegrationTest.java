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
package org.kie.kogito.cloud.workitems;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiscoveredServiceWorkItemHandlerIntegrationTest {

    private static final String personsCrudServiceResourcePath = "/services/persons-crud-service";
    private static final String httpCallsQuarkusServiceResourcePath = "/services/httpcalls-cloud-workitems-quarkus-service";
    private static final String httpCallsSpringbootServiceResourcePath = "/services/httpcalls-cloud-workitems-springboot-service";

    private static final String DEFAULT_NAME = "DEFAULT_NAME";
    private static final String NEW_NAME = "NEW_NAME";

    private static final String personsSvcName = "persons-service";
    private static final String personsSvcBuilderName = personsSvcName + "-builder";
    private static final String httpCallsQuarkusSvcName = "httpcalls-service-quarkus";
    private static final String httpCallsQuarkusSvcBuilderName = httpCallsQuarkusSvcName + "-builder";
    private static final String httpCallsSpringbootSvcName = "httpcalls-service-springboot";
    private static final String httpCallsSpringbootSvcBuilderName = httpCallsSpringbootSvcName + "-builder";

    private static OpenshiftOperations openshiftOps;
    private static HttpCallsServiceOperations httpCallsQuarkusServiceOps;
    private static HttpCallsServiceOperations httpCallsSpringbootServiceOps;

    @BeforeAll
    public static void setupProject() throws UnsupportedEncodingException, InterruptedException {
        String projectName = "kogito-workitems-" + getRandom();
        openshiftOps = new OpenshiftOperations();
        openshiftOps.init(projectName);
        buildAndLoadServices();
        httpCallsQuarkusServiceOps = new HttpCallsServiceOperations(openshiftOps.getHttpRoute(httpCallsQuarkusSvcName));
        httpCallsSpringbootServiceOps = new HttpCallsServiceOperations(openshiftOps.getHttpRoute(httpCallsSpringbootSvcName));

        // Wait for httproute to be available
        openshiftOps.waitFor(() -> httpCallsQuarkusServiceOps.isRouteAvailable(), "Waiting for route to be available for service " + httpCallsQuarkusSvcName, 5L);
        openshiftOps.waitFor(() -> httpCallsSpringbootServiceOps.isRouteAvailable(), "Waiting for route to be available for service " + httpCallsSpringbootSvcName, 5L);
    }

    private static void buildAndLoadServices() {
        // Create Crud & httpcalls builder builds
        openshiftOps.startS2IBinaryBuild(TestConfig.getKaasS2iQuarkusBuilderImage(), personsSvcBuilderName, getResource(personsCrudServiceResourcePath));
        openshiftOps.startS2IBinaryBuild(TestConfig.getKaasS2iQuarkusBuilderImage(), httpCallsQuarkusSvcBuilderName, getResource(httpCallsQuarkusServiceResourcePath));
        openshiftOps.startS2IBinaryBuild(TestConfig.getKaasS2iSpringBootBuilderImage(), httpCallsSpringbootSvcBuilderName, getResource(httpCallsSpringbootServiceResourcePath));
        openshiftOps.waitForBuildCompleted(personsSvcBuilderName, 20L);
        openshiftOps.waitForBuildCompleted(httpCallsQuarkusSvcBuilderName, 20L);
        openshiftOps.waitForBuildCompleted(httpCallsSpringbootSvcBuilderName, 20L);

        // Build final httpcalls image
        openshiftOps.startRuntimeBuild(TestConfig.getKaasQuarkusRuntimeImage(), personsSvcName, personsSvcBuilderName);
        openshiftOps.startRuntimeBuild(TestConfig.getKaasQuarkusRuntimeImage(), httpCallsQuarkusSvcName, httpCallsQuarkusSvcBuilderName);
        openshiftOps.startRuntimeBuild(TestConfig.getKaasSpringBootRuntimeImage(), httpCallsSpringbootSvcName, httpCallsSpringbootSvcBuilderName);
        openshiftOps.waitForBuildCompleted(personsSvcName, 5L);
        openshiftOps.waitForBuildCompleted(httpCallsQuarkusSvcName, 5L);
        openshiftOps.waitForBuildCompleted(httpCallsSpringbootSvcName, 5L);

        Map<String, String> svcLabels = new HashMap<String, String>();
        svcLabels.put("persons", "service");
        openshiftOps.startApp(personsSvcName, new HashMap<>(), svcLabels);
        Map<String, String> envVariables = new HashMap<String, String>();
        envVariables.put("NAMESPACE", openshiftOps.getOpenshift().getNamespace());
        openshiftOps.startApp(httpCallsQuarkusSvcName, envVariables, new HashMap<>());
        openshiftOps.startApp(httpCallsSpringbootSvcName, envVariables, new HashMap<>());
        openshiftOps.waitForPod(personsSvcName, 1, 5L);
        openshiftOps.waitForPod(httpCallsQuarkusSvcName, 1, 5L);
        openshiftOps.waitForPod(httpCallsSpringbootSvcName, 1, 5L);

        openshiftOps.exposeService(httpCallsQuarkusSvcName);
        openshiftOps.exposeService(httpCallsSpringbootSvcName);
        openshiftOps.waitForService(httpCallsQuarkusSvcName, 5L);
        openshiftOps.waitForService(httpCallsSpringbootSvcName, 5L);
    }

    @AfterAll
    private static void removeProject() {
        openshiftOps.delete();
    }

    @Test
    public void testCrudOnPersonsQuarkusService() {
        launchTestCrudOnService(httpCallsQuarkusServiceOps);
    }

    @Test
    public void testCrudOnPersonsSpringbootService() {
        launchTestCrudOnService(httpCallsSpringbootServiceOps);
    }

    @SuppressWarnings("unchecked")
    private void launchTestCrudOnService(HttpCallsServiceOperations httpCallsServiceOperations) {
        // Create person
        Map<String, Object> personMap = new HashMap<>();
        personMap.put("name", DEFAULT_NAME);
        personMap = httpCallsServiceOperations.call("persons", HttpMethods.POST, personMap);
        String personId = retrieveId(personMap);
        assertNotNull(personId);
        assertEquals(DEFAULT_NAME, retrieveName(personMap));

        // Check person exists
        personMap = httpCallsServiceOperations.call("persons", HttpMethods.GET, new HashMap<>());
        List<Map<String, Object>> persons = (List<Map<String, Object>>) personMap.get("persons");
        personMap = persons.stream()
                .filter(p -> personId.equals(retrieveId(p)))
                .findFirst()
                .get();
        assertEquals(DEFAULT_NAME, retrieveName(personMap));

        // Update name
        personMap.put("name", NEW_NAME);
        personMap = httpCallsServiceOperations.call("persons", HttpMethods.PUT, personMap);
        assertEquals(personId, retrieveId(personMap));
        assertEquals(NEW_NAME, retrieveName(personMap));

        // check id exist and its name updated
        personMap = httpCallsServiceOperations.call("persons", HttpMethods.GET, new HashMap<>());
        persons = (List<Map<String, Object>>) personMap.get("persons");
        personMap = persons.stream()
                .filter(p -> personId.equals(retrieveId(p)))
                .findFirst()
                .get();
        assertEquals(NEW_NAME, retrieveName(personMap));

        // delete person & check it has been removed
        personMap = httpCallsServiceOperations.call("persons", HttpMethods.DELETE, personMap);
        personMap = httpCallsServiceOperations.call("persons", HttpMethods.GET, new HashMap<>());
        persons = (List<Map<String, Object>>) personMap.get("persons");
        assertEquals(0, persons.size());
    }

    private static String retrieveId(Map<String, Object> personMap) {
        return (String) personMap.get("id");
    }

    private static String retrieveName(Map<String, Object> personMap) {
        return (String) personMap.get("name");
    }

    private static String getRandom() {
        return RandomStringUtils.randomAlphanumeric(4).toLowerCase();
    }

    private static String getResource(String resourcePath) {
        return DiscoveredServiceWorkItemHandlerIntegrationTest.class.getResource(resourcePath).getFile();
    }
}
