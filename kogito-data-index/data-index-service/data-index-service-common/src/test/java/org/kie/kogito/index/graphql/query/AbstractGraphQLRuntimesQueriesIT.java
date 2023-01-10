/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.index.graphql.query;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceEventBody;
import org.kie.kogito.index.api.KogitoRuntimeClient;
import org.kie.kogito.index.event.KogitoJobCloudEvent;
import org.kie.kogito.index.graphql.GraphQLSchemaManagerImpl;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.service.AbstractIndexingIT;
import org.kie.kogito.persistence.protobuf.ProtobufService;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.TestUtils.getJob;
import static org.kie.kogito.index.TestUtils.getJobCloudEvent;
import static org.kie.kogito.index.TestUtils.getProcessCloudEvent;
import static org.kie.kogito.index.TestUtils.getProcessInstance;
import static org.kie.kogito.index.TestUtils.getTaskAttachment;
import static org.kie.kogito.index.TestUtils.getTaskComment;
import static org.kie.kogito.index.TestUtils.getUserTaskCloudEvent;
import static org.kie.kogito.index.model.ProcessInstanceState.ACTIVE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractGraphQLRuntimesQueriesIT extends AbstractIndexingIT {

    @Inject
    public GraphQLSchemaManagerImpl manager;

    @Inject
    public ProtobufService protobufService;

    private String processId = "travels";
    String user = "jdoe";
    List<String> groups = Arrays.asList("managers", "users", "IT");

    private KogitoRuntimeClient dataIndexApiClient;

    @BeforeEach
    public void setup() throws Exception {
        protobufService.registerProtoBufferType(getTestProtobufFileContent());
        dataIndexApiClient = mock(KogitoRuntimeClient.class);
        manager.setDataIndexApiExecutor(dataIndexApiClient);
    }

    @Test
    void testProcessInstanceAbort() {
        String processInstanceId = UUID.randomUUID().toString();
        ProcessInstanceDataEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ACTIVE, null, null, null);
        indexProcessCloudEvent(startEvent);

        checkOkResponse("{ \"query\" : \"mutation{ ProcessInstanceAbort ( id: \\\"" + processInstanceId + "\\\")}\"}");

        verify(dataIndexApiClient).abortProcessInstance(eq("http://localhost:8080"),
                eq(getProcessInstance(processId, processInstanceId, 1, null, null)));
    }

    @Test
    void testProcessInstanceRetry() {
        String processInstanceId = UUID.randomUUID().toString();
        ProcessInstanceDataEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ACTIVE, null, null, null);
        indexProcessCloudEvent(startEvent);

        checkOkResponse("{ \"query\" : \"mutation{ ProcessInstanceRetry ( id: \\\"" + processInstanceId + "\\\")}\"}");

        verify(dataIndexApiClient).retryProcessInstance(eq("http://localhost:8080"),
                eq(getProcessInstance(processId, processInstanceId, 1, null, null)));
    }

    @Test
    void testProcessInstanceSkip() {
        String processInstanceId = UUID.randomUUID().toString();
        ProcessInstanceDataEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ACTIVE, null, null, null);
        indexProcessCloudEvent(startEvent);

        checkOkResponse("{ \"query\" : \"mutation{ ProcessInstanceSkip ( id: \\\"" + processInstanceId + "\\\")}\"}");

        verify(dataIndexApiClient).skipProcessInstance(eq("http://localhost:8080"),
                eq(getProcessInstance(processId, processInstanceId, 1, null, null)));
    }

    @Test
    void testProcessInstanceUpdateVariables() {
        String variablesUpdated = "variablesUpdated";
        String processInstanceId = UUID.randomUUID().toString();

        ProcessInstanceDataEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ACTIVE, null, null, null);
        indexProcessCloudEvent(startEvent);

        checkOkResponse("{ \"query\" : \"mutation{ ProcessInstanceUpdateVariables ( id: \\\"" + processInstanceId + "\\\", variables: \\\"" + variablesUpdated + "\\\")}\"}");

        verify(dataIndexApiClient).updateProcessInstanceVariables(eq("http://localhost:8080"),
                eq(getProcessInstance(processId, processInstanceId, 1, null, null)), eq(variablesUpdated));
    }

    @Test
    void testProcessInstanceNodeDefinitions() {
        String processInstanceId = UUID.randomUUID().toString();
        ProcessInstanceDataEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ACTIVE, null, null, null);
        indexProcessCloudEvent(startEvent);

        checkOkResponse("{ \"query\" : \"query { ProcessInstances (where: { id: {equal: \\\"" + processInstanceId + "\\\"}}) { nodeDefinitions { id }} }\" }");
        verify(dataIndexApiClient).getProcessInstanceNodeDefinitions(eq("http://localhost:8080"),
                eq(getProcessInstance(processId, processInstanceId, 1, null, null)));
    }

    @Test
    void testProcessInstanceDiagram() {
        String processInstanceId = UUID.randomUUID().toString();
        ProcessInstanceDataEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ACTIVE, null, null, null);
        indexProcessCloudEvent(startEvent);

        checkOkResponse("{ \"query\" : \"query { ProcessInstances (where: { id: {equal: \\\"" + processInstanceId + "\\\"}}) {diagram} }\" }");

        verify(dataIndexApiClient).getProcessInstanceDiagram(eq("http://localhost:8080"),
                eq(getProcessInstance(processId, processInstanceId, 1, null, null)));
    }

    @Test
    void testProcessInstanceSource() {
        String processInstanceId = UUID.randomUUID().toString();
        ProcessInstanceDataEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ACTIVE, null, null, null);
        indexProcessCloudEvent(startEvent);

        checkOkResponse("{ \"query\" : \"query { ProcessInstances (where: { id: {equal: \\\"" + processInstanceId + "\\\"}}) {source} }\" }");

        verify(dataIndexApiClient).getProcessInstanceSourceFileContent(eq("http://localhost:8080"),
                eq(getProcessInstance(processId, processInstanceId, 1, null, null)));
    }

    @Test
    void testNodeInstanceTrigger() {
        String nodeId = "nodeIdToTrigger";
        String processInstanceId = UUID.randomUUID().toString();
        ProcessInstanceDataEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ACTIVE, null, null, null);
        indexProcessCloudEvent(startEvent);

        checkOkResponse("{ \"query\" : \"mutation{ NodeInstanceTrigger ( id: \\\"" + processInstanceId + "\\\", nodeId: \\\"" + nodeId + "\\\")}\"}");

        verify(dataIndexApiClient).triggerNodeInstance(eq("http://localhost:8080"),
                eq(getProcessInstance(processId, processInstanceId, 1, null, null)), eq(nodeId));
    }

    @Test
    void testNodeInstanceRetrigger() {
        String nodeInstanceId = "nodeInstanceIdToRetrigger";
        String processInstanceId = UUID.randomUUID().toString();
        ProcessInstanceDataEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ACTIVE, null, null, null);
        indexProcessCloudEvent(startEvent);

        checkOkResponse("{ \"query\" : \"mutation{ NodeInstanceRetrigger ( id: \\\"" + processInstanceId + "\\\", nodeInstanceId: \\\"" + nodeInstanceId + "\\\")}\"}");

        verify(dataIndexApiClient).retriggerNodeInstance(eq("http://localhost:8080"),
                eq(getProcessInstance(processId, processInstanceId, 1, null, null)), eq(nodeInstanceId));
    }

    @Test
    void testNodeInstanceCancel() {
        String nodeInstanceId = "nodeInstanceIdToCancel";
        String processInstanceId = UUID.randomUUID().toString();
        ProcessInstanceDataEvent startEvent = getProcessCloudEvent(processId, processInstanceId, ACTIVE, null, null, null);
        indexProcessCloudEvent(startEvent);

        checkOkResponse("{ \"query\" : \"mutation{ NodeInstanceCancel ( id: \\\"" + processInstanceId + "\\\", nodeInstanceId: \\\"" + nodeInstanceId + "\\\")}\"}");

        verify(dataIndexApiClient).cancelNodeInstance(eq("http://localhost:8080"),
                eq(getProcessInstance(processId, processInstanceId, 1, null, null)), eq(nodeInstanceId));
    }

    @Test
    void testJobCancel() {
        String jobId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();

        KogitoJobCloudEvent event = getJobCloudEvent(jobId, processId, processInstanceId, null, null, "EXECUTED");

        indexJobCloudEvent(event);
        checkOkResponse("{ \"query\" : \"mutation{ JobCancel ( id: \\\"" + jobId + "\\\")}\"}");

        verify(dataIndexApiClient).cancelJob(eq("http://localhost:8080/jobs"),
                eq(getJob(jobId, processId, processInstanceId, null, null, "SCHEDULED")));
    }

    @Test
    void testJobReschedule() {
        String jobId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();
        String data = "jobNewData";

        KogitoJobCloudEvent event = getJobCloudEvent(jobId, processId, processInstanceId, null, null, "EXECUTED");

        indexJobCloudEvent(event);
        checkOkResponse("{ \"query\" : \"mutation{ JobReschedule ( id: \\\"" + jobId + "\\\", data: \\\"" + data + "\\\")}\"}");

        verify(dataIndexApiClient).rescheduleJob(eq("http://localhost:8080/jobs"),
                eq(getJob(jobId, processId, processInstanceId, null, null, "SCHEDULED")),
                eq(data));
    }

    @Test
    void testGetTaskSchema() {
        String processInstanceId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();

        UserTaskInstanceDataEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null,
                null, "InProgress", user);

        indexUserTaskCloudEvent(event);
        checkOkResponse("{ \"query\" : \"{UserTaskInstances (where: {id: {equal:\\\"" + taskId + "\\\" }}){ " +
                "schema ( user: \\\"" + user + "\\\", groups: [\\\"managers\\\", \\\"users\\\", \\\"IT\\\"] )" +
                "}}\"}");
        ArgumentCaptor<UserTaskInstance> userTaskInstanceCaptor = ArgumentCaptor.forClass(UserTaskInstance.class);

        verify(dataIndexApiClient).getUserTaskSchema(eq("http://localhost:8080"),
                userTaskInstanceCaptor.capture(),
                eq(user), eq(groups));
        assertUserTaskInstance(userTaskInstanceCaptor.getValue(), taskId, processId, processInstanceId, user);
    }

    @Test
    void testUpdateUserTaskInstance() {
        String processInstanceId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        String newDescription = "NewDescription";

        UserTaskInstanceDataEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null,
                null, "InProgress", user);

        indexUserTaskCloudEvent(event);
        checkOkResponse("{ \"query\" : \"mutation { UserTaskInstanceUpdate ( " +
                "taskId: \\\"" + taskId + "\\\"," +
                "user: \\\"" + user + "\\\", " +
                "groups: [\\\"managers\\\", \\\"users\\\", \\\"IT\\\"]," +
                "description:  \\\"" + newDescription + "\\\"" +
                ")}\"}");
        ArgumentCaptor<UserTaskInstance> userTaskInstanceCaptor = ArgumentCaptor.forClass(UserTaskInstance.class);
        ArgumentCaptor<Map> taskInfoCaptor = ArgumentCaptor.forClass(Map.class);

        verify(dataIndexApiClient).updateUserTaskInstance(eq("http://localhost:8080"),
                userTaskInstanceCaptor.capture(),
                eq(user), eq(groups), taskInfoCaptor.capture());
        assertThat(taskInfoCaptor.getValue().get("description")).isEqualTo(newDescription);
        assertUserTaskInstance(userTaskInstanceCaptor.getValue(), taskId, processId, processInstanceId, user);
    }

    @Test
    void testCreateTaskComment() {
        String processInstanceId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        String comment = "Comment to add";

        UserTaskInstanceDataEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null,
                null, "InProgress", user);

        indexUserTaskCloudEvent(event);
        checkOkResponse("{ \"query\" : \"mutation{ UserTaskInstanceCommentCreate(" +
                "taskId: \\\"" + taskId + "\\\", " +
                "user: \\\"" + user + "\\\", " +
                "groups: [\\\"managers\\\", \\\"users\\\", \\\"IT\\\"]," +
                "comment: \\\"" + comment + "\\\" " +
                ")}\"}");
        ArgumentCaptor<UserTaskInstance> userTaskInstanceCaptor = ArgumentCaptor.forClass(UserTaskInstance.class);

        verify(dataIndexApiClient).createUserTaskInstanceComment(eq("http://localhost:8080"),
                userTaskInstanceCaptor.capture(),
                eq(user), eq(groups),
                eq(comment));
        assertUserTaskInstance(userTaskInstanceCaptor.getValue(), taskId, processId, processInstanceId, user);
    }

    @Test
    void testUpdateUserTaskInstanceComment() {
        String processInstanceId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        String commentId = UUID.randomUUID().toString();
        String commentContent = "commentContent";

        UserTaskInstanceDataEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null,
                null, "InProgress", user);
        UserTaskInstanceEventBody userTaskInstance = event.getData();
        userTaskInstance.setComments(List.of(getTaskComment(commentId, null, null)));
        indexUserTaskCloudEvent(event);
        checkOkResponse("{ \"query\" : \"mutation { UserTaskInstanceCommentUpdate ( " +
                "user: \\\"" + user + "\\\", " +
                "groups: [\\\"managers\\\", \\\"users\\\", \\\"IT\\\"]," +
                "commentId:  \\\"" + commentId + "\\\"" +
                "comment:  \\\"" + commentContent + "\\\"" +
                ")}\"}");
        ArgumentCaptor<UserTaskInstance> userTaskInstanceCaptor = ArgumentCaptor.forClass(UserTaskInstance.class);

        verify(dataIndexApiClient).updateUserTaskInstanceComment(eq("http://localhost:8080"),
                userTaskInstanceCaptor.capture(),
                eq(user), eq(groups), eq(commentId), eq(commentContent));
        assertUserTaskInstance(userTaskInstanceCaptor.getValue(), taskId, processId, processInstanceId, user);
    }

    @Test
    void testDeleteUserTaskInstanceComment() {
        String processInstanceId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        String commentId = UUID.randomUUID().toString();

        UserTaskInstanceDataEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null,
                null, "InProgress", user);
        UserTaskInstanceEventBody userTaskInstance = event.getData();
        userTaskInstance.setComments(List.of(getTaskComment(commentId, null, null)));
        indexUserTaskCloudEvent(event);
        checkOkResponse("{ \"query\" : \"mutation { UserTaskInstanceCommentDelete ( " +
                "user: \\\"" + user + "\\\", " +
                "groups: [\\\"managers\\\", \\\"users\\\", \\\"IT\\\"]," +
                "commentId:  \\\"" + commentId + "\\\"" +
                ")}\"}");
        ArgumentCaptor<UserTaskInstance> userTaskInstanceCaptor = ArgumentCaptor.forClass(UserTaskInstance.class);

        verify(dataIndexApiClient).deleteUserTaskInstanceComment(eq("http://localhost:8080"),
                userTaskInstanceCaptor.capture(),
                eq(user), eq(groups), eq(commentId));
        assertUserTaskInstance(userTaskInstanceCaptor.getValue(), taskId, processId, processInstanceId, user);
    }

    @Test
    void testCreateTaskAttachment() {
        String processInstanceId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        String attachmentName = "attachment name";
        String attachmentUri = "https://drive.google.com/file/d/1Z_Lipg2jzY9TNewTaskAttachmentUri";

        UserTaskInstanceDataEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null,
                null, "InProgress", user);

        indexUserTaskCloudEvent(event);
        checkOkResponse("{ \"query\" : \"mutation{ UserTaskInstanceAttachmentCreate(" +
                "taskId: \\\"" + taskId + "\\\", " +
                "user: \\\"" + user + "\\\", " +
                "groups: [\\\"managers\\\", \\\"users\\\", \\\"IT\\\"]," +
                "name: \\\"" + attachmentName + "\\\", " +
                "uri: \\\"" + attachmentUri + "\\\" " +
                ")}\"}");
        ArgumentCaptor<UserTaskInstance> userTaskInstanceCaptor = ArgumentCaptor.forClass(UserTaskInstance.class);

        verify(dataIndexApiClient).createUserTaskInstanceAttachment(eq("http://localhost:8080"),
                userTaskInstanceCaptor.capture(),
                eq(user), eq(groups),
                eq(attachmentName),
                eq(attachmentUri));
        assertUserTaskInstance(userTaskInstanceCaptor.getValue(), taskId, processId, processInstanceId, user);
    }

    @Test
    void testUpdateUserTaskInstanceAttachment() {
        String processInstanceId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        String attachmentId = UUID.randomUUID().toString();
        String attachmentName = "attachmentName";
        String attachmentUri = "attachmentUri";

        UserTaskInstanceDataEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null,
                null, "InProgress", user);
        UserTaskInstanceEventBody userTaskInstance = event.getData();
        userTaskInstance.setAttachments(List.of(getTaskAttachment(attachmentId, null, null, null)));
        indexUserTaskCloudEvent(event);
        checkOkResponse("{ \"query\" : \"mutation { UserTaskInstanceAttachmentUpdate ( " +
                "user: \\\"" + user + "\\\", " +
                "groups: [\\\"managers\\\", \\\"users\\\", \\\"IT\\\"]," +
                "attachmentId:  \\\"" + attachmentId + "\\\"" +
                "name:  \\\"" + attachmentName + "\\\"" +
                "uri:  \\\"" + attachmentUri + "\\\"" +
                ")}\"}");
        ArgumentCaptor<UserTaskInstance> userTaskInstanceCaptor = ArgumentCaptor.forClass(UserTaskInstance.class);

        verify(dataIndexApiClient).updateUserTaskInstanceAttachment(eq("http://localhost:8080"),
                userTaskInstanceCaptor.capture(),
                eq(user), eq(groups), eq(attachmentId), eq(attachmentName), eq(attachmentUri));
        assertUserTaskInstance(userTaskInstanceCaptor.getValue(), taskId, processId, processInstanceId, user);
    }

    @Test
    void testDeleteUserTaskInstanceAttachment() {
        String processInstanceId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        String attachmentId = UUID.randomUUID().toString();

        UserTaskInstanceDataEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, null,
                null, "InProgress", user);
        UserTaskInstanceEventBody userTaskInstance = event.getData();
        userTaskInstance.setAttachments(List.of(getTaskAttachment(attachmentId, null, null, null)));
        indexUserTaskCloudEvent(event);
        checkOkResponse("{ \"query\" : \"mutation { UserTaskInstanceAttachmentDelete ( " +
                "user: \\\"" + user + "\\\", " +
                "groups: [\\\"managers\\\", \\\"users\\\", \\\"IT\\\"]," +
                "attachmentId:  \\\"" + attachmentId + "\\\"" +
                ")}\"}");
        ArgumentCaptor<UserTaskInstance> userTaskInstanceCaptor = ArgumentCaptor.forClass(UserTaskInstance.class);

        verify(dataIndexApiClient).deleteUserTaskInstanceAttachment(eq("http://localhost:8080"),
                userTaskInstanceCaptor.capture(),
                eq(user), eq(groups), eq(attachmentId));
        assertUserTaskInstance(userTaskInstanceCaptor.getValue(), taskId, processId, processInstanceId, user);
    }

    private void assertUserTaskInstance(UserTaskInstance userTaskInstance, String taskId, String processId,
            String processInstanceId, String actualOwner) {
        assertThat(userTaskInstance.getId()).isEqualTo(taskId);
        assertThat(userTaskInstance.getProcessId()).isEqualTo(processId);
        assertThat(userTaskInstance.getProcessInstanceId()).isEqualTo(processInstanceId);
        assertThat(userTaskInstance.getActualOwner()).isEqualTo(actualOwner);
    }

    private void checkOkResponse(String body) {
        given().contentType(ContentType.JSON)
                .body(body)
                .when().post("/graphql")
                .then().statusCode(200);
    }

    protected abstract String getTestProtobufFileContent() throws Exception;
}
