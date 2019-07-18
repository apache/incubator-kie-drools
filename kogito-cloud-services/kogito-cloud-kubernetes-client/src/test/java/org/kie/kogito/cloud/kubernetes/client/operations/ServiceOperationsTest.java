/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.cloud.kubernetes.client.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.kie.kogito.cloud.kubernetes.client.MockKubernetesServerSupport;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ServiceOperationsTest extends MockKubernetesServerSupport {

    private void assertDefaultServiceCreated(final String services) throws JsonParseException, JsonMappingException, IOException {
        this.assertDefaultServiceCreated(services, "127.0.0.1");
    }

    private void assertDefaultServiceCreated(final String services, final String assertIp) throws JsonParseException, JsonMappingException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        assertThat(services, notNullValue());
        assertThat(services.isEmpty(), not(true));
        final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
        };
        Map<String, Object> servicesMap = mapper.readValue(services, typeRef);
        Map<String, Object> spec =
                new MapWalker(servicesMap)
                                          .mapToListMap("items")
                                          .listToMap(0)
                                          .mapToMap("spec")
                                          .asMap();
        assertThat(spec.get("clusterIP"), is(assertIp));
        assertThat(Integer.parseInt(new MapWalker(spec).mapToListMap("ports").listToMap(0).asMap().get("port").toString()), is(8080));
    }

    @Test
    public void whenThereIsOneSingleServiceNamespaced() throws JsonParseException, JsonMappingException, IOException {
        this.createMockService();
        String services = this.getKubeClient().services().listNamespaced(MOCK_NAMESPACE, null).asJson();
        this.assertDefaultServiceCreated(services);
    }

    @Test
    public void whenThereIsAListOfServicesNamespaced() throws JsonParseException, JsonMappingException, IOException {
        this.createMockService();
        this.createMockService("service2", "192.168.0.1", Collections.singletonMap("service", "test2"), MOCK_NAMESPACE);
        String services = this.getKubeClient().services().listNamespaced(MOCK_NAMESPACE, null).asJson();
        this.assertDefaultServiceCreated(services, "192.168.0.1");
    }
    
    @Test
    public void whenThereIsAListOfServicesNamespacedLookingForLabelKey() throws JsonParseException, JsonMappingException, IOException {
        this.createMockService("service2", "192.168.0.1", Collections.singletonMap("service", null), MOCK_NAMESPACE);
        String services = this.getKubeClient().services().listNamespaced(MOCK_NAMESPACE, Collections.singletonMap("service", null)).asJson();
        this.assertDefaultServiceCreated(services, "192.168.0.1");
    }

    @Test
    public void whenThereIsAListOfServicesNamespacedWithLabel() throws JsonParseException, JsonMappingException, IOException {
        this.createMockService();
        this.createMockService("service2", "192.168.0.1", Collections.singletonMap("service", "test2"), MOCK_NAMESPACE);
        String servicesJson = this.getKubeClient()
                                  .services()
                                  .listNamespaced(MOCK_NAMESPACE, Collections.singletonMap("service", "test2"))
                                  .asJson();
        this.assertDefaultServiceCreated(servicesJson, "192.168.0.1");
    }

    @Test
    public void whenThereIsOneServiceNamespacedReturnedAsMap() {
        this.createMockService();
        Map<String, Object> services =
                this.getKubeClient()
                    .services()
                    .listNamespaced(MOCK_NAMESPACE, null)
                    .asMap();
        assertThat(services, notNullValue());
        assertThat(services.size(), is(3));
        assertThat(services.get("items"), instanceOf(ArrayList.class));
    }

    @Test
    public void whenThereIsOneSingleService() throws JsonParseException, JsonMappingException, IOException {
        this.createMockService("");
        String services = this.getKubeClient().services().list(null).asJson();
        this.assertDefaultServiceCreated(services);
    }

}
