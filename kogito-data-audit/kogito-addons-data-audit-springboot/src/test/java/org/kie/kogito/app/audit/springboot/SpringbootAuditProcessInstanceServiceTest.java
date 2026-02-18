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
package org.kie.kogito.app.audit.springboot;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.kie.kogito.app.audit.api.SubsystemConstants;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.process.*;
import org.kie.kogito.process.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.kie.kogito.app.audit.quarkus.DataAuditTestUtils.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server_port=0")
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SpringbootAuditProcessInstanceServiceTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    EventPublisher publisher;

    @BeforeAll
    public void init() {

        ProcessInstanceStateDataEvent processInstanceEvent;

        // the first process started
        processInstanceEvent = newProcessInstanceStateEvent("processId1", "1", ProcessInstance.STATE_ACTIVE, "rootI1", "rootP1", "parent1", "identity",
                ProcessInstanceStateEventBody.EVENT_TYPE_STARTED);
        publisher.publish(processInstanceEvent);

        ProcessInstanceVariableDataEvent processInstanceVariableEvent = newProcessInstanceVariableEvent(processInstanceEvent, "var_id1", "varName", "errorMessage", "identity");
        publisher.publish(processInstanceVariableEvent);

        processInstanceVariableEvent = newProcessInstanceVariableEvent(processInstanceEvent, "var_id1", "varName", "variableValue", "identity");
        publisher.publish(processInstanceVariableEvent);

        ProcessInstanceNodeDataEvent processInstanceNodeEvent;
        processInstanceNodeEvent = newProcessInstanceNodeEvent(processInstanceEvent, "StartNode", "nd1", "ni1", "name1", null, "myuser", ProcessInstanceNodeEventBody.EVENT_TYPE_ENTER);
        publisher.publish(processInstanceNodeEvent);

        processInstanceNodeEvent = newProcessInstanceNodeEvent(processInstanceEvent, "StartNode", "nd1", "ni1", "name1", null, "myuser", ProcessInstanceNodeEventBody.EVENT_TYPE_EXIT);
        publisher.publish(processInstanceNodeEvent);

        processInstanceNodeEvent = newProcessInstanceNodeEvent(processInstanceEvent, "EndNode", "nd2", "ni2", "name2", null, "myuser", ProcessInstanceNodeEventBody.EVENT_TYPE_ENTER);
        publisher.publish(processInstanceNodeEvent);

        // the second completed
        processInstanceEvent = newProcessInstanceStateEvent("processId1", "2", ProcessInstance.STATE_ACTIVE, "rootI1", "rootP1", "parent1", "identity",
                ProcessInstanceStateEventBody.EVENT_TYPE_STARTED);
        publisher.publish(processInstanceEvent);

        processInstanceEvent = deriveProcessInstanceStateEvent(processInstanceEvent, "identity2", ProcessInstance.STATE_COMPLETED, ProcessInstanceStateEventBody.EVENT_TYPE_ENDED);
        publisher.publish(processInstanceEvent);

        // the third in error
        processInstanceEvent = newProcessInstanceStateEvent("processId2", "3", ProcessInstance.STATE_ACTIVE, "rootI1", "rootP1", "parent1", "identity",
                ProcessInstanceStateEventBody.EVENT_TYPE_STARTED);
        publisher.publish(processInstanceEvent);

        ProcessInstanceErrorDataEvent processInstanceErrorEvent = newProcessInstanceErrorEvent(processInstanceEvent, "nd1", "ni1", "errorMessage1", "identity");
        publisher.publish(processInstanceErrorEvent);

        processInstanceErrorEvent = newProcessInstanceErrorEvent(processInstanceEvent, "nd2", "ni2", "errorMessage2", "identity");
        publisher.publish(processInstanceErrorEvent);

        processInstanceEvent = deriveProcessInstanceStateEvent(processInstanceEvent, "identity3", ProcessInstance.STATE_ERROR, ProcessInstanceStateEventBody.EVENT_TYPE_ENDED);
        publisher.publish(processInstanceEvent);

    }

    @Test
    public void testGetAllProcessInstancesState() {

        String query =
                "{ GetAllProcessInstancesState { eventId, eventDate, eventUser, processType, processId, processVersion, parentProcessInstanceId, rootProcessId, rootProcessInstanceId, processInstanceId, businessKey, eventType, outcome, state, slaDueDate, roles} }";

        query = wrapQuery(query);

        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllProcessInstancesState");

        assertThat(data)
                .hasSize(3)
                .extracting(e -> e.get("processInstanceId"), e -> e.get("state"), e -> e.get("eventUser"))
                .containsExactlyInAnyOrder(
                        tuple("1", String.valueOf(ProcessInstance.STATE_ACTIVE), "identity"),
                        tuple("2", String.valueOf(ProcessInstance.STATE_COMPLETED), "identity2"),
                        tuple("3", String.valueOf(ProcessInstance.STATE_ERROR), "identity3"));

    }

    @Test
    public void testGetProcessInstancesStateHistory() {
        String query =
                "{ GetProcessInstancesStateHistory ( processInstanceId : \\\"2\\\") { eventId, eventDate, eventUser, processType, processId, processVersion, parentProcessInstanceId, rootProcessId, rootProcessInstanceId, processInstanceId, businessKey, eventType, outcome, state, slaDueDate, roles} }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetProcessInstancesStateHistory");

        assertThat(data)
                .hasSize(2)
                .extracting(e -> e.get("processInstanceId"), e -> e.get("state"), e -> e.get("eventUser"))
                .containsExactlyInAnyOrder(
                        tuple("2", String.valueOf(ProcessInstance.STATE_ACTIVE), "identity"),
                        tuple("2", String.valueOf(ProcessInstance.STATE_COMPLETED), "identity2"));

    }

    @Test
    public void testGetProcessInstancesStateHistoryByBusinessKey() {
        String query =
                "{ GetProcessInstancesStateHistoryByBusinessKey ( businessKey : \\\"BusinessKey2\\\") { eventId, eventDate, eventUser, processType, processId, processVersion, parentProcessInstanceId, rootProcessId, rootProcessInstanceId, processInstanceId, businessKey, eventType, outcome, state, slaDueDate, roles} }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetProcessInstancesStateHistoryByBusinessKey");

        assertThat(data)
                .hasSize(2)
                .extracting(e -> e.get("processInstanceId"), e -> e.get("state"), e -> e.get("eventUser"))
                .containsExactlyInAnyOrder(
                        tuple("2", String.valueOf(ProcessInstance.STATE_ACTIVE), "identity"),
                        tuple("2", String.valueOf(ProcessInstance.STATE_COMPLETED), "identity2"));

    }

    @Test
    public void testGetAllProcessInstancesStateByStatus() {

        String query =
                "{ GetAllProcessInstancesStateByStatus (status : \\\"1\\\") { eventId, eventDate, eventUser, processType, processId, processVersion, parentProcessInstanceId, rootProcessId, rootProcessInstanceId, processInstanceId, businessKey, eventType, outcome, state, slaDueDate, roles} }";

        query = wrapQuery(query);

        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllProcessInstancesStateByStatus");

        assertThat(data)
                .hasSize(1)
                .extracting(e -> e.get("processInstanceId"), e -> e.get("state"), e -> e.get("eventUser"))
                .containsExactlyInAnyOrder(
                        tuple("1", String.valueOf(ProcessInstance.STATE_ACTIVE), "identity"));

    }

    @Test
    public void testGetAllProcessInstancesStateByProcessId() {

        String query =
                "{ GetAllProcessInstancesStateByProcessId (processId : \\\"processId1\\\") { eventId, eventDate, eventUser, processType, processId, processVersion, parentProcessInstanceId, rootProcessId, rootProcessInstanceId, processInstanceId, businessKey, eventType, outcome, state, slaDueDate, roles} }";

        query = wrapQuery(query);

        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllProcessInstancesStateByProcessId");

        assertThat(data)
                .hasSize(2)
                .extracting(e -> e.get("processInstanceId"), e -> e.get("state"), e -> e.get("eventUser"))
                .containsExactlyInAnyOrder(
                        tuple("1", String.valueOf(ProcessInstance.STATE_ACTIVE), "identity"),
                        tuple("2", String.valueOf(ProcessInstance.STATE_COMPLETED), "identity2"));

    }

    @Test
    public void testGetAllProcessInstancesNodeByProcessInstanceId() {
        String query =
                "{ GetAllProcessInstancesNodeByProcessInstanceId ( processInstanceId : \\\"1\\\") { eventId, eventDate, processType, processId, processVersion, parentProcessInstanceId, rootProcessId, rootProcessInstanceId, processInstanceId, businessKey, eventType, nodeType , nodeName, nodeInstanceId, connection, slaDueDate , eventData  } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllProcessInstancesNodeByProcessInstanceId");

        assertThat(data)
                .hasSize(2)
                .extracting(e -> e.get("processInstanceId"), e -> e.get("nodeInstanceId"), e -> e.get("eventType"))
                .containsExactlyInAnyOrder(
                        tuple("1", "ni1", "EXIT"),
                        tuple("1", "ni2", "ENTER"));

    }

    @Test
    public void testGetAllProcessInstancesErrorByProcessInstanceId() {

        String query =
                "{ GetAllProcessInstancesErrorByProcessInstanceId ( processInstanceId : \\\"3\\\")  { eventId, eventDate, processType, processId, processVersion, parentProcessInstanceId, rootProcessId, rootProcessInstanceId, processInstanceId, businessKey, errorMessage, nodeDefinitionId, nodeInstanceId } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllProcessInstancesErrorByProcessInstanceId");

        assertThat(data)
                .hasSize(2)
                .extracting(e -> e.get("errorMessage"), e -> e.get("nodeDefinitionId"), e -> e.get("nodeInstanceId"))
                .containsExactlyInAnyOrder(
                        tuple("errorMessage1", "nd1", "ni1"),
                        tuple("errorMessage2", "nd2", "ni2"));

    }

    @Test
    public void testGetAllProcessInstancesVariableByProcessInstanceId() {

        String query =
                "{ GetAllProcessInstancesVariableByProcessInstanceId ( processInstanceId : \\\"1\\\")  { eventId, eventDate, processType, processId, processVersion, parentProcessInstanceId, rootProcessId, rootProcessInstanceId, processInstanceId, businessKey, variableId, variableName, variableValue } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllProcessInstancesVariableByProcessInstanceId");

        assertThat(data)
                .hasSize(1)
                .extracting(e -> e.get("variableId"), e -> e.get("variableName"), e -> e.get("variableValue"))
                .containsExactlyInAnyOrder(
                        tuple("var_id1", "varName", "variableValue"));

    }

    @Test
    public void testGetAllProcessInstancesVariableHistoryByProcessInstanceId() {

        String query =
                "{ GetAllProcessInstancesVariableHistoryByProcessInstanceId ( processInstanceId : \\\"1\\\")  { variableId, variableName, logs { eventId, eventDate, eventUser, processType, processId, processVersion, parentProcessInstanceId, rootProcessId, rootProcessInstanceId, processInstanceId, businessKey, variableId, variableName, variableValue} } }";
        query = wrapQuery(query);
        List<Map<String, Object>> data = given()
                .contentType(ContentType.JSON)
                .body(query)
                .port(port)
                .when()
                .post(SubsystemConstants.DATA_AUDIT_QUERY_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract().path("data.GetAllProcessInstancesVariableHistoryByProcessInstanceId");

        assertThat(data)
                .hasSize(1)
                .extracting(e -> e.get("variableId"), e -> e.get("variableName"))
                .containsExactlyInAnyOrder(
                        tuple("var_id1", "varName"));

        List<Map<String, Object>> variableLogs = (List<Map<String, Object>>) data.get(0).get("logs");
        assertThat(variableLogs)
                .hasSize(2)
                .extracting(e -> e.get("processInstanceId"), e -> e.get("variableId"), e -> e.get("variableName"), e -> e.get("variableValue"), e -> e.get("eventUser"))
                .containsExactlyInAnyOrder(tuple("1", "var_id1", "varName", "variableValue", "identity"),
                        tuple("1", "var_id1", "varName", "errorMessage", "identity"));

    }
}
