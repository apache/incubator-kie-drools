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
package org.kie.kogito.app.audit.api;

import java.util.Map;

import org.kie.kogito.app.audit.graphql.GraphQLSchemaManager;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

import static java.util.Collections.emptyMap;
import static org.kie.kogito.app.audit.graphql.GraphQLSchemaManager.graphQLSchemaManagerInstance;

public class DataAuditQueryService {

    private GraphQLSchemaManager graphQLManager;

    private DataAuditQueryService(GraphQLSchemaManager graphQLManager) {
        this.graphQLManager = graphQLManager;
    }

    public GraphQLSchema getGraphQLSchema() {
        return this.graphQLManager.getGraphQLSchema();
    }

    public GraphQL getGraphQL() {
        return this.graphQLManager.getGraphQL();
    }

    public ExecutionResult executeQuery(DataAuditContext context, String query) {
        return executeQuery(context, query, emptyMap());
    }

    public ExecutionResult executeQuery(DataAuditContext context, String query, Map<String, Object> variables) {
        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .localContext(context)
                .query(query)
                .variables(variables)
                .build();

        return graphQLManager.execute(executionInput);
    }

    public static DataAuditQueryService newAuditQuerySerice() {
        return new DataAuditQueryService(graphQLSchemaManagerInstance());
    }
}
