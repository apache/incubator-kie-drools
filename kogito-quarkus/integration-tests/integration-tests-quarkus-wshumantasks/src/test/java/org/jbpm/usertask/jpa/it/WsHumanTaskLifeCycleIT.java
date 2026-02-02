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

package org.jbpm.usertask.jpa.it;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.kie.kogito.testcontainers.quarkus.PostgreSqlQuarkusTestResource;
import org.kie.kogito.usertask.model.TransitionInfo;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

@QuarkusIntegrationTest
@TestTransaction
@QuarkusTestResource(value = PostgreSqlQuarkusTestResource.class, restrictToAnnotatedClass = true)
public class WsHumanTaskLifeCycleIT {
    public static final String USER_TASKS_ENDPOINT = "/usertasks/instance";
    public static final String USER_TASKS_INSTANCE_ENDPOINT = USER_TASKS_ENDPOINT + "/{taskId}";
    public static final String USER_TASKS_INSTANCE_TRANSITION_ENDPOINT = USER_TASKS_INSTANCE_ENDPOINT + "/transition";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testUserTaskLifeCycle() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);
        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);
        isProcessCompleted(processId, pid);
    }

    @Test
    public void testForwardTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var forwardedUsers = new String[] { "mark", "eric" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);
        potentialUsers = forward(taskId, user, potentialUsers, forwardedUsers).toArray(String[]::new);

        user = "mark";
        claim(taskId, user);
        forwardedUsers = new String[] { "adam" };
        potentialUsers = forward(taskId, user, potentialUsers, forwardedUsers).toArray(String[]::new);

        user = "adam";
        claim(taskId, user);
        forwardedUsers = new String[] { "bob" };
        potentialUsers = forward(taskId, user, potentialUsers, forwardedUsers).toArray(String[]::new);

        user = "bob";
        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testDelegateTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        var delegatedUser = "adam";
        potentialUsers = delegate(taskId, user, potentialUsers, delegatedUser).toArray(String[]::new);

        user = "adam";
        delegatedUser = "john";
        potentialUsers = delegate(taskId, user, potentialUsers, delegatedUser).toArray(String[]::new);

        user = "john";
        start(taskId, user);
        delegatedUser = "mark";
        potentialUsers = delegate(taskId, user, potentialUsers, delegatedUser).toArray(String[]::new);

        user = "mark";
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testReleaseTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        claim(taskId, user);
        release(taskId, user);

        user = "john";
        claim(taskId, user);
        start(taskId, user);
        release(taskId, user);

        user = "dave";
        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testStopTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        claim(taskId, user);
        start(taskId, user);
        stop(taskId, user);

        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testFailTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        claim(taskId, user);
        start(taskId, user);
        fail(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendAndResumeTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspend(taskId, user);
        resume(taskId, user, "Ready");

        claim(taskId, user);
        suspend(taskId, user);
        resume(taskId, user, "Reserved");

        start(taskId, user);
        suspend(taskId, user);
        resume(taskId, user, "InProgress");

        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilWithDuration() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspendWithDurationOrTimestamp(taskId, user, "PT1S");
        verifyTaskStatus(taskId, user, "Ready");

        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilWithTimestamp() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        claim(taskId, user);

        suspendWithDurationOrTimestamp(taskId, user, ZonedDateTime.now().plusSeconds(2).toString());
        verifyTaskStatus(taskId, user, "Reserved");

        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilWithInvalidDurationOrTimestamp() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspendWithInvalidDurationOrTimestamp(taskId, user, "INVALID");
        verifyTaskStatus(taskId, user, "Ready");

        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilWithNegativeDuration() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspendWithInvalidDurationOrTimestamp(taskId, user, "PT-1H");
        verifyTaskStatus(taskId, user, "Ready");

        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilMultipleStatesWithDuration() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspendWithDurationOrTimestamp(taskId, user, "PT1S");
        verifyTaskStatus(taskId, user, "Ready");

        claim(taskId, user);
        suspendWithDurationOrTimestamp(taskId, user, "PT1S");
        verifyTaskStatus(taskId, user, "Reserved");

        start(taskId, user);
        suspendWithDurationOrTimestamp(taskId, user, "PT1S");
        verifyTaskStatus(taskId, user, "InProgress");

        complete(taskId, user);
        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilInProcessDefinition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "suspend_until";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspend(taskId, user);
        verifyTaskStatus(taskId, user, "Ready");

        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilInProcessDefinitionWithVariableNotation() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "suspend_until_variable";
        var pid = startProcessInstanceWithVariables(processId, Map.of("resumeAt", "PT1S"));
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspend(taskId, user);
        verifyTaskStatus(taskId, user, "Ready");

        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilWithZeroDuration() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspendWithInvalidDurationOrTimestamp(taskId, user, "PT0S");
        verifyTaskStatus(taskId, user, "Ready");

        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilWithEmptyValue() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspendWithInvalidDurationOrTimestamp(taskId, user, "  ");
        verifyTaskStatus(taskId, user, "Ready");

        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilWithPastTimestamp() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspendWithInvalidDurationOrTimestamp(taskId, user, ZonedDateTime.now().minusHours(1).toString());
        verifyTaskStatus(taskId, user, "Ready");

        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilWithSimpleDurationFormat() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspendWithDurationOrTimestamp(taskId, user, "1s");
        verifyTaskStatus(taskId, user, "Ready");

        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilWithInvalidRepeatTimerFormat() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspendWithInvalidDurationOrTimestamp(taskId, user, "R3/PT5S");
        verifyTaskStatus(taskId, user, "Ready");

        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilManualResumeBeforeAutoResume() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspendWithDurationOrTimestamp(taskId, user, "PT5S");
        resume(taskId, user, "Ready");

        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSuspendUntilOverridingProcessDefinitionValue() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "suspend_until";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        suspendWithDurationOrTimestamp(taskId, user, "PT4S");
        verifyTaskStatus(taskId, user, "Suspended", 3);
        verifyTaskStatus(taskId, user, "Ready", 6);

        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSkipTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        claim(taskId, user);
        start(taskId, user);
        skip(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testFaultTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        claim(taskId, user);
        start(taskId, user);
        fault(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testExitTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        claim(taskId, user);
        start(taskId, user);
        exit(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testNominateTransition() {
        var user = "carl";
        var processId = "manager_admin";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId("carl", pid);
        verifyTask(processId, pid, taskId, user, "Created", new String[] {});

        var nominatedUsers = new String[] { "john", "dave" };
        nominate(taskId, user, "Ready", nominatedUsers);

        user = "john";
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    @Test
    public void testSingleUserUserTaskLifeCycle() {
        var user = "jdoe";
        var processId = "manager_single_user";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user, pid);
        verifyTask(processId, pid, taskId, user, "Reserved", new String[] { user });

        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId, pid);
    }

    private void nominate(String taskId, String user, String status, String[] nominatedUsers) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("nominate", Map.of("NOMINATED_USERS", nominatedUsers)))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo(status))
                .body("status.terminate", equalTo(null))
                .body("potentialUsers", hasItems(nominatedUsers));
    }

    private void exit(String taskId, String user) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("exit"))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("Exited"))
                .body("status.terminate", equalTo("EXITED"));
    }

    private void fault(String taskId, String user) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("fault"))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("Error"))
                .body("status.terminate", equalTo("ERROR"));
    }

    private void skip(String taskId, String user) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("skip"))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("Obsolete"))
                .body("status.terminate", equalTo("OBSOLETE"));
    }

    private void resume(String taskId, String user, String previousState) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("resume"))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo(previousState))
                .body("status.terminate", equalTo(null))
                .body("metadata.SuspendedJobId", equalTo(null));
    }

    private void suspend(String taskId, String user) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("suspend"))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("Suspended"))
                .body("status.terminate", equalTo(null));
    }

    private void fail(String taskId, String user) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("fail", Map.of("message", "failed")))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("Failed"))
                .body("status.terminate", equalTo("FAILED"))
                .body("outputs.message", equalTo("failed"));
    }

    private void stop(String taskId, String user) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("stop"))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("Reserved"))
                .body("status.terminate", equalTo(null))
                .body("actualOwner", equalTo(user));
    }

    private void release(String taskId, String user) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("release"))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("Ready"))
                .body("status.terminate", equalTo(null))
                .body("actualOwner", equalTo(null));
    }

    private List<String> delegate(String taskId, String user, String[] potentialUsers, String delegatedUser) {
        var updatePotentialUsers = Stream.concat(Stream.of(potentialUsers), Stream.of(delegatedUser)).distinct().toArray(String[]::new);
        return given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("delegate", Map.of("DELEGATED_USER", delegatedUser)))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("Reserved"))
                .body("status.terminate", equalTo(null))
                .body("potentialUsers", hasItems(updatePotentialUsers))
                .body("actualOwner", equalTo(delegatedUser))
                .extract()
                .path("potentialUsers");
    }

    private List<String> forward(String taskId, String user, String[] potentialUsers, String[] forwardedUsers) {
        var updatePotentialUsers = Stream.concat(Arrays.stream(potentialUsers).filter(u -> !user.equals(u)), Arrays.stream(forwardedUsers))
                .toArray(String[]::new);
        return given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("forward", Map.of("FORWARDED_USERS", forwardedUsers)))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("Ready"))
                .body("status.terminate", equalTo(null))
                .body("potentialUsers", hasItems(updatePotentialUsers))
                .extract()
                .path("potentialUsers");
    }

    private void claim(String taskId, String user) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("claim"))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("Reserved"))
                .body("status.terminate", equalTo(null));
    }

    public String startProcessInstance(String processId) {
        return startProcessInstanceWithVariables(processId, Map.of());
    }

    public String startProcessInstanceWithVariables(String processId, Map<String, Object> variables) {
        String pid = given().contentType(ContentType.JSON)
                .when()
                .body(variables)
                .post("/{processId}", processId)
                .then()
                .statusCode(201)
                .header("Location", not(emptyOrNullString()))
                .body("id", not(emptyOrNullString()))
                .extract()
                .path("id");

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/{processId}/{id}", processId, pid)
                .then()
                .statusCode(200)
                .body("id", equalTo(pid));

        return pid;
    }

    private void isProcessCompleted(String processId, String pid) {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/{processId}/{pid}", processId, pid)
                .then()
                .statusCode(404);
    }

    private String getTaskId(String user, String pid) {
        return given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .get(USER_TASKS_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .param("pid", pid)
                .getString("find { it.processInfo.processInstanceId == pid }.id");
    }

    private void verifyTask(String processId, String pid, String taskId, String user, String state, String[] potentialUsers) {
        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .get(USER_TASKS_INSTANCE_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo(state))
                .body("taskName", equalTo("Task"))
                .body("potentialUsers", hasItems(potentialUsers))
                .body("processInfo.processInstanceId", equalTo(pid))
                .body("processInfo.processId", equalTo(processId))
                .body("processInfo.processVersion", equalTo("1.0"))
                .body("metadata.Skippable", equalTo("true"))
                .body("metadata.Lifecycle", equalTo("ws-human-task"))
                .body("metadata.ProcessType", equalTo("BPMN"))
                .body("metadata.ProcessInstanceState", equalTo(1));
    }

    private void complete(String taskId, String user) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("complete"))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("Completed"))
                .body("status.terminate", equalTo("COMPLETED"));
    }

    private void start(String taskId, String user) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("start"))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("InProgress"))
                .body("status.terminate", equalTo(null));
    }

    private void suspendWithDurationOrTimestamp(String taskId, String user, String temporal) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("suspend", Map.of("suspendUntil", temporal)))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("Suspended"))
                .body("status.terminate", equalTo(null))
                .body("metadata.SuspendedTaskJobId", not(emptyOrNullString()));
    }

    private void suspendWithInvalidDurationOrTimestamp(String taskId, String user, String temporal) {
        given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .body(new TransitionInfo("suspend", Map.of("suspendUntil", temporal)))
                .post(USER_TASKS_INSTANCE_TRANSITION_ENDPOINT, taskId)
                .then()
                .statusCode(400);
    }

    private void verifyTaskStatus(String taskId, String user, String expectedStatus) {
        verifyTaskStatus(taskId, user, expectedStatus, 5);
    }

    private void verifyTaskStatus(String taskId, String user, String expectedStatus, long timeout) {
        await()
                .atMost(Duration.ofSeconds(timeout))
                .untilAsserted(() -> given()
                        .contentType(ContentType.JSON)
                        .when()
                        .queryParam("user", user)
                        .get(USER_TASKS_INSTANCE_ENDPOINT, taskId)
                        .then()
                        .statusCode(200)
                        .body("id", equalTo(taskId))
                        .body("status.name", equalTo(expectedStatus)));
    }
}
