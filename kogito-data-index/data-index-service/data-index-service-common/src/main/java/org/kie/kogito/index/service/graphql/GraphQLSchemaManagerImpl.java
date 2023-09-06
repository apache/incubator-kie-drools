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
package org.kie.kogito.index.service.graphql;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.index.graphql.AbstractGraphQLSchemaManager;
import org.kie.kogito.index.graphql.query.GraphQLQueryParserRegistry;
import org.kie.kogito.index.json.DataIndexParsingException;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.service.DataIndexServiceException;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.reactivestreams.Publisher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.scalars.ExtendedScalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.equalTo;

@ApplicationScoped
public class GraphQLSchemaManagerImpl extends AbstractGraphQLSchemaManager {

    private static final String PROCESS_INSTANCE_ADDED = "ProcessInstanceAdded";
    private static final String PROCESS_INSTANCE_UPDATED = "ProcessInstanceUpdated";
    private static final String USER_TASK_INSTANCE_ADDED = "UserTaskInstanceAdded";
    private static final String USER_TASK_INSTANCE_UPDATED = "UserTaskInstanceUpdated";
    private static final String JOB_UPDATED = "JobUpdated";
    private static final String JOB_ADDED = "JobAdded";
    private static final String USER = "user";
    private static final String GROUPS = "groups";
    private static final String TASK_ID = "taskId";
    private static final String COMMENT_ID = "commentId";
    private static final String ATTACHMENT_ID = "attachmentId";

    @Override
    @PostConstruct
    public void setup() {
        super.setup();
        GraphQLQueryParserRegistry.get().registerParsers(
                (GraphQLInputObjectType) getGraphQLSchema().getType("KogitoMetadataArgument"),
                (GraphQLInputObjectType) getGraphQLSchema().getType("JobArgument"));
    }

