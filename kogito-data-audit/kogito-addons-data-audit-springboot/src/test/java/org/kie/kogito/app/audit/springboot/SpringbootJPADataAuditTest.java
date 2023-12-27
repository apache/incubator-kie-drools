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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.kogito.app.audit.api.SubsystemConstants;
import org.kie.kogito.event.EventPublisher;
import org.kie.kogito.event.process.ProcessInstanceEventMetadata;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateEventBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server_port=0")
public class SpringbootJPADataAuditTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringbootJPADataAuditTest.class);

    @LocalServerPort
    int randomServerPort;

    @Autowired
    EventPublisher eventPublisher;

    @Test
    public void testSpringbootEventPublisher() {

        String processId = "processId";
        String processVersion = "1.0";
        String processType = "BPMN2";
        ProcessInstanceStateEventBody body = ProcessInstanceStateEventBody.create()
                .processId(processId)
                .processInstanceId("1")
                .parentInstanceId(null)
                .processType(processType)
                .processVersion(processVersion)
                .processName(UUID.randomUUID().toString())
                .eventDate(new Date())
                .state(1)
                .businessKey(UUID.randomUUID().toString())
                .roles("admin")
                .eventUser("myUser")
                .eventType(ProcessInstanceStateEventBody.EVENT_TYPE_STARTED)
                .build();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, "1");
        metadata.put(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA, processId);
        metadata.put(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA, processVersion);
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_STATE_META_DATA, String.valueOf(1));
        metadata.put(ProcessInstanceEventMetadata.PROCESS_TYPE_META_DATA, processType);

        ProcessInstanceStateDataEvent event =
                new ProcessInstanceStateDataEvent("http://localhost:8080/" + processId, "", "myUser", metadata, body);

        event.setKogitoBusinessKey(body.getBusinessKey());

        eventPublisher.publish(event);

        Response response = given()
                .port(randomServerPort)
                .contentType(ContentType.JSON)
                .body("{\"query\": \"{ GetAllProcessInstancesState { eventId, eventDate, processType, processId, processVersion, parentProcessInstanceId, rootProcessId, rootProcessInstanceId, processInstanceId, businessKey, eventType, outcome, state, slaDueDate } }\"}")
                .when()
                .post(SubsystemConstants.DATA_AUDIT_PATH)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .response();

        LOGGER.info("Data response is {}", response.asPrettyString());

    }

}
