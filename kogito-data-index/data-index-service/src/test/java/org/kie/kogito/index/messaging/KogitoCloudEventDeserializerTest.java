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

package org.kie.kogito.index.messaging;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.event.KogitoProcessCloudEvent;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;

import static java.util.Collections.emptySet;
import static org.kie.kogito.index.TestUtils.readFileContent;

public class KogitoCloudEventDeserializerTest {

    @Test
    public void testProcessDeserializer() throws Exception {
        KogitoProcessCloudEvent event = new KogitoProcessCloudEventDeserializer().deserialize(null, readFileContent("process_instance_event.json").getBytes());
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(event)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", "b50a3dd4-a955-49c4-a055-f30a4d768f87")
                .hasFieldOrPropertyWithValue("parentProcessInstanceId", "f8868a2e-1bbb-47eb-93cf-fa46ff9dbfee")
                .hasFieldOrPropertyWithValue("processId", "hotelBooking")
                .hasFieldOrPropertyWithValue("processInstanceId", "c2fa5c5e-3002-44c7-aef7-bce82297e3fe")
                .hasFieldOrPropertyWithValue("state", 2)
                .hasFieldOrPropertyWithValue("rootProcessId", "travels")
                .hasFieldOrPropertyWithValue("rootProcessInstanceId", "f8868a2e-1bbb-47eb-93cf-fa46ff9dbfee")
                .hasFieldOrPropertyWithValue("kogitoAddons", "jobs-management,prometheus-monitoring,process-management")
                .hasFieldOrPropertyWithValue("time", ZonedDateTime.parse("2019-08-20T19:26:02.110668Z[UTC]", DateTimeFormatter.ISO_DATE_TIME))
                .hasFieldOrPropertyWithValue("type", "ProcessInstanceEvent")
                .hasFieldOrPropertyWithValue("source", URI.create("/hotelBooking"))
                .hasFieldOrPropertyWithValue("data.id", "c2fa5c5e-3002-44c7-aef7-bce82297e3fe")
                .hasFieldOrPropertyWithValue("data.processId", "hotelBooking")
                .hasFieldOrPropertyWithValue("data.end", ZonedDateTime.parse("2019-08-20T19:26:02.092Z[UTC]", DateTimeFormatter.ISO_DATE_TIME))
                .hasFieldOrPropertyWithValue("data.parentProcessInstanceId", "f8868a2e-1bbb-47eb-93cf-fa46ff9dbfee")
                .hasFieldOrPropertyWithValue("data.processId", "hotelBooking")
                .hasFieldOrPropertyWithValue("data.rootProcessInstanceId", "f8868a2e-1bbb-47eb-93cf-fa46ff9dbfee")
                .hasFieldOrPropertyWithValue("data.rootProcessId", "travels")
                .hasFieldOrPropertyWithValue("data.start", ZonedDateTime.parse("2019-08-20T19:26:02.091Z[UTC]", DateTimeFormatter.ISO_DATE_TIME))
                .hasFieldOrPropertyWithValue("data.state", 2)
                .hasFieldOrPropertyWithValue("specVersion", "0.3")
                .hasFieldOrPropertyWithValue("contentType", null)
                .hasFieldOrPropertyWithValue("schemaURL", null)
                .hasFieldOrPropertyWithValue("kogitoReferenceId", null);
        softly.assertThat(event.getData().getNodes().get(0))
                .hasFieldOrPropertyWithValue("id", "54e66e2f-2acd-4d47-b8e6-991cb6372ad8")
                .hasFieldOrPropertyWithValue("exit", ZonedDateTime.parse("2019-08-20T19:26:02.092Z[UTC]", DateTimeFormatter.ISO_DATE_TIME))
                .hasFieldOrPropertyWithValue("definitionId", "EndEvent_1")
                .hasFieldOrPropertyWithValue("nodeId", "3")
                .hasFieldOrPropertyWithValue("name", "End Event 1")
                .hasFieldOrPropertyWithValue("type", "EndNode")
                .hasFieldOrPropertyWithValue("enter", ZonedDateTime.parse("2019-08-20T19:26:02.092Z[UTC]", DateTimeFormatter.ISO_DATE_TIME));
        softly.assertThat(event.getData().getMilestones().get(0))
                .hasFieldOrPropertyWithValue("id", "_18950AC4-664B-465E-B99E-766D38D362BF")
                .hasFieldOrPropertyWithValue("name", "AutoStartMilestone")
                .hasFieldOrPropertyWithValue("status", "AVAILABLE");
        
        softly.assertAll();
    }

