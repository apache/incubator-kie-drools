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

import javax.enterprise.context.ApplicationScoped;

import org.kie.kogito.index.model.ProcessInstanceState;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;

@ApplicationScoped
public class GraphQLAddonSchemaManagerImpl extends AbstractGraphQLSchemaManager {

    protected GraphQLSchema createSchema() {
        TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();
        typeDefinitionRegistry.merge(loadSchemaDefinitionFile("basic.schema.graphqls"));

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
                .type("ProcessInstanceState", builder -> {
                    builder.enumValues(name -> ProcessInstanceState.valueOf(name).ordinal());
                    return builder;
                })
                .scalar(qlDateTimeScalarType)
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    }

}
