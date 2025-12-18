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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.it.KogitoSpringbootApplication;
import org.kie.kogito.testcontainers.springboot.PostgreSqlSpringBootTestResource;
import org.kie.kogito.usertask.model.TransitionInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers = PostgreSqlSpringBootTestResource.class)
public class WsHumanTaskLifeCycleIT {
    public static final String USER_TASKS_ENDPOINT = "/usertasks/instance";
    public static final String USER_TASKS_INSTANCE_ENDPOINT = USER_TASKS_ENDPOINT + "/{taskId}";
    public static final String USER_TASKS_INSTANCE_TRANSITION_ENDPOINT = USER_TASKS_INSTANCE_ENDPOINT + "/transition";

    @LocalServerPort
    int httpPort;

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void setPort() {
        RestAssured.port = httpPort;
    }

    @Test
    public void testUserTaskLifeCycle() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);
        claim(taskId, user);
        start(taskId, user);
        complete(taskId, user);
        isProcessCompleted(processId);
    }

    @Test
    public void testForwardTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var forwardedUsers = new String[] { "mark", "eric" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user);
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

        isProcessCompleted(processId);
    }

    @Test
    public void testDelegateTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user);
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

        isProcessCompleted(processId);
    }

    @Test
    public void testReleaseTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user);
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

        isProcessCompleted(processId);
    }

    @Test
    public void testStopTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        claim(taskId, user);
        start(taskId, user);
        stop(taskId, user);

        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId);
    }

    @Test
    public void testFailTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        claim(taskId, user);
        start(taskId, user);
        fail(taskId, user);

        isProcessCompleted(processId);
    }

    @Test
    public void testSuspendAndResumeTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user);
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

        isProcessCompleted(processId);
    }

    @Test
    public void testSkipTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        claim(taskId, user);
        start(taskId, user);
        skip(taskId, user);

        isProcessCompleted(processId);
    }

    @Test
    public void testFaultTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        claim(taskId, user);
        start(taskId, user);
        fault(taskId, user);

        isProcessCompleted(processId);
    }

    @Test
    public void testExitTransition() {
        var user = "dave";
        var potentialUsers = new String[] { "john", "dave" };
        var processId = "manager_multiple_users";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user);
        verifyTask(processId, pid, taskId, user, "Ready", potentialUsers);

        claim(taskId, user);
        start(taskId, user);
        exit(taskId, user);

        isProcessCompleted(processId);
    }

    @Test
    public void testNominateTransition() {
        var user = "carl";
        var processId = "manager_admin";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId("carl");
        verifyTask(processId, pid, taskId, user, "Created", new String[] {});

        var nominatedUsers = new String[] { "john", "dave" };
        nominate(taskId, user, "Ready", nominatedUsers);

        user = "john";
        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId);
    }

    @Test
    public void testSingleUserUserTaskLifeCycle() {
        var user = "jdoe";
        var processId = "manager_single_user";
        var pid = startProcessInstance(processId);
        var taskId = getTaskId(user);
        verifyTask(processId, pid, taskId, user, "Reserved", new String[] { user });

        start(taskId, user);
        complete(taskId, user);

        isProcessCompleted(processId);
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
                .body("status.terminate", equalTo(null));
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
        String pid = given().contentType(ContentType.JSON)
                .when()
                .body(Map.of())
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

    private void isProcessCompleted(String processId) {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/{processId}", processId)
                .then()
                .statusCode(200)
                .body("$.size()", is(0));
    }

    private String getTaskId(String user) {
        return given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", user)
                .get(USER_TASKS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .extract()
                .path("[0].id");
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
}
