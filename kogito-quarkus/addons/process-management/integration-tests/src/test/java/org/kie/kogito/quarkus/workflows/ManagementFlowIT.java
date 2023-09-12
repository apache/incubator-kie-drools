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
package org.kie.kogito.quarkus.workflows;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.serverless.workflow.SWFConstants;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@QuarkusIntegrationTest
class ManagementFlowIT {

    @Test
    void testManagementAPINodes() {
        assertThat(given().contentType(ContentType.JSON).accept(ContentType.JSON).get("management/processes/parallel/nodes")
                .then().statusCode(200).extract().as(new TypeRef<List<Map<String, Object>>>() {
                }).stream().map(m -> (Map<String, Object>) m.get("metadata")).filter(m -> m.containsKey(SWFConstants.STATE_NAME) && m.containsKey(SWFConstants.ACTION_NAME)
                        && m.containsKey(SWFConstants.BRANCH_NAME))
                .count()).isGreaterThanOrEqualTo(3);
    }

    @Test
    void testManagementAPIProcess() {
        given().contentType(ContentType.JSON).accept(ContentType.JSON).get("management/processes/parallel")
                .then().statusCode(200).body("annotations", is(Arrays.asList("Football", "Betis")));
    }
}