    @Test
    public void testProcessEmptyIdsDeserializer() throws Exception {
        KogitoProcessCloudEvent event = new KogitoProcessCloudEventDeserializer().deserialize(null, readFileContent("process_instance_empty_event.json").getBytes());
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(event)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", "b50a3dd4-a955-49c4-a055-f30a4d768f87")
                .hasFieldOrPropertyWithValue("parentProcessInstanceId", null)
                .hasFieldOrPropertyWithValue("processId", "hotelBooking")
                .hasFieldOrPropertyWithValue("processInstanceId", null)
                .hasFieldOrPropertyWithValue("rootProcessId", "travels")
                .hasFieldOrPropertyWithValue("rootProcessInstanceId", null)
                .hasFieldOrPropertyWithValue("kogitoAddons", null)
                .hasFieldOrPropertyWithValue("type", "ProcessInstanceEvent")
                .hasFieldOrPropertyWithValue("data.id", null)
                .hasFieldOrPropertyWithValue("data.processId", "hotelBooking")
                .hasFieldOrPropertyWithValue("data.parentProcessInstanceId", null)
                .hasFieldOrPropertyWithValue("data.processId", "hotelBooking")
                .hasFieldOrPropertyWithValue("data.rootProcessInstanceId", null)
                .hasFieldOrPropertyWithValue("data.rootProcessId", "travels")
                .hasFieldOrPropertyWithValue("specVersion", "0.3")
                .hasFieldOrPropertyWithValue("contentType", null)
                .hasFieldOrPropertyWithValue("schemaURL", null)
                .hasFieldOrPropertyWithValue("kogitoReferenceId", null);

        softly.assertAll();
    }

    @Test
    public void testUserTaskDeserializer() throws Exception {
        KogitoUserTaskCloudEvent event = new KogitoUserTaskCloudEventDeserializer().deserialize(null, readFileContent("user_task_instance_event.json").getBytes());
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(event)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", "ef315bd2-93a9-456f-b539-d73c0c83c1df")
                .hasFieldOrPropertyWithValue("userTaskInstanceId", "228d5922-5e88-4bfa-8329-7116a5cbe58b")
                .hasFieldOrPropertyWithValue("state", "Completed")
                .hasFieldOrPropertyWithValue("processId", "travels")
                .hasFieldOrPropertyWithValue("processInstanceId", "f78fb147-ec22-4478-a592-3063add9f956")
                .hasFieldOrPropertyWithValue("rootProcessId", null)
                .hasFieldOrPropertyWithValue("rootProcessInstanceId", null)
                .hasFieldOrPropertyWithValue("kogitoAddons", "jobs-management,prometheus-monitoring,process-management")
                .hasFieldOrPropertyWithValue("time", ZonedDateTime.parse("2019-08-30T11:48:37.857915Z[UTC]", DateTimeFormatter.ISO_DATE_TIME))
                .hasFieldOrPropertyWithValue("type", "UserTaskInstanceEvent")
                .hasFieldOrPropertyWithValue("specVersion", "0.3")
                .hasFieldOrPropertyWithValue("schemaURL", null)
                .hasFieldOrPropertyWithValue("contentType", null)
                .hasFieldOrPropertyWithValue("kogitoReferenceId", null)
                .hasFieldOrPropertyWithValue("data.id", "228d5922-5e88-4bfa-8329-7116a5cbe58b")
                .hasFieldOrPropertyWithValue("data.processId", "travels")
                .hasFieldOrPropertyWithValue("data.processInstanceId", "f78fb147-ec22-4478-a592-3063add9f956")
                .hasFieldOrPropertyWithValue("data.rootProcessInstanceId", null)
                .hasFieldOrPropertyWithValue("data.rootProcessId", null)
                .hasFieldOrPropertyWithValue("data.state", "Completed")
                .hasFieldOrPropertyWithValue("data.description", "")
                .hasFieldOrPropertyWithValue("data.name", "Apply for visa")
                .hasFieldOrPropertyWithValue("data.referenceName", "http://localhost:8080/travels/{uuid}/ConfirmTravel/{task-uuid}")
                .hasFieldOrPropertyWithValue("data.priority", "1")
                .hasFieldOrPropertyWithValue("data.potentialGroups", emptySet())
                .hasFieldOrPropertyWithValue("data.potentialUsers", emptySet())
                .hasFieldOrPropertyWithValue("data.actualOwner", null)
                .hasFieldOrPropertyWithValue("data.adminGroups", emptySet())
                .hasFieldOrPropertyWithValue("data.adminUsers", emptySet())
                .hasFieldOrPropertyWithValue("data.excludedUsers", emptySet())
                .hasFieldOrPropertyWithValue("data.completed", ZonedDateTime.parse("2019-08-30T11:48:37.828Z[UTC]", DateTimeFormatter.ISO_DATE_TIME))
                .hasFieldOrPropertyWithValue("data.started", ZonedDateTime.parse("2019-08-30T11:47:42.886Z[UTC]", DateTimeFormatter.ISO_DATE_TIME));

        softly.assertAll();
    }

