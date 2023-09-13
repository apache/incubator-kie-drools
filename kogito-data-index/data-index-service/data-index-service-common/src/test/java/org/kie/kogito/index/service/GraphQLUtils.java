/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.index.service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.model.UserTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.ArrayUtils.insert;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;

public class GraphQLUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLUtils.class);
    private static final Map<Class, String> QUERY_FIELDS = new HashMap<>();
    private static final Map<String, String> QUERIES = new HashMap<>();

    static {
        QUERY_FIELDS.put(ProcessDefinition.class, getAllFieldsList(ProcessDefinition.class).map(getFieldName()).collect(joining(", ")));
        QUERY_FIELDS.put(UserTaskInstance.class, getAllFieldsList(UserTaskInstance.class).map(getFieldName()).collect(joining(", ")));
        QUERY_FIELDS.put(ProcessInstance.class, getAllFieldsList(ProcessInstance.class).map(getFieldName()).collect(joining(", ")));
        QUERY_FIELDS.put(Job.class, getAllFieldsList(Job.class).map(getFieldName()).collect(joining(", ")));
        QUERY_FIELDS.computeIfPresent(ProcessInstance.class, (k, v) -> v + ", serviceUrl");
        QUERY_FIELDS.computeIfPresent(ProcessInstance.class, (k, v) -> v + ", childProcessInstances { id, processName }");
        QUERY_FIELDS.computeIfPresent(ProcessInstance.class, (k, v) -> v + ", parentProcessInstance { id, processName }");

        try {
            JsonNode node = getObjectMapper().readTree(Files.readString(Path.of(Thread.currentThread().getContextClassLoader().getResource("graphql_queries.json").toURI())));
            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext();) {
                Map.Entry<String, JsonNode> entry = it.next();
                QUERIES.put(entry.getKey(), entry.getValue().toString());
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to parse graphql_queries.json file: {}", ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public static String getProcessDefinitionByIdAndVersion(String id, String version) {
        return getProcessDefinitionQuery("ProcessDefinitionByIdAndVersion", id, version);
    }

    public static String getProcessInstanceById(String id) {
        return getProcessInstanceQuery("ProcessInstanceById", id);
    }

    public static String getProcessInstanceByIdAndState(String id, ProcessInstanceState state) {
        return getProcessInstanceQuery("ProcessInstanceByIdAndState", id, state.name());
    }

    public static String getProcessInstanceByIdAndStart(String id, String start) {
        return getProcessInstanceQuery("ProcessInstanceByIdAndStart", id, start);
    }

    public static String getProcessInstanceByIdAndProcessId(String id, String processId) {
        return getProcessInstanceQuery("ProcessInstanceByIdAndProcessId", id, processId);
    }

    public static String getProcessInstanceByIdAndParentProcessInstanceId(String id, String parentProcessInstanceId) {
        return getProcessInstanceQuery("ProcessInstanceByIdAndParentProcessInstanceId", id, parentProcessInstanceId);
    }

    public static String getProcessInstanceByParentProcessInstanceId(String parentProcessInstanceId) {
        return getProcessInstanceQuery("ProcessInstanceByParentProcessInstanceId", parentProcessInstanceId);
    }

    public static String getProcessInstanceByIdAndNullParentProcessInstanceId(String id, Boolean isNull) {
        return getProcessInstanceQuery("ProcessInstanceByIdAndNullParentProcessInstanceId", id, isNull.toString());
    }

    public static String getProcessInstanceByIdAndNullRootProcessInstanceId(String id, Boolean isNull) {
        return getProcessInstanceQuery("ProcessInstanceByIdAndNullRootProcessInstanceId", id, isNull.toString());
    }

    public static String getProcessInstanceByRootProcessInstanceId(String rootProcessInstanceId) {
        return getProcessInstanceQuery("ProcessInstanceByRootProcessInstanceId", rootProcessInstanceId);
    }

    public static String getProcessInstanceByIdAndErrorNode(String id, String nodeDefinitionId) {
        return getProcessInstanceQuery("ProcessInstanceByIdAndErrorNode", id, nodeDefinitionId);
    }

    public static String getProcessInstanceByIdAndAddon(String id, String addon) {
        return getProcessInstanceQuery("ProcessInstanceByIdAndAddon", id, addon);
    }

    public static String getProcessInstanceByIdAndMilestoneName(String id, String milestone) {
        return getProcessInstanceQuery("ProcessInstanceByIdAndMilestoneName", id, milestone);
    }

    public static String getProcessInstanceByIdAndMilestoneStatus(String id, String status) {
        return getProcessInstanceQuery("ProcessInstanceByIdAndMilestoneStatus", id, status);
    }

    public static String getProcessInstanceByBusinessKey(String businessKeys) {
        return getProcessInstanceQuery("ProcessInstanceByBusinessKey", businessKeys);
    }

    public static String getProcessInstanceByCreatedBy(String identity) {
        return getProcessInstanceQuery("ProcessInstanceByCreatedBy", identity);
    }

    public static String getProcessInstanceByUpdatedBy(String identity) {
        return getProcessInstanceQuery("ProcessInstanceByUpdatedBy", identity);
    }

    public static String getUserTaskInstanceById(String id) {
        return getUserTaskInstanceQuery("UserTaskInstanceById", id);
    }

    public static String getUserTaskInstanceByProcessInstanceId(String id) {
        return getUserTaskInstanceQuery("UserTaskInstanceByProcessInstanceId", id);
    }

    public static String getUserTaskInstanceByIdAndActualOwner(String id, String actualOwner) {
        return getUserTaskInstanceQuery("UserTaskInstanceByIdAndActualOwner", id, actualOwner);
    }

    public static String getUserTaskInstanceByIdAndProcessId(String id, String processId) {
        return getUserTaskInstanceQuery("UserTaskInstanceByIdAndProcessId", id, processId);
    }

    public static String getUserTaskInstanceByIdNoActualOwner(String id) {
        return getUserTaskInstanceQuery("UserTaskInstanceByIdNoActualOwner", id);
    }

    public static String getUserTaskInstanceByIdAndState(String id, String state) {
        return getUserTaskInstanceQuery("UserTaskInstanceByIdAndState", id, state);
    }

    public static String getUserTaskInstanceByIdAndStarted(String id, String started) {
        return getUserTaskInstanceQuery("UserTaskInstanceByIdAndStarted", id, started);
    }

    public static String getUserTaskInstanceByIdAndCompleted(String id, String completed) {
        return getUserTaskInstanceQuery("UserTaskInstanceByIdAndCompleted", id, completed);
    }

    public static String getUserTaskInstanceByIdAndPotentialGroups(String id, List<String> potentialGroups) throws Exception {
        return getUserTaskInstanceWithArray("UserTaskInstanceByIdAndPotentialGroups", potentialGroups, "potentialGroups", id);
    }

    public static String getUserTaskInstanceByIdAndPotentialUsers(String id, List<String> potentialUsers) throws Exception {
        return getUserTaskInstanceWithArray("UserTaskInstanceByIdAndPotentialUsers", potentialUsers, "potentialUsers", id);
    }

    public static String getJobById(String id) {
        return getJobQuery("JobById", id);
    }

    public static String getTravelsByUserTaskId(String id) {
        return getQuery("TravelsByUserTaskId", id);
    }

    public static String getTravelsByProcessInstanceId(String id) {
        return getQuery("TravelsByProcessInstanceId", id);
    }

    public static String getTravelsByProcessInstanceIdAndTravellerFirstName(String id, String name) {
        return getQuery("TravelsByProcessInstanceIdAndTravellerFirstName", id, name);
    }

    public static String getDealsByTaskId(String id) {
        return getQuery("DealsByTaskId", id);
    }

    public static String getDealsByTaskIdNoActualOwner(String id) {
        return getQuery("DealsByTaskIdNoActualOwner", id);
    }

    private static String getUserTaskInstanceWithArray(String query, List<String> values, String variable, String... args) throws Exception {
        String json = getUserTaskInstanceQuery(query, args);
        ObjectNode jsonNode = (ObjectNode) getObjectMapper().readTree(json);
        ArrayNode pg = (ArrayNode) jsonNode.get("variables").get(variable);
        values.forEach(g -> pg.add(g));
        return jsonNode.toString();
    }

    private static String getQuery(String name, String... args) {
        return format(QUERIES.get(name), args);
    }

    private static String getProcessInstanceQuery(String name, String... args) {
        return getQuery(name, ProcessInstance.class, args);
    }

    private static String getProcessDefinitionQuery(String name, String... args) {
        return getQuery(name, ProcessDefinition.class, args);
    }

    private static String getUserTaskInstanceQuery(String name, String... args) {
        return getQuery(name, UserTaskInstance.class, args);
    }

    private static String getJobQuery(String name, String... args) {
        return getQuery(name, Job.class, args);
    }

    private static String getQuery(String name, Class clazz, String... args) {
        return format(QUERIES.get(name), insert(0, args, QUERY_FIELDS.get(clazz)));
    }

    private static Stream<Field> getAllFieldsList(Class clazz) {
        return FieldUtils.getAllFieldsList(clazz).stream().filter(getSoourcePredicate().or(getSoourcePredicate()));
    }

    private static Function<Field, String> getFieldName() {
        return field -> {
            if (field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                StringBuilder builder = new StringBuilder();
                builder.append(Arrays.stream(genericType.getActualTypeArguments()).filter(type -> type.getTypeName().startsWith("org.kie.kogito.index.model"))
                        .flatMap(type -> {
                            try {
                                return getAllFieldsList(Class.forName(type.getTypeName()));
                            } catch (Exception ex) {
                                return Stream.empty();
                            }
                        }).map(f -> getFieldName().apply(f)).collect(joining(", ")));
                if (builder.length() > 0) {
                    return field.getName() + " { " + builder.toString() + " }";
                }
            }

            if (field.getType().getName().startsWith("org.kie.kogito.index.model")) {
                return field.getName() + " { " + getAllFieldsList(field.getType()).map(f -> getFieldName().apply(f)).collect(joining(", ")) + " }";
            }

            return field.getName();
        };
    }

    //  See https://www.eclemma.org/jacoco/trunk/doc/faq.html    
    private static Predicate<Field> getJacocoPredicate() {
        return field -> !field.getName().equals("$jacocoData");
    }

    private static Predicate<Field> getSoourcePredicate() {
        return field -> !(field.getDeclaringClass().equals(ProcessDefinition.class) && (field.getName().equals("source") || field.getName().equals("nodes")));
    }
}
