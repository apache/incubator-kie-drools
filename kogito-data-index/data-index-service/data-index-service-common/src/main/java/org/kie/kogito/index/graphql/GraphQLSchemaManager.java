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
package org.kie.kogito.index.graphql;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.index.DataIndexStorageService;
import org.kie.kogito.index.api.KogitoRuntimeClient;
import org.kie.kogito.index.graphql.query.GraphQLQueryOrderByParser;
import org.kie.kogito.index.graphql.query.GraphQLQueryParserRegistry;
import org.kie.kogito.index.json.DataIndexParsingException;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.Node;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.service.DataIndexServiceException;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.kie.kogito.index.json.JsonUtils.getObjectMapper;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.equalTo;

@ApplicationScoped
public class GraphQLSchemaManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLSchemaManager.class);
    private static final String PROCESS_INSTANCE_ADDED = "ProcessInstanceAdded";
    private static final String PROCESS_INSTANCE_UPDATED = "ProcessInstanceUpdated";
    private static final String USER_TASK_INSTANCE_ADDED = "UserTaskInstanceAdded";
    private static final String USER_TASK_INSTANCE_UPDATED = "UserTaskInstanceUpdated";
    private static final String JOB_UPDATED = "JobUpdated";
    private static final String JOB_ADDED = "JobAdded";

    @Inject
    DataIndexStorageService cacheService;

    @Inject
    GraphQLScalarType qlDateTimeScalarType;

    @Inject
    KogitoRuntimeClient dataIndexApiExecutor;

    private GraphQLSchema schema;

    @PostConstruct
    public void setup() {
        schema = createSchema();
        GraphQLQueryParserRegistry.get().registerParsers(
                (GraphQLInputObjectType) schema.getType("ProcessInstanceArgument"),
                (GraphQLInputObjectType) schema.getType("UserTaskInstanceArgument"),
                (GraphQLInputObjectType) schema.getType("JobArgument"),
                (GraphQLInputObjectType) schema.getType("KogitoMetadataArgument"));
    }

    private GraphQLSchema createSchema() {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("schema.graphqls");

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(new InputStreamReader(stream));

        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> {
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
                .type("ProcessInstance", builder -> {
                    builder.dataFetcher("parentProcessInstance", this::getParentProcessInstanceValue);
                    builder.dataFetcher("childProcessInstances", this::getChildProcessInstancesValues);
                    builder.dataFetcher("serviceUrl", this::getProcessInstanceServiceUrl);
                    builder.dataFetcher("diagram", this::getProcessInstanceDiagram);
                    builder.dataFetcher("source", this::getProcessInstanceSourceFileContent);
                    builder.dataFetcher("nodeDefinitions", this::getProcessNodes);
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
                .scalar(qlDateTimeScalarType)
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    }

    public CompletableFuture<String> abortProcessInstance(DataFetchingEnvironment env) {
        ProcessInstance processInstance = cacheService.getProcessInstancesCache().get(env.getArgument("id"));
        return dataIndexApiExecutor.abortProcessInstance(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()), processInstance);
    }

    public CompletableFuture<String> retryProcessInstance(DataFetchingEnvironment env) {
        ProcessInstance processInstance = cacheService.getProcessInstancesCache().get(env.getArgument("id"));
        return dataIndexApiExecutor.retryProcessInstance(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()), processInstance);
    }

    public CompletableFuture<String> skipProcessInstance(DataFetchingEnvironment env) {
        ProcessInstance processInstance = cacheService.getProcessInstancesCache().get(env.getArgument("id"));
        return dataIndexApiExecutor.skipProcessInstance(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()), processInstance);
    }

    public CompletableFuture<String> updateProcessInstanceVariables(DataFetchingEnvironment env) {
        ProcessInstance processInstance = cacheService.getProcessInstancesCache().get(env.getArgument("id"));
        return dataIndexApiExecutor.updateProcessInstanceVariables(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()), processInstance, env.getArgument("variables"));
    }

    public CompletableFuture<String> triggerNodeInstance(DataFetchingEnvironment env) {
        ProcessInstance processInstance = cacheService.getProcessInstancesCache().get(env.getArgument("id"));
        return dataIndexApiExecutor.triggerNodeInstance(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()),
                processInstance,
                env.getArgument("nodeId"));
    }

    public CompletableFuture<String> retriggerNodeInstance(DataFetchingEnvironment env) {
        ProcessInstance processInstance = cacheService.getProcessInstancesCache().get(env.getArgument("id"));
        return dataIndexApiExecutor.retriggerNodeInstance(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()),
                processInstance,
                env.getArgument("nodeInstanceId"));
    }

    public CompletableFuture<String> cancelNodeInstance(DataFetchingEnvironment env) {
        ProcessInstance processInstance = cacheService.getProcessInstancesCache().get(env.getArgument("id"));
        return dataIndexApiExecutor.cancelNodeInstance(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()),
                processInstance,
                env.getArgument("nodeInstanceId"));
    }

    public CompletableFuture<String> cancelJob(DataFetchingEnvironment env) {
        Job job = cacheService.getJobsCache().get(env.getArgument("id"));
        return dataIndexApiExecutor.cancelJob(job.getEndpoint(), job);
    }

    public CompletableFuture<String> rescheduleJob(DataFetchingEnvironment env) {
        Job job = cacheService.getJobsCache().get(env.getArgument("id"));
        return dataIndexApiExecutor.rescheduleJob(job.getEndpoint(), job, env.getArgument("data"));
    }

    public CompletableFuture getProcessInstanceDiagram(DataFetchingEnvironment env) {
        ProcessInstance processInstance = env.getSource();
        return dataIndexApiExecutor.getProcessInstanceDiagram(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()), processInstance);
    }

    public CompletableFuture<String> getProcessInstanceSourceFileContent(DataFetchingEnvironment env) {
        ProcessInstance processInstance = env.getSource();
        return dataIndexApiExecutor.getProcessInstanceSourceFileContent(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()), processInstance);
    }

    public CompletableFuture<List<Node>> getProcessNodes(DataFetchingEnvironment env) {
        ProcessInstance processInstance = env.getSource();
        return dataIndexApiExecutor.getProcessInstanceNodeDefinitions(getServiceUrl(processInstance.getEndpoint(), processInstance.getProcessId()), processInstance);
    }

    protected String getProcessInstanceServiceUrl(DataFetchingEnvironment env) {
        ProcessInstance source = env.getSource();
        if (source == null || source.getEndpoint() == null || source.getProcessId() == null) {
            return null;
        }
        return getServiceUrl(source.getEndpoint(), source.getProcessId());
    }

    protected String getProcessInstanceJsonServiceUrl(DataFetchingEnvironment env) {
        Object source = env.getSource();
        if (source != null && source instanceof JsonNode) {
            String endpoint = ((JsonNode) source).get("endpoint").asText();
            String processId = ((JsonNode) source).get("processId").asText();
            return getServiceUrl(endpoint, processId);
        }
        return null;
    }

    private String getServiceUrl(String endpoint, String processId) {
        LOGGER.debug("Process endpoint {}", endpoint);
        if (endpoint.startsWith("/")) {
            LOGGER.warn("Process '{}' endpoint '{}', does not contain full URL, please review the kogito.service.url system property to point the public URL for this runtime.",
                    processId, endpoint);
        }
        String context = getContext(processId);
        LOGGER.debug("Process context {}", context);
        if (context.equals(endpoint) || endpoint.equals("/" + context)) {
            return null;
        } else {
            return endpoint.contains("/" + context) ? endpoint.substring(0, endpoint.lastIndexOf("/" + context)) : null;
        }
    }

    private String getContext(String processId) {
        return processId.contains(".") ? processId.substring(processId.lastIndexOf('.') + 1) : processId;
    }

    private Collection<ProcessInstance> getChildProcessInstancesValues(DataFetchingEnvironment env) {
        ProcessInstance source = env.getSource();
        Query<ProcessInstance> query = cacheService.getProcessInstancesCache().query();
        query.filter(singletonList(equalTo("parentProcessInstanceId", source.getId())));
        return query.execute();
    }

    private ProcessInstance getParentProcessInstanceValue(DataFetchingEnvironment env) {
        ProcessInstance source = env.getSource();
        if (source.getParentProcessInstanceId() == null) {
            return null;
        }
        Query<ProcessInstance> query = cacheService.getProcessInstancesCache().query();
        query.filter(singletonList(equalTo("id", source.getParentProcessInstanceId())));
        List<ProcessInstance> execute = query.execute();
        return !execute.isEmpty() ? execute.get(0) : null;
    }

    private CompletableFuture getUserTaskInstanceSchema(DataFetchingEnvironment env) {
        UserTaskInstance userTaskInstance = env.getSource();
        return dataIndexApiExecutor.getUserTaskSchema(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument("user"),
                env.getArgument("groups"));
    }

    private CompletableFuture<String> updateUserTaskInstance(DataFetchingEnvironment env) {
        UserTaskInstance userTaskInstance = cacheService.getUserTaskInstancesCache().get(env.getArgument("taskId"));
        return dataIndexApiExecutor.updateUserTaskInstance(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument("user"),
                env.getArgument("groups"),
                env.getArguments());
    }

    private CompletableFuture<String> createTaskInstanceComment(DataFetchingEnvironment env) {
        UserTaskInstance userTaskInstance = cacheService.getUserTaskInstancesCache().get(env.getArgument("taskId"));
        return dataIndexApiExecutor.createUserTaskInstanceComment(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument("user"),
                env.getArgument("groups"),
                env.getArgument("comment"));
    }

    private CompletableFuture<String> createTaskInstanceAttachment(DataFetchingEnvironment env) {
        UserTaskInstance userTaskInstance = cacheService.getUserTaskInstancesCache().get(env.getArgument("taskId"));
        return dataIndexApiExecutor.createUserTaskInstanceAttachment(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument("user"),
                env.getArgument("groups"),
                env.getArgument("name"),
                env.getArgument("uri"));
    }

    private CompletableFuture<String> updateUserTaskComment(DataFetchingEnvironment env) {
        Query<UserTaskInstance> query = cacheService.getUserTaskInstancesCache().query();
        query.filter(singletonList(equalTo("comments.id", env.getArgument("commentId"))));
        UserTaskInstance userTaskInstance = query.execute().get(0);
        return dataIndexApiExecutor.updateUserTaskInstanceComment(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument("user"),
                env.getArgument("groups"),
                env.getArgument("commentId"),
                env.getArgument("comment"));
    }

    private CompletableFuture<String> deleteUserTaskComment(DataFetchingEnvironment env) {
        Query<UserTaskInstance> query = cacheService.getUserTaskInstancesCache().query();
        query.filter(singletonList(equalTo("comments.id", env.getArgument("commentId"))));
        UserTaskInstance userTaskInstance = query.execute().get(0);
        return dataIndexApiExecutor.deleteUserTaskInstanceComment(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument("user"),
                env.getArgument("groups"),
                env.getArgument("commentId"));
    }

    private CompletableFuture<String> updateUserTaskAttachment(DataFetchingEnvironment env) {
        Query<UserTaskInstance> query = cacheService.getUserTaskInstancesCache().query();
        query.filter(singletonList(equalTo("attachments.id", env.getArgument("attachmentId"))));
        UserTaskInstance userTaskInstance = query.execute().get(0);
        return dataIndexApiExecutor.updateUserTaskInstanceAttachment(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument("user"),
                env.getArgument("groups"),
                env.getArgument("attachmentId"),
                env.getArgument("name"),
                env.getArgument("uri"));
    }

    private CompletableFuture<String> deleteUserTaskAttachment(DataFetchingEnvironment env) {
        Query<UserTaskInstance> query = cacheService.getUserTaskInstancesCache().query();
        query.filter(singletonList(equalTo("attachments.id", env.getArgument("attachmentId"))));
        UserTaskInstance userTaskInstance = query.execute().get(0);
        return dataIndexApiExecutor.deleteUserTaskInstanceAttachment(getServiceUrl(userTaskInstance.getEndpoint(), userTaskInstance.getProcessId()),
                userTaskInstance,
                env.getArgument("user"),
                env.getArgument("groups"),
                env.getArgument("attachmentId"));
    }

    private Collection<ProcessInstance> getProcessInstancesValues(DataFetchingEnvironment env) {
        return executeAdvancedQueryForCache(cacheService.getProcessInstancesCache(), env);
    }

    private Collection<Job> getJobsValues(DataFetchingEnvironment env) {
        return executeAdvancedQueryForCache(cacheService.getJobsCache(), env);
    }

    private <T> List<T> executeAdvancedQueryForCache(Storage<String, T> cache, DataFetchingEnvironment env) {
        Objects.requireNonNull(cache, "Cache not found");

        String inputTypeName = ((GraphQLNamedType) env.getFieldDefinition().getArgument("where").getType()).getName();

        Query<T> query = cache.query();

        Map<String, Object> where = env.getArgument("where");
        query.filter(GraphQLQueryParserRegistry.get().getParser(inputTypeName).apply(where));

        query.sort(new GraphQLQueryOrderByParser().apply(env));

        Map<String, Integer> pagination = env.getArgument("pagination");
        if (pagination != null) {
            Integer limit = pagination.get("limit");
            if (limit != null) {
                query.limit(limit);
            }
            Integer offset = pagination.get("offset");
            if (offset != null) {
                query.offset(offset);
            }
        }

        return query.execute();
    }

    private Collection<UserTaskInstance> getUserTaskInstancesValues(DataFetchingEnvironment env) {
        return executeAdvancedQueryForCache(cacheService.getUserTaskInstancesCache(), env);
    }

    private DataFetcher<Publisher<ObjectNode>> getProcessInstanceAddedDataFetcher() {
        return objectCreatedPublisher(() -> cacheService.getProcessInstancesCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getProcessInstanceUpdatedDataFetcher() {
        return objectUpdatedPublisher(() -> cacheService.getProcessInstancesCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getUserTaskInstanceAddedDataFetcher() {
        return objectCreatedPublisher(() -> cacheService.getUserTaskInstancesCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getUserTaskInstanceUpdatedDataFetcher() {
        return objectUpdatedPublisher(() -> cacheService.getUserTaskInstancesCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getJobUpdatedDataFetcher() {
        return objectUpdatedPublisher(() -> cacheService.getJobsCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getJobAddedDataFetcher() {
        return objectCreatedPublisher(() -> cacheService.getJobsCache());
    }

    private DataFetcher<Publisher<ObjectNode>> objectCreatedPublisher(Supplier<Storage> cache) {
        return env -> cache.get().objectCreatedListener();
    }

    private DataFetcher<Publisher<ObjectNode>> objectUpdatedPublisher(Supplier<Storage> cache) {
        return env -> cache.get().objectUpdatedListener();
    }

    private Supplier<DataIndexServiceException> cacheNotFoundException(String processId) {
        return () -> new DataIndexServiceException(format("Cache for process %s not found", processId));
    };

    protected DataFetcher<Publisher<ObjectNode>> getDomainModelUpdatedDataFetcher(String processId) {
        return env -> Optional.ofNullable(cacheService.getDomainModelCache(processId)).orElseThrow(cacheNotFoundException(processId)).objectUpdatedListener();
    }

    protected DataFetcher<Publisher<ObjectNode>> getDomainModelAddedDataFetcher(String processId) {
        return env -> Optional.ofNullable(cacheService.getDomainModelCache(processId)).orElseThrow(cacheNotFoundException(processId)).objectCreatedListener();
    }

    protected DataFetcher<Collection<ObjectNode>> getDomainModelDataFetcher(String processId) {
        return env -> {
            List result = executeAdvancedQueryForCache(Optional.ofNullable(cacheService.getDomainModelCache(processId)).orElseThrow(cacheNotFoundException(processId)), env);
            return (Collection<ObjectNode>) result.stream().map(json -> {
                try {
                    return (ObjectNode) getObjectMapper().readTree(json.toString());
                } catch (IOException e) {
                    throw new DataIndexParsingException("Failed to parse JSON: " + e.getMessage(), e);
                }
            }).collect(toList());
        };
    }

    public GraphQLSchema getGraphQLSchema() {
        return schema;
    }

    public void transform(Consumer<GraphQLSchema.Builder> builder) {
        schema = schema.transform(builder);
    }

    public void setDataIndexApiExecutor(KogitoRuntimeClient dataIndexApiExecutor) {
        this.dataIndexApiExecutor = dataIndexApiExecutor;
    }
}
