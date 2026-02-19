/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.addons.quarkus.kubernetes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.kubernetes.client.KubernetesTestServer;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.kie.kogito.addons.quarkus.k8s.test.utils.KubeTestUtils.createKnativeServiceIfNotExists;

@QuarkusIntegrationTest
@WithKubernetesTestServer
class ConfigValueExpanderIT {

    private static final String NAMESPACE = "default";

    private static final String SERVICENAME = "serverless-workflow-greeting-quarkus";

    @KubernetesTestServer
    io.quarkus.test.kubernetes.client.KubernetesServer mockServer;

    @BeforeEach
    void beforeEach() {
        createKnativeServiceIfNotExists(mockServer.getClient(), "knative/quarkus-greeting.yaml", NAMESPACE, SERVICENAME);
    }

    @Test
    void test() {
        given().when()
                .get("/foo")
                .then()
                .statusCode(200)
                .body(is("http://serverless-workflow-greeting-quarkus.test.10.99.154.147.sslip.io/path"));
    }
}
