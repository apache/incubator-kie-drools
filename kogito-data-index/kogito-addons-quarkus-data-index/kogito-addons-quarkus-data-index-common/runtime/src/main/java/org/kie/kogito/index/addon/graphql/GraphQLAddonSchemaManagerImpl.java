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
package org.kie.kogito.index.addon.graphql;

import org.kie.kogito.index.graphql.AbstractGraphQLSchemaManager;
import org.kie.kogito.index.model.ProcessInstanceState;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GraphQLAddonSchemaManagerImpl extends AbstractGraphQLSchemaManager {

    public GraphQLSchema createSchema() {
        TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();
        typeDefinitionRegistry.merge(loadSchemaDefinitionFile("basic.schema.graphqls"));
        addCountQueries(typeDefinitionRegistry);
        addJsonQueries(typeDefinitionRegistry);
        loadAdditionalMutations(typeDefinitionRegistry);

        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> {
                    builder.dataFetcher("ProcessDefinitions", this::getProcessDefinitionsValues);
                    builder.dataFetcher("ProcessInstances", this::getProcessInstancesValues);
                    builder.dataFetcher("UserTaskInstances", this::getUserTaskInstancesValues);
                    builder.dataFetcher("Jobs", this::getJobsValues);
                    addCountQueries(builder);
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
                    loadAdditionalMutations(builder);
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
                    builder.dataFetcher("executionSummary", this::getExecutionSummary);
                    return builder;
                })
                .type("ProcessInstanceState", builder -> {
                    builder.enumValues(name -> ProcessInstanceState.valueOf(name).ordinal());
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
}
