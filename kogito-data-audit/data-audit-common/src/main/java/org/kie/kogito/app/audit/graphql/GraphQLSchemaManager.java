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
package org.kie.kogito.app.audit.graphql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

import org.kie.kogito.app.audit.api.DataAuditContext;
import org.kie.kogito.app.audit.api.DataAuditQuery;
import org.kie.kogito.app.audit.spi.GraphQLSchemaQuery;
import org.kie.kogito.app.audit.spi.GraphQLSchemaQueryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.SchemaPrinter;
import graphql.schema.idl.TypeDefinitionRegistry;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class GraphQLSchemaManager {

    private static final GraphQLSchemaManager INSTANCE = new GraphQLSchemaManager();

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLSchemaManager.class);

    private GraphQLSchema graphQLSchema;

    private GraphQL graphQL;

    private Map<String, String> graphQLdefinitions;

    public static GraphQLSchemaManager graphQLSchemaManagerInstance() {
        return INSTANCE;
    }

    private GraphQLSchemaManager() {
        this.graphQLdefinitions = new HashMap<>();
    }

    public GraphQLSchemaBuild rebuildDefinitions(DataAuditContext dataAuditContext, Map<String, String> additionalDefinitions) {
        LOGGER.debug("Rebuilding graphQL definitions");
        RuntimeWiring.Builder runtimeWiringBuilder = newRuntimeWiring();

        runtimeWiringBuilder.scalar(ExtendedScalars.GraphQLBigInteger);
        runtimeWiringBuilder.scalar(ExtendedScalars.GraphQLLong);
        runtimeWiringBuilder.scalar(ExtendedScalars.Date);
        runtimeWiringBuilder.scalar(ExtendedScalars.DateTime);
        runtimeWiringBuilder.scalar(ExtendedScalars.Json);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        classLoader = (classLoader != null) ? classLoader : this.getClass().getClassLoader();

        TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();

        List<String> graphqlSchemas = new ArrayList<>();
        graphqlSchemas.addAll(
                List.of("META-INF/data-audit-types.graphqls",
                        "META-INF/data-audit-job-query.graphqls",
                        "META-INF/data-audit-process-query.graphqls",
                        "META-INF/data-audit-usertask-query.graphqls"));

        ServiceLoader.load(GraphQLSchemaQueryProvider.class, classLoader).forEach(queryProvider -> {
            graphqlSchemas.addAll(List.of(queryProvider.graphQLQueryExtension()));
            for (GraphQLSchemaQuery query : queryProvider.queries(dataAuditContext)) {
                runtimeWiringBuilder.type("Query", builder -> builder.dataFetcher(query.name(), query::fetch));
            }
        });

        List<InputStream> data = new ArrayList<>();
        data.addAll(graphqlSchemas.stream().map(this::toInputStream).toList());
        data.addAll(additionalDefinitions.values().stream().map(String::getBytes).map(ByteArrayInputStream::new).toList());

        // now we have all of definitions
        List<FieldDefinition> queryDefinitions = new ArrayList<>();
        for (InputStream graphQLSchema : data) {
            TypeDefinitionRegistry newTypes = readDefinitionRegistry(graphQLSchema);

            // for allowing extension of the schema we need to merge this object manually
            // we remove it from the new Types and aggregate in temporal list so we can add this at the end
            // of extension processing
            Optional<ObjectTypeDefinition> newDefinitions = newTypes.getType("Query", ObjectTypeDefinition.class);
            if (newDefinitions.isPresent()) {
                queryDefinitions.addAll(newDefinitions.get().getFieldDefinitions());
                newTypes.remove(newDefinitions.get());
            }
            typeDefinitionRegistry.merge(newTypes);
        }

        RuntimeWiring runtimeWiring = runtimeWiringBuilder.build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        // we merge the query object
        typeDefinitionRegistry.add(ObjectTypeDefinition.newObjectTypeDefinition().name("Query").fieldDefinitions(queryDefinitions).build());
        GraphQLSchema newGraphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        GraphQL newGraphQL = GraphQL.newGraphQL(newGraphQLSchema).build();

        LOGGER.debug("Succesfuly rebuilding graphQL definitions");
        return new GraphQLSchemaBuild(newGraphQLSchema, newGraphQL, additionalDefinitions);
    }

    private InputStream toInputStream(String classpathFile) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(classpathFile)) {
            return new ByteArrayInputStream(is.readAllBytes());
        } catch (IOException e) {
            LOGGER.error("could not find or process {}", graphQLSchema, e);
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    private TypeDefinitionRegistry readDefinitionRegistry(InputStream inputStream) {
        SchemaParser schemaParser = new SchemaParser();
        return schemaParser.parse(inputStream);
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }

    public GraphQLSchema getGraphQLSchema() {
        return graphQLSchema;
    }

    public ExecutionResult execute(ExecutionInput executionInput) {
        return graphQL.execute(executionInput);
    }

    public String getGraphQLSchemaDefinition() {
        SchemaPrinter printer = new SchemaPrinter();
        return printer.print(graphQL.getGraphQLSchema());
    }

    public void init(DataAuditContext dataAuditContext, Map<String, String> additionalQueries) {
        setGraphQLSchemaBuild(rebuildDefinitions(dataAuditContext, additionalQueries));
    }

    public GraphQLSchemaBuild devireNewDataAuditQuerySchema(DataAuditContext dataAuditContext, DataAuditQuery dataAuditQuery) {
        String graphQLDefinition = dataAuditQuery.getGraphQLDefinition();
        TypeDefinitionRegistry registry = readDefinitionRegistry(new ByteArrayInputStream(graphQLDefinition.getBytes()));
        LOGGER.debug("Registering data audit query {} with definition {}", dataAuditQuery.getIdentifier(), registry.getType("Query"));
        Map<String, String> additionalDefinitions = new HashMap<>(this.graphQLdefinitions);
        additionalDefinitions.put(dataAuditQuery.getIdentifier(), dataAuditQuery.getGraphQLDefinition());
        return rebuildDefinitions(dataAuditContext, additionalDefinitions);
    }

    public void setGraphQLSchemaBuild(GraphQLSchemaBuild build) {
        this.graphQL = build.graphQL();
        this.graphQLSchema = build.graphQLSchema();
        this.graphQLdefinitions = build.additionalDefinitions();
    }

}