    @Test
    public void testJobDeserializer() throws Exception {
        KogitoJobCloudEvent event = new KogitoJobCloudEventDeserializer().deserialize(null, readFileContent("job_event.json").getBytes());
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(event)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", "2fea1981-321d-40e7-aa82-3730504c1672")
                .hasFieldOrPropertyWithValue("source", URI.create("http://localhost:8080/jobs"))
                .hasFieldOrPropertyWithValue("time", ZonedDateTime.parse("2020-01-16T20:40:58.227215Z[UTC]", DateTimeFormatter.ISO_DATE_TIME))
                .hasFieldOrPropertyWithValue("type", "JobEvent")
                .hasFieldOrPropertyWithValue("specVersion", "0.3")
                .hasFieldOrPropertyWithValue("schemaURL", null)
                .hasFieldOrPropertyWithValue("contentType", null)
                .hasFieldOrPropertyWithValue("processId", "timerscycle")
                .hasFieldOrPropertyWithValue("processInstanceId", "7c1d9b38-b462-47c5-8bf2-d9154f54957b")
                .hasFieldOrPropertyWithValue("rootProcessId", null)
                .hasFieldOrPropertyWithValue("rootProcessInstanceId", null)
                .hasFieldOrPropertyWithValue("data.id", "8350b8b6-c5d9-432d-a339-a9fc85f642d4_0")
                .hasFieldOrPropertyWithValue("data.processId", "timerscycle")
                .hasFieldOrPropertyWithValue("data.processInstanceId", "7c1d9b38-b462-47c5-8bf2-d9154f54957b")
                .hasFieldOrPropertyWithValue("data.rootProcessInstanceId", null)
                .hasFieldOrPropertyWithValue("data.rootProcessId", null)
                .hasFieldOrPropertyWithValue("data.repeatInterval", 1000L)
                .hasFieldOrPropertyWithValue("data.repeatLimit", 2147483647)
                .hasFieldOrPropertyWithValue("data.scheduledId", "0")
                .hasFieldOrPropertyWithValue("data.retries", 0)
                .hasFieldOrPropertyWithValue("data.status", "SCHEDULED")
                .hasFieldOrPropertyWithValue("data.executionCounter", 0)
                .hasFieldOrPropertyWithValue("data.priority", 0)
                .hasFieldOrPropertyWithValue("data.expirationTime", ZonedDateTime.parse("2020-01-16T20:40:58.918Z[UTC]", DateTimeFormatter.ISO_DATE_TIME));

        softly.assertAll();
    }
}
