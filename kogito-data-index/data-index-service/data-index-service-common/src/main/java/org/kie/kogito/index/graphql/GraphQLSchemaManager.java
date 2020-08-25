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

package org.kie.kogito.index.graphql;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLNamedInputType;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.quarkus.arc.Arc;
import io.vertx.axle.core.eventbus.EventBus;
import io.vertx.axle.core.eventbus.Message;
import io.vertx.axle.core.eventbus.MessageConsumer;
import io.vertx.axle.core.eventbus.MessageProducer;
import org.kie.kogito.index.DataIndexStorageService;
import org.kie.kogito.index.graphql.query.GraphQLQueryOrderByParser;
import org.kie.kogito.index.graphql.query.GraphQLQueryParserRegistry;
import org.kie.kogito.index.json.DataIndexParsingException;
import org.kie.kogito.index.model.Job;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private ConcurrentMap<String, MessageProducer> producers = new ConcurrentHashMap<>();
    private GraphQLSchema schema;

    @PostConstruct
    public void setup() {
        schema = createSchema();
        GraphQLQueryParserRegistry.get().registerParsers(
                (GraphQLInputObjectType) schema.getType("ProcessInstanceArgument"),
                (GraphQLInputObjectType) schema.getType("UserTaskInstanceArgument"),
                (GraphQLInputObjectType) schema.getType("JobArgument"),
                (GraphQLInputObjectType) schema.getType("KogitoMetadataArgument")
        );
    }

    @PreDestroy
    public void destroy() {
        producers.values().forEach(MessageProducer::close);
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
                .type("ProcessInstance", builder -> {
                    builder.dataFetcher("parentProcessInstance", this::getParentProcessInstanceValue);
                    builder.dataFetcher("childProcessInstances", this::getChildProcessInstancesValues);
                    builder.dataFetcher("serviceUrl", this::getProcessInstanceServiceUrl);
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
            return endpoint.contains("/" + context) ? endpoint.substring(0, endpoint.indexOf("/" + context)) : null;
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
        return execute.size() > 0 ? execute.get(0) : null;
    }

    private Collection<ProcessInstance> getProcessInstancesValues(DataFetchingEnvironment env) {
        return executeAdvancedQueryForCache(cacheService.getProcessInstancesCache(), env);
    }

    private Collection<Job> getJobsValues(DataFetchingEnvironment env) {
        return executeAdvancedQueryForCache(cacheService.getJobsCache(), env);
    }

    private <T> List<T> executeAdvancedQueryForCache(Storage<String, T> cache, DataFetchingEnvironment env) {
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
        return ojectCreatedPublisher(PROCESS_INSTANCE_ADDED, cacheService.getProcessInstancesCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getProcessInstanceUpdatedDataFetcher() {
        return objectUpdatedPublisher(PROCESS_INSTANCE_UPDATED, cacheService.getProcessInstancesCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getUserTaskInstanceAddedDataFetcher() {
        return ojectCreatedPublisher(USER_TASK_INSTANCE_ADDED, cacheService.getUserTaskInstancesCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getUserTaskInstanceUpdatedDataFetcher() {
        return objectUpdatedPublisher(USER_TASK_INSTANCE_UPDATED, cacheService.getUserTaskInstancesCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getJobUpdatedDataFetcher() {
        return objectUpdatedPublisher(JOB_UPDATED, cacheService.getJobsCache());
    }

    private DataFetcher<Publisher<ObjectNode>> getJobAddedDataFetcher() {
        return ojectCreatedPublisher(JOB_ADDED, cacheService.getJobsCache());
    }

    private DataFetcher<Publisher<ObjectNode>> ojectCreatedPublisher(String address, Storage cache) {
        return env -> createPublisher(address, producer -> cache.addObjectCreatedListener(ut -> producer.write(getObjectMapper().convertValue(ut, ObjectNode.class))));
    }

    private DataFetcher<Publisher<ObjectNode>> objectUpdatedPublisher(String address, Storage cache) {
        return env -> createPublisher(address, producer -> cache.addObjectUpdatedListener(ut -> producer.write(getObjectMapper().convertValue(ut, ObjectNode.class))));
    }

    private Publisher<ObjectNode> createPublisher(String address, Consumer<MessageProducer<ObjectNode>> consumer) {
        EventBus eventBus = Arc.container().instance(EventBus.class).get();

        LOGGER.debug("Creating new message consumer for EventBus address: {}", address);
        MessageConsumer<ObjectNode> messageConsumer = eventBus.consumer(address);
        Publisher<ObjectNode> publisher = messageConsumer.toPublisherBuilder().map(Message::body).buildRs();

        producers.computeIfAbsent(address, key -> {
            LOGGER.debug("Creating new message publisher for EventBus address: {}", address);
            MessageProducer<ObjectNode> producer = eventBus.publisher(address);
            consumer.accept(producer);
            return producer;
        });

        return publisher;
    }

    protected DataFetcher<Publisher<ObjectNode>> getDomainModelUpdatedDataFetcher(String processId) {
        return env -> createPublisher(processId + "Updated", producer -> cacheService.getDomainModelCache(processId).addObjectUpdatedListener(producer::write));
    }

    protected DataFetcher<Publisher<ObjectNode>> getDomainModelAddedDataFetcher(String processId) {
        return env -> createPublisher(processId + "Added", producer -> cacheService.getDomainModelCache(processId).addObjectCreatedListener(producer::write));
    }

    protected DataFetcher<Collection<ObjectNode>> getDomainModelDataFetcher(String processId) {
        return env -> {
            List result = executeAdvancedQueryForCache(cacheService.getDomainModelCache(processId), env);
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
}
