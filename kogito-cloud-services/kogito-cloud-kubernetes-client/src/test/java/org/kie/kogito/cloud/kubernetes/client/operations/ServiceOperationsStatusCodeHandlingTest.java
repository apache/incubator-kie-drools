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

import java.net.HttpURLConnection;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.cloud.kubernetes.client.KogitoKubeClientException;
import org.kie.kogito.cloud.kubernetes.client.MockKubernetesServerSupport;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Service Operations test cases that integrates with a mock Kubernetes server to validate HTTP Rest API handling.
 */
public class ServiceOperationsStatusCodeHandlingTest extends MockKubernetesServerSupport {

    public ServiceOperationsStatusCodeHandlingTest() {
        super(false);
    }

    @Test
    public void whenNotFoundResponse() {
        getServer().expect().get().withPath("/api/v1/services").andReturn(404, null).once();
        Map<String, Object> services = this.getKubeClient().services().list(null).asMap();
        assertThat(services, notNullValue());
        assertThat(services.size(), is(0));
    }

    @Test
    public void whenForbiddenResponse() {
        try {
            getServer().expect().get().withPath("/api/v1/services").andReturn(HttpURLConnection.HTTP_FORBIDDEN, null).once();
            this.getKubeClient().services().list(null).asMap();
            fail("Should explode a forbidden exception");
        } catch (KogitoKubeClientException e) {
            assertThat(e.getMessage(), containsString("forbidden"));
        }
    }

    @Test
    public void whenUnauthorizedResponse() {
        try {
            getServer().expect().get().withPath("/api/v1/services").andReturn(HttpURLConnection.HTTP_UNAUTHORIZED, null).once();
            this.getKubeClient().services().list(null).asMap();
            fail("Should explode a forbidden exception");
        } catch (KogitoKubeClientException e) {
            assertThat(e.getMessage(), containsString("unauthorized"));
        }
    }

    @Test
    public void whenServerError() {
        getServer().expect().get().withPath("/api/v1/services").andReturn(HttpURLConnection.HTTP_BAD_GATEWAY, null).once();
        assertThrows(KogitoKubeClientException.class, () -> this.getKubeClient().services().list(null).asMap());
    }
}
