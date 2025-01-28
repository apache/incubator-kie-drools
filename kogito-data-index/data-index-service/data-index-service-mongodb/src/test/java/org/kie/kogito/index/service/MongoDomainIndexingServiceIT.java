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
package org.kie.kogito.index.service;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.kie.kogito.index.service.test.InMemoryMessageTestProfile;
import org.kie.kogito.index.test.TestUtils;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.isA;

@QuarkusTest
@TestProfile(InMemoryMessageTestProfile.class)
class MongoDomainIndexingServiceIT extends AbstractDomainIndexingServiceIT {

    @Override
    protected String getProcessProtobufFileContent() throws Exception {
        return TestUtils.readFileContent("travels-mongo.proto");
    }

    @Override
    protected String getUserTaskProtobufFileContent() throws Exception {
        return TestUtils.readFileContent("deals-mongo.proto");
    }

    @Test
    void testAddProtoFileTwice() throws Exception {
        protobufService.registerProtoBufferType(getProtoBufferFileV1());
        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Game{ player, id, name, metadata { processInstances { id } } } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Game", isA(Collection.class));
        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{ProcessInstances{ id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.ProcessInstances", isA(Collection.class));

        protobufService.registerProtoBufferType(getProtoBufferFileV2());
        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Game{ id, name, company, metadata { processInstances { id } } } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Game", isA(Collection.class));
        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{ProcessInstances{ id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.ProcessInstances", isA(Collection.class));
    }

    private String getProtoBufferFileV1() {
        return "package org.demo;\n" +
                "import \"kogito-index.proto\";\n" +
                "option kogito_model=\"Game\";\n" +
                "option kogito_id=\"game\";\n" +
                "/* @Indexed */\n" +
                "message Game {\n" +
                "   optional string player = 1;\n" +
                "   /* @Field(index = Index.YES, store = Store.YES) @SortableField */\n" +
                "   optional string id = 2;\n" +
                "   optional string name = 3;\n" +
                "   optional org.kie.kogito.index.model.KogitoMetadata metadata = 4;\n" +
                "}\n" +
                "\n";
    }

    private String getProtoBufferFileV2() {
        return "package org.demo;\n" +
                "import \"kogito-index.proto\";\n" +
                "option kogito_model=\"Game\";\n" +
                "option kogito_id=\"game\";\n" +
                "/* @Indexed */\n" +
                "message Game {\n" +
                "   /* @Field(index = Index.YES, store = Store.YES) @SortableField */\n" +
                "   optional string id = 1;\n" +
                "   optional string name = 2;\n" +
                "   optional string company = 3;\n" +
                "   optional org.kie.kogito.index.model.KogitoMetadata metadata = 4;\n" +
                "}\n" +
                "\n";
    }
}
