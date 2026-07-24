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
package org.kie.kogito.index.postgresql;

import org.junit.jupiter.api.Test;
import org.kie.kogito.index.AbstractProcessDataIndexIT;
import org.kie.kogito.test.quarkus.QuarkusTestProperty;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.kie.kogito.index.test.Constants.KOGITO_DATA_INDEX_SERVICE_URL;

public abstract class AbstractProcessDataIndexPostgreSqlIT extends AbstractProcessDataIndexIT {

    @QuarkusTestProperty(name = KOGITO_DATA_INDEX_SERVICE_URL)
    String dataIndex;

    @Override
    public String getDataIndexURL() {
        return dataIndex;
    }

    @Override
    public boolean validateDomainData() {
        return false;
    }

    @Override
    public boolean validateGetProcessInstanceSource() {
        return true;
    }

    @Test
    public void testJsonQueryVariablesAndMetadata() throws Exception {
        String pId = createTestProcessInstance();

        await().atMost(TIMEOUT).untilAsserted(() -> given().spec(dataIndexSpec())
                .contentType(ContentType.JSON)
                .body("{ \"query\": \"{ ProcessInstances(where: { id: { equal: \\\"" + pId + "\\\" } }) { id variables state nodes {inputArgs outputArgs}} }\" }")
                .when().post("/graphql")
                .then().statusCode(200)
                .body("data.ProcessInstances[0].id", containsString(pId))
                .body("data.ProcessInstances[0].variables.traveller.firstName", containsString("Darth"))
                .body("data.ProcessInstances[0].state", is("ACTIVE")));
    }

}
