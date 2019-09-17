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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.kie.kogito.index.model.ProcessInstanceState;
import org.kie.kogito.index.query.QueryService;

@ApplicationScoped
public class GraphQLSchemaManager {

    @Inject
    QueryService queryService;

    @Inject
    GraphQLScalarType qlDateTimeScalarType;

    private GraphQLSchema schema;

    @PostConstruct
    public void setup() {
        schema = createSchema();
    }

    private GraphQLSchema createSchema() {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("schema.graphqls");

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(new InputStreamReader(stream));

        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> {
                    builder.dataFetcher("ProcessInstances", env -> getProcessInstancesValues(env));
                    return builder;
                })
                .type("ProcessInstanceState", builder -> {
                    builder.enumValues(name -> ProcessInstanceState.valueOf(name).ordinal());
                    return builder;
                })
                .scalar(qlDateTimeScalarType)
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    }

    private Collection<JsonObject> getProcessInstancesValues(DataFetchingEnvironment env) {
        Map<String, Object> filter = env.getArgument("filter");
        return queryService.queryProcessInstances(new ProcessInstanceFilterMapper().apply(filter));
    }

    public GraphQLSchema getGraphQLSchema() {
        return schema;
    }

    public void transform(Consumer<GraphQLSchema.Builder> builder) {
        schema = schema.transform(builder);
    }
}
