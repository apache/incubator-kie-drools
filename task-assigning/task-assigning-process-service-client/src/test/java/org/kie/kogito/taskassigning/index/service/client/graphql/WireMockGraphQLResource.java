/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.index.service.client.graphql;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.kie.kogito.taskassigning.index.service.client.graphql.util.JsonUtils.OBJECT_MAPPER;

public class WireMockGraphQLResource implements QuarkusTestResourceLifecycleManager {

    public static final String GRAPHQL_SERVICE_URL = "graphql.service.url";

    public static final String USER_TASKS_QUERY_MOCK = "{\"query\": \"USER_TASKS_QUERY_MOCK\"}";

    public static final UserTaskInstanceMock[] USER_TASKS_QUERY_MOCK_RESULT = new UserTaskInstanceMock[]{
            new UserTaskInstanceMock("1", "task1", "2020-12-01T07:54:56.883Z", new String[]{"Group1"}, "{\"inputVariable1\":\"value1\"}"),
            new UserTaskInstanceMock("2", "task2", "2020-12-02T07:54:56.883Z", new String[]{"Group2"}, "{\"inputVariable2\":\"value2\"}"),
            new UserTaskInstanceMock("3", "task3", "2020-12-03T07:54:56.883Z", new String[]{"Group3"}, "{\"inputVariable3\":\"value3\"}")};

    public static final String USER_TASKS_QUERY_FAILURE_MOCK = "{\"query\": \"USER_TASKS_QUERY_FAILURE_MOCK\"}";

    public static final QueryError[] USER_TASKS_QUERY_FAILURE_MOCK_RESULT = new QueryError[]{
            new QueryError("Error1"),
            new QueryError("Error2")};

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor(wireMockServer.port());

        try {
            stubFor(post(urlEqualTo("/graphql/"))
                            .withRequestBody(equalToJson(USER_TASKS_QUERY_MOCK))
                            .willReturn(aResponse()
                                                .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                                                .withBody(buildUserTaskResult(USER_TASKS_QUERY_MOCK_RESULT))

                            )
            );

            stubFor(post(urlEqualTo("/graphql/"))
                            .withRequestBody(equalToJson(USER_TASKS_QUERY_FAILURE_MOCK))
                            .willReturn(aResponse()
                                                .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                                                .withBody(buildFailureResult(USER_TASKS_QUERY_FAILURE_MOCK_RESULT))

                            )
            );
        } catch (JsonProcessingException e) {
            //by construction this exception will never be produced, since the json generation is produced
            //by this test. In the rare case that it could still be produced only way to fail fast a quarkus test
            //resource is by throwing a RuntimeException.
            throw new RuntimeException(e);
        }
        return Collections.singletonMap(GRAPHQL_SERVICE_URL, wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    private static String buildUserTaskResult(UserTaskInstanceMock[] tasks) throws JsonProcessingException {
        MockQueryResult result = new MockQueryResult(new MockUserInstancesResult(tasks));
        return OBJECT_MAPPER.writeValueAsString(result);
    }

    private static String buildFailureResult(QueryError[] errors) throws JsonProcessingException {
        MockQueryFailure failure = new MockQueryFailure(errors);
        return OBJECT_MAPPER.writeValueAsString(failure);
    }

    public static class UserTaskInstanceMock {

        private String id;
        private String name;
        private String started;
        private String[] potentialGroups;
        private String inputs;

        public UserTaskInstanceMock(String id, String name, String started, String[] potentialGroups, String inputs) {
            this.id = id;
            this.name = name;
            this.started = started;
            this.potentialGroups = potentialGroups;
            this.inputs = inputs;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getStarted() {
            return started;
        }

        public String[] getPotentialGroups() {
            return potentialGroups;
        }

        public String getInputs() {
            return inputs;
        }
    }

    public static class MockUserInstancesResult {

        @JsonProperty("UserTaskInstances")
        private UserTaskInstanceMock[] tasks;

        public UserTaskInstanceMock[] getTasks() {
            return tasks;
        }

        public void setTasks(UserTaskInstanceMock[] tasks) {
            this.tasks = tasks;
        }

        public MockUserInstancesResult(UserTaskInstanceMock[] tasks) {
            this.tasks = tasks;
        }
    }

    public static class MockQueryResult {

        private Object data;

        public MockQueryResult(Object data) {
            this.data = data;
        }

        public Object getData() {
            return data;
        }
    }

    public static class MockQueryFailure {

        private QueryError[] errors;

        public MockQueryFailure(QueryError[] errors) {
            this.errors = errors;
        }

        public QueryError[] getErrors() {
            return errors;
        }
    }

    public static class QueryError {

        private String message;

        public QueryError(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