    @Override
    public GraphQLSchema createSchema() {
        TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();
        typeDefinitionRegistry.merge(loadSchemaDefinitionFile("basic.schema.graphqls"));
        typeDefinitionRegistry.merge(loadSchemaDefinitionFile("domain.schema.graphqls"));

        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> {
                    builder.dataFetcher("ProcessDefinitions", this::getProcessDefinitionsValues);
                    builder.dataFetcher("ProcessInstances", this::getProcessInstancesValues);
                    builder.dataFetcher("UserTaskInstances", this::getUserTaskInstancesValues);
                    builder.dataFetcher("Jobs", this::getJobsValues);
                    return builder;
                })
                .type("Mutation", builder -> {
                    builder.dataFetcher("ProcessInstanceAbort", this::abortProcessInstance);
                    builder.dataFetcher("ProcessInstanceRetry", this::retryProcessInstance);
                    builder.dataFetcher("ProcessInstanceSkip", this::skipProcessInstance);
                    builder.dataFetcher("ProcessInstanceUpdateVariables", this::updateProcessInstanceVariables);
                    builder.dataFetcher("NodeInstanceTrigger", this::triggerNodeInstance);
                    builder.dataFetcher("NodeInstanceRetrigger", this::retriggerNodeInstance);
                    builder.dataFetcher("NodeInstanceCancel", this::cancelNodeInstance);
                    builder.dataFetcher("JobCancel", this::cancelJob);
                    builder.dataFetcher("JobReschedule", this::rescheduleJob);
                    builder.dataFetcher("UserTaskInstanceUpdate", this::updateUserTaskInstance);
                    builder.dataFetcher("UserTaskInstanceCommentCreate", this::createTaskInstanceComment);
                    builder.dataFetcher("UserTaskInstanceAttachmentCreate", this::createTaskInstanceAttachment);
                    builder.dataFetcher("UserTaskInstanceCommentUpdate", this::updateUserTaskComment);
                    builder.dataFetcher("UserTaskInstanceCommentDelete", this::deleteUserTaskComment);
                    builder.dataFetcher("UserTaskInstanceAttachmentUpdate", this::updateUserTaskAttachment);
                    builder.dataFetcher("UserTaskInstanceAttachmentDelete", this::deleteUserTaskAttachment);
                    return builder;
                })
                .type("ProcessDefinition", builder -> {
                    builder.dataFetcher("source", e -> getProcessDefinitionSource(e.getSource()));
                    builder.dataFetcher("nodes", e -> getProcessDefinitionNodes(e.getSource()));
                    builder.dataFetcher("serviceUrl", this::getProcessDefinitionServiceUrl);
                    return builder;
                })
                .type("ProcessInstance", builder -> {
                    builder.dataFetcher("parentProcessInstance", this::getParentProcessInstanceValue);
                    builder.dataFetcher("childProcessInstances", this::getChildProcessInstancesValues);
                    builder.dataFetcher("serviceUrl", this::getProcessInstanceServiceUrl);
                    builder.dataFetcher("diagram", this::getProcessInstanceDiagram);
                    builder.dataFetcher("source", this::getProcessInstanceSource);
                    builder.dataFetcher("nodeDefinitions", this::getProcessInstanceNodes);
                    builder.dataFetcher("definition", this::getProcessDefinition);
                    return builder;
                })
                .type("UserTaskInstance", builder -> {
                    builder.dataFetcher("schema", this::getUserTaskInstanceSchema);
                    return builder;
                })
                .type("ProcessInstanceMeta", builder -> {
                    builder.dataFetcher("serviceUrl", this::getProcessInstanceJsonServiceUrl);
                    return builder;
                })
                .type("ProcessInstanceState", builder -> {
                    builder.enumValues(name -> ProcessInstanceState.valueOf(name).ordinal());
                    return builder;
                })
                .type("Subscription", builder -> {
                    builder.dataFetcher(PROCESS_INSTANCE_ADDED, getProcessInstanceAddedDataFetcher());
                    builder.dataFetcher(PROCESS_INSTANCE_UPDATED, getProcessInstanceUpdatedDataFetcher());
                    builder.dataFetcher(USER_TASK_INSTANCE_ADDED, getUserTaskInstanceAddedDataFetcher());
                    builder.dataFetcher(USER_TASK_INSTANCE_UPDATED, getUserTaskInstanceUpdatedDataFetcher());
                    builder.dataFetcher(JOB_ADDED, getJobAddedDataFetcher());
                    builder.dataFetcher(JOB_UPDATED, getJobUpdatedDataFetcher());
                    return builder;
                })
                .scalar(getDateTimeScalarType())
                .scalar(ExtendedScalars.GraphQLBigDecimal)
                .scalar(ExtendedScalars.GraphQLLong)
                .scalar(ExtendedScalars.Json)
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    }

    public CompletableFuture<String> abortProcessInstance(DataFetchingEnvironment env) {
        ProcessInstance processInstance = getCacheService().getProcessInstancesCache().get(env.getArgument("id"));
        return getDataIndexApiExecutor().abortProcessInstance(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()), processInstance);
    }

    public CompletableFuture<String> retryProcessInstance(DataFetchingEnvironment env) {
        ProcessInstance processInstance = getCacheService().getProcessInstancesCache().get(env.getArgument("id"));
        return getDataIndexApiExecutor().retryProcessInstance(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()), processInstance);
    }

    public CompletableFuture<String> skipProcessInstance(DataFetchingEnvironment env) {
        ProcessInstance processInstance = getCacheService().getProcessInstancesCache().get(env.getArgument("id"));
        return getDataIndexApiExecutor().skipProcessInstance(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()), processInstance);
    }

    public CompletableFuture<String> updateProcessInstanceVariables(DataFetchingEnvironment env) {
        ProcessInstance processInstance = getCacheService().getProcessInstancesCache().get(env.getArgument("id"));
        return getDataIndexApiExecutor().updateProcessInstanceVariables(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()), processInstance, env.getArgument("variables"));
    }

    public CompletableFuture<String> triggerNodeInstance(DataFetchingEnvironment env) {
        ProcessInstance processInstance = getCacheService().getProcessInstancesCache().get(env.getArgument("id"));
        return getDataIndexApiExecutor().triggerNodeInstance(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()),
                processInstance,
                env.getArgument("nodeId"));
    }

    public CompletableFuture<String> retriggerNodeInstance(DataFetchingEnvironment env) {
        ProcessInstance processInstance = getCacheService().getProcessInstancesCache().get(env.getArgument("id"));
        return getDataIndexApiExecutor().retriggerNodeInstance(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()),
                processInstance,
                env.getArgument("nodeInstanceId"));
    }

    public CompletableFuture<String> cancelNodeInstance(DataFetchingEnvironment env) {
        ProcessInstance processInstance = getCacheService().getProcessInstancesCache().get(env.getArgument("id"));
        return getDataIndexApiExecutor().cancelNodeInstance(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()),
                processInstance,
                env.getArgument("nodeInstanceId"));
    }

    public CompletableFuture<String> cancelJob(DataFetchingEnvironment env) {
        Job job = getCacheService().getJobsCache().get(env.getArgument("id"));
        return getDataIndexApiExecutor().cancelJob(job.getEndpoint(), job);
    }

    public CompletableFuture<String> rescheduleJob(DataFetchingEnvironment env) {
        Job job = getCacheService().getJobsCache().get(env.getArgument("id"));
        return getDataIndexApiExecutor().rescheduleJob(job.getEndpoint(), job, env.getArgument("data"));
    }

    protected String getProcessInstanceJsonServiceUrl(DataFetchingEnvironment env) {
        Object source = env.getSource();
        if (source instanceof JsonNode) {
            String endpoint = ((JsonNode) source).get("endpoint").asText();
            String processId = ((JsonNode) source).get("processId").asText();
            return getServiceUrl(endpoint, processId);
        }
        return null;
    }

    private CompletableFuture<String> getUserTaskInstanceSchema(DataFetchingEnvironment env) {
        UserTaskInstance userTaskInstance = env.getSource();
        return getDataIndexApiExecutor().getUserTaskSchema(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument(USER),
                env.getArgument(GROUPS));
    }

    private CompletableFuture<String> updateUserTaskInstance(DataFetchingEnvironment env) {
        UserTaskInstance userTaskInstance = getCacheService().getUserTaskInstancesCache().get(env.getArgument(TASK_ID));
        return getDataIndexApiExecutor().updateUserTaskInstance(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument(USER),
                env.getArgument(GROUPS),
                env.getArguments());
    }

    private CompletableFuture<String> createTaskInstanceComment(DataFetchingEnvironment env) {
        UserTaskInstance userTaskInstance = getCacheService().getUserTaskInstancesCache().get(env.getArgument(TASK_ID));
        return getDataIndexApiExecutor().createUserTaskInstanceComment(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument(USER),
                env.getArgument(GROUPS),
                env.getArgument("comment"));
    }

    private CompletableFuture<String> createTaskInstanceAttachment(DataFetchingEnvironment env) {
        UserTaskInstance userTaskInstance = getCacheService().getUserTaskInstancesCache().get(env.getArgument(TASK_ID));
        return getDataIndexApiExecutor().createUserTaskInstanceAttachment(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument(USER),
                env.getArgument(GROUPS),
                env.getArgument("name"),
                env.getArgument("uri"));
    }

    private CompletableFuture<String> updateUserTaskComment(DataFetchingEnvironment env) {
        Query<UserTaskInstance> query = getCacheService().getUserTaskInstancesCache().query();
        query.filter(singletonList(equalTo("comments.id", env.getArgument(COMMENT_ID))));
        UserTaskInstance userTaskInstance = query.execute().get(0);
        return getDataIndexApiExecutor().updateUserTaskInstanceComment(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument(USER),
                env.getArgument(GROUPS),
                env.getArgument(COMMENT_ID),
                env.getArgument("comment"));
    }

    private CompletableFuture<String> deleteUserTaskComment(DataFetchingEnvironment env) {
        Query<UserTaskInstance> query = getCacheService().getUserTaskInstancesCache().query();
        query.filter(singletonList(equalTo("comments.id", env.getArgument(COMMENT_ID))));
        UserTaskInstance userTaskInstance = query.execute().get(0);
        return getDataIndexApiExecutor().deleteUserTaskInstanceComment(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument(USER),
                env.getArgument(GROUPS),
                env.getArgument(COMMENT_ID));
    }

    private CompletableFuture<String> updateUserTaskAttachment(DataFetchingEnvironment env) {
        Query<UserTaskInstance> query = getCacheService().getUserTaskInstancesCache().query();
        query.filter(singletonList(equalTo("attachments.id", env.getArgument(ATTACHMENT_ID))));
        UserTaskInstance userTaskInstance = query.execute().get(0);
        return getDataIndexApiExecutor().updateUserTaskInstanceAttachment(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument(USER),
                env.getArgument(GROUPS),
                env.getArgument(ATTACHMENT_ID),
                env.getArgument("name"),
                env.getArgument("uri"));
    }

    private CompletableFuture<String> deleteUserTaskAttachment(DataFetchingEnvironment env) {
        Query<UserTaskInstance> query = getCacheService().getUserTaskInstancesCache().query();
        query.filter(singletonList(equalTo("attachments.id", env.getArgument(ATTACHMENT_ID))));
        UserTaskInstance userTaskInstance = query.execute().get(0);
        return getDataIndexApiExecutor().deleteUserTaskInstanceAttachment(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument(USER),
                env.getArgument(GROUPS),
                env.getArgument(ATTACHMENT_ID));
    }

    private DataFetcher<Publisher<ObjectNode>> getProcessInstanceAddedDataFetcher() {
        return objectCreatedPublisher(() -> getCacheService().getProcessInstancesCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getProcessInstanceUpdatedDataFetcher() {
        return objectUpdatedPublisher(() -> getCacheService().getProcessInstancesCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getUserTaskInstanceAddedDataFetcher() {
        return objectCreatedPublisher(() -> getCacheService().getUserTaskInstancesCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getUserTaskInstanceUpdatedDataFetcher() {
        return objectUpdatedPublisher(() -> getCacheService().getUserTaskInstancesCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getJobUpdatedDataFetcher() {
        return objectUpdatedPublisher(() -> getCacheService().getJobsCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getJobAddedDataFetcher() {
        return objectCreatedPublisher(() -> getCacheService().getJobsCache());
    }

    private DataFetcher<Publisher<ObjectNode>> objectCreatedPublisher(Supplier<Storage> cache) {
        return env -> cache.get().objectCreatedListener();
    }

    private DataFetcher<Publisher<ObjectNode>> objectUpdatedPublisher(Supplier<Storage> cache) {
        return env -> cache.get().objectUpdatedListener();
    }

    private Supplier<DataIndexServiceException> cacheNotFoundException(String processId) {
        return () -> new DataIndexServiceException(format("Cache for process %s not found", processId));
    }

    protected DataFetcher<Publisher<ObjectNode>> getDomainModelUpdatedDataFetcher(String processId) {
        return env -> Optional.ofNullable(getCacheService().getDomainModelCache(processId)).orElseThrow(cacheNotFoundException(processId)).objectUpdatedListener();
    }

    protected DataFetcher<Publisher<ObjectNode>> getDomainModelAddedDataFetcher(String processId) {
        return env -> Optional.ofNullable(getCacheService().getDomainModelCache(processId)).orElseThrow(cacheNotFoundException(processId)).objectCreatedListener();
    }

    protected DataFetcher<Collection<ObjectNode>> getDomainModelDataFetcher(String processId) {
        return env -> {
            List result = executeAdvancedQueryForCache(Optional.ofNullable(getCacheService().getDomainModelCache(processId)).orElseThrow(cacheNotFoundException(processId)), env);
            return (Collection<ObjectNode>) result.stream().map(json -> {
                try {
                    return getObjectMapper().readTree(json.toString());
                } catch (IOException e) {
                    throw new DataIndexParsingException("Failed to parse JSON: " + e.getMessage(), e);
                }
            }).collect(toList());
        };
    }

}
