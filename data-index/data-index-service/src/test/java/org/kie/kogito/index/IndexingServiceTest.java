/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.index;

import java.util.Collection;
import java.util.UUID;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.event.KogitoCloudEvent;
import org.kie.kogito.index.infinispan.protostream.ProtobufService;
import org.kie.kogito.index.messaging.ReactiveMessagingEventConsumer;
import org.kie.kogito.index.model.ProcessInstanceState;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.kie.kogito.index.TestUtils.getTravelsCloudEvent;
import static org.kie.kogito.index.TestUtils.getTravelsProtoBufferFile;

@QuarkusTest
@QuarkusTestResource(InfinispanServerTestResource.class)
public class IndexingServiceTest {

    @Inject
    ReactiveMessagingEventConsumer consumer;

    @Inject
    ProtobufService protobufService;

    @BeforeAll
    public static void setup() {
        RestAssured.config = RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false));
    }

    @Test
    public void testAddBrokenProtoFile() {
        try {
            protobufService.registerProtoBufferType(getBrokenProtoBufferFile());
            fail("Registering broken proto file should fail");
        } catch (Exception ex) {
            assertThat(ex.getMessage()).isEqualTo("Failed to resolve type of field \"travels.traveller\". Type not found : stringa");
        }
    }

    @Test
    public void testAddProtoFileMissingModel() {
        try {
            protobufService.registerProtoBufferType(getProtoBufferFileWithoutModel());
            fail("Registering broken proto file should fail");
        } catch (Exception ex) {
            assertThat(ex.getMessage()).isEqualTo("Missing marker for main message type in proto file, please add option kogito_model=\"messagename\"");
        }
    }

    @Test
    public void testAddProtoFileMissingId() {
        try {
            protobufService.registerProtoBufferType(getProtoBufferFileWithoutId());
            fail("Registering broken proto file should fail");
        } catch (Exception ex) {
            assertThat(ex.getMessage()).isEqualTo("Missing marker for process id in proto file, please add option kogito_id=\"processid\"");
        }
    }

    @Test
    public void testAddProtoFileMissingModelType() {
        try {
            protobufService.registerProtoBufferType(getProtoBufferFileWithoutModelType());
            fail("Registering broken proto file should fail");
        } catch (Exception ex) {
            assertThat(ex.getMessage()).isEqualTo("Could not find message with name: traveller in proto file, e, please review option kogito_model");
        }
    }

    @Test //Reproducer for KOGITO-172
    public void testAddProtoFileTwice() throws Exception {
        protobufService.registerProtoBufferType(getProtoBufferFileV1());
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{Game{ player, id, name, processInstances { id } } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Game", isA(Collection.class));
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances{ id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.ProcessInstances", isA(Collection.class));

        protobufService.registerProtoBufferType(getProtoBufferFileV2());
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{Game{ id, name, company, processInstances { id } } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Game", isA(Collection.class));
        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances{ id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.ProcessInstances", isA(Collection.class));
    }

    @Test
    public void testAddProtoFile() throws Exception {
        String processId = "travels";
        String subProcessId = processId + "_sub";
        String processInstanceId = UUID.randomUUID().toString();
        String subProcessInstanceId = UUID.randomUUID().toString();

        protobufService.registerProtoBufferType(getTravelsProtoBufferFile());

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{Travels{ id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200).body("data.Travels", isA(Collection.class));

        KogitoCloudEvent startEvent = getTravelsCloudEvent(processId, processInstanceId, "ProcessInstanceEvent", ProcessInstanceState.ACTIVE, null, null);
        consumer.onProcessInstanceEvent(startEvent);

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances(filter: { id: \\\"" + processInstanceId + "\\\", state: ACTIVE, offset: 0, limit: 10 }){ id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.ProcessInstances[0].id", is(processInstanceId))
                .body("data.ProcessInstances[0].processId", is(processId))
                .body("data.ProcessInstances[0].rootProcessId", isEmptyOrNullString())
                .body("data.ProcessInstances[0].rootProcessInstanceId", isEmptyOrNullString())
                .body("data.ProcessInstances[0].parentProcessInstanceId", isEmptyOrNullString());

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Travels(query: \\\"from org.acme.travels.travels.Travels t where t.traveller.firstName:'ma*' and t.processInstances.id:'" + processInstanceId + "'\\\"){ flight { flightNumber }, hotel { name }, traveller { firstName }, processInstances { id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].processInstances.size()", is(1))
                .body("data.Travels[0].processInstances[0].id", is(processInstanceId))
                .body("data.Travels[0].processInstances[0].processId", is(processId))
                .body("data.Travels[0].processInstances[0].rootProcessId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].rootProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].processInstances[0].parentProcessInstanceId", isEmptyOrNullString())
                .body("data.Travels[0].traveller.firstName", is("Maciej"))
                .body("data.Travels[0].hotel.name", is("Meriton"))
                .body("data.Travels[0].flight.flightNumber", is("MX555"));

        KogitoCloudEvent subProcessStartEvent = getTravelsCloudEvent(subProcessId, subProcessInstanceId, "ProcessInstanceEvent", ProcessInstanceState.ACTIVE, processInstanceId, processId);
        consumer.onProcessInstanceEvent(subProcessStartEvent);

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances(filter: { id: \\\"" + subProcessInstanceId + "\\\", state: ACTIVE, offset: 0, limit: 10 }){ id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.ProcessInstances[0].id", is(subProcessInstanceId))
                .body("data.ProcessInstances[0].processId", is(subProcessId))
                .body("data.ProcessInstances[0].rootProcessId", is(processId))
                .body("data.ProcessInstances[0].rootProcessInstanceId", is(processInstanceId))
                .body("data.ProcessInstances[0].parentProcessInstanceId", is(processInstanceId));

        KogitoCloudEvent endEvent = getTravelsCloudEvent(processId, processInstanceId, "ProcessInstanceEvent", ProcessInstanceState.COMPLETED, null, null);
        consumer.onProcessInstanceEvent(endEvent);

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ProcessInstances(filter: { id: \\\"" + processInstanceId + "\\\", state: COMPLETED, offset: 0, limit: 10 }){ id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.ProcessInstances[0].id", is(processInstanceId))
                .body("data.ProcessInstances[0].processId", is(processId))
                .body("data.ProcessInstances[0].rootProcessId", isEmptyOrNullString())
                .body("data.ProcessInstances[0].rootProcessInstanceId", isEmptyOrNullString())
                .body("data.ProcessInstances[0].parentProcessInstanceId", isEmptyOrNullString());

        given().contentType(ContentType.JSON)
                .body("{ \"query\" : \"{Travels(query: \\\"from org.acme.travels.travels.Travels t where t.traveller.firstName:'ma*' and t.processInstances.id:'" + subProcessInstanceId + "'\\\"){ flight { flightNumber, arrival, departure }, hotel { name }, traveller { firstName }, processInstances { id, processId, rootProcessId, rootProcessInstanceId, parentProcessInstanceId } } }\"}")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels[0].processInstances.size()", is(2))
                .body("data.Travels[0].processInstances[1].id", is(subProcessInstanceId))
                .body("data.Travels[0].processInstances[1].processId", is(subProcessId))
                .body("data.Travels[0].processInstances[1].rootProcessId", is(processId))
                .body("data.Travels[0].processInstances[1].rootProcessInstanceId", is(processInstanceId))
                .body("data.Travels[0].processInstances[1].parentProcessInstanceId", is(processInstanceId))
                .body("data.Travels[0].traveller.firstName", is("Maciej"))
                .body("data.Travels[0].hotel.name", is("Meriton"))
                .body("data.Travels[0].flight.flightNumber", is("MX555"))
                .body("data.Travels[0].flight.arrival", is("2019-08-20T22:12:57.340Z"))
                .body("data.Travels[0].flight.departure", is("2019-08-20T07:12:57.340Z"));
    }

    private String getProtoBufferFileWithoutModelType() {
        return "   option kogito_id=\"travels\";\n" +
                "   option kogito_model=\"traveller\";\n" +
                "message travels {\n" +
                "   optional string traveller = 1;\n" +
                "   optional string hotel = 2;\n" +
                "   optional string flight = 3;\n" +
                "}\n" +
                "\n";
    }

    private String getProtoBufferFileWithoutId() {
        return "   option kogito_model=\"travels\";\n" +
                "message travels {\n" +
                "   optional string traveller = 1;\n" +
                "   optional string hotel = 2;\n" +
                "   optional string flight = 3;\n" +
                "}\n" +
                "\n";
    }

    private String getProtoBufferFileWithoutModel() {
        return "   option kogito_id=\"travels\";\n" +
                "message travels {\n" +
                "   optional string traveller = 1;\n" +
                "   optional string hotel = 2;\n" +
                "   optional string flight = 3;\n" +
                "}\n" +
                "\n";
    }

    private String getBrokenProtoBufferFile() {
        return "message travels {\n" +
                "   optional stringa traveller = 1;\n" +
                "   optional string hotel = 2;\n" +
                "   optional string flight = 3;\n" +
                "}\n" +
                "\n";
    }

    private String getProtoBufferFileV1() {
        return "import \"kogito-index.proto\";\n" +
                "option kogito_model=\"Game\";\n" +
                "option kogito_id=\"game\";\n" +
                "message Game {\n" +
                "   optional string player = 1;\n" +
                "   optional string id = 2;\n" +
                "   optional string name = 3;\n" +
                "   repeated org.kie.kogito.index.model.ProcessInstanceMeta processInstances = 4;\n" +
                "}\n" +
                "\n";
    }

    private String getProtoBufferFileV2() {
        return "import \"kogito-index.proto\";\n" +
                "option kogito_model=\"Game\";\n" +
                "option kogito_id=\"game\";\n" +
                "message Game {\n" +
                "   optional string id = 1;\n" +
                "   optional string name = 2;\n" +
                "   optional string company = 3;\n" +
                "   repeated org.kie.kogito.index.model.ProcessInstanceMeta processInstances = 4;\n" +
                "}\n" +
                "\n";
    }
}