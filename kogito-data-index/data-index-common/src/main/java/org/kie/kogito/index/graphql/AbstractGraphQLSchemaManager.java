/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.kie.kogito.index.graphql.query.GraphQLQueryOrderByParser;
import org.kie.kogito.index.graphql.query.GraphQLQueryParserRegistry;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import static java.util.Collections.singletonList;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.equalTo;

public abstract class AbstractGraphQLSchemaManager implements GraphQLSchemaManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGraphQLSchemaManager.class);

    @Inject
    DataIndexStorageService cacheService;

    @Inject
    GraphQLScalarType dateTimeScalarType;

    private GraphQLSchema schema;

    @PostConstruct
    public void setup() {
        schema = createSchema();
        GraphQLQueryParserRegistry.get().registerParsers(
                (GraphQLInputObjectType) schema.getType("ProcessInstanceArgument"),
                (GraphQLInputObjectType) schema.getType("UserTaskInstanceArgument"));
    }

    protected TypeDefinitionRegistry loadSchemaDefinitionFile(String fileName) {
        SchemaParser schemaParser = new SchemaParser();
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
                InputStreamReader reader = new InputStreamReader(stream)) {
            return schemaParser.parse(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract GraphQLSchema createSchema();

    public DataIndexStorageService getCacheService() {
        return cacheService;
    }

    public GraphQLScalarType getDateTimeScalarType() {
        return dateTimeScalarType;
    }

    public String getProcessInstanceServiceUrl(DataFetchingEnvironment env) {
        ProcessInstance source = env.getSource();
        if (source == null || source.getEndpoint() == null || source.getProcessId() == null) {
            return null;
        }
        return getServiceUrl(source.getEndpoint(), source.getProcessId());
    }

    protected String getServiceUrl(String endpoint, String processId) {
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

    protected Collection<ProcessInstance> getChildProcessInstancesValues(DataFetchingEnvironment env) {
        ProcessInstance source = env.getSource();
        Query<ProcessInstance> query = cacheService.getProcessInstancesCache().query();
        query.filter(singletonList(equalTo("parentProcessInstanceId", source.getId())));
        return query.execute();
    }

    protected ProcessInstance getParentProcessInstanceValue(DataFetchingEnvironment env) {
        ProcessInstance source = env.getSource();
        if (source.getParentProcessInstanceId() == null) {
            return null;
        }
        Query<ProcessInstance> query = cacheService.getProcessInstancesCache().query();
        query.filter(singletonList(equalTo("id", source.getParentProcessInstanceId())));
        List<ProcessInstance> execute = query.execute();
        return !execute.isEmpty() ? execute.get(0) : null;
    }

    protected Collection<ProcessInstance> getProcessInstancesValues(DataFetchingEnvironment env) {
        return executeAdvancedQueryForCache(cacheService.getProcessInstancesCache(), env);
    }

    protected <T> List<T> executeAdvancedQueryForCache(Storage<String, T> cache, DataFetchingEnvironment env) {
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

    protected Collection<UserTaskInstance> getUserTaskInstancesValues(DataFetchingEnvironment env) {
        return executeAdvancedQueryForCache(cacheService.getUserTaskInstancesCache(), env);
    }

    @Override
    public GraphQLSchema getGraphQLSchema() {
        return schema;
    }

    public void transform(Consumer<GraphQLSchema.Builder> builder) {
        schema = schema.transform(builder);
    }

}
