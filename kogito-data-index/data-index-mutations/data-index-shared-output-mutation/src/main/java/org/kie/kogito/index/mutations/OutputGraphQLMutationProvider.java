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
package org.kie.kogito.index.mutations;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.kie.kogito.index.CommonUtils;
import org.kie.kogito.index.api.ExecuteArgs;
import org.kie.kogito.index.graphql.AbstractGraphQLSchemaManager;
import org.kie.kogito.index.graphql.GraphQLMutationsProvider;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessDefinitionKey;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.MergeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.idl.TypeDefinitionRegistry;

public class OutputGraphQLMutationProvider implements GraphQLMutationsProvider {

    private static Logger logger = LoggerFactory.getLogger(OutputGraphQLMutationProvider.class);
    private static final String COMPLETED_INSTANCE_ID = "completedInstanceId";

    @Override
    public Map<String, DataFetcher<CompletableFuture<?>>> mutations(AbstractGraphQLSchemaManager schemaManager) {
        return Map.of("ExecuteAfter", env -> sharedOutput(schemaManager, env));
    }

    private CompletableFuture<JsonNode> sharedOutput(AbstractGraphQLSchemaManager schemaManager, DataFetchingEnvironment env) {
        DataIndexStorageService cacheService = schemaManager.getCacheService();
        ProcessDefinitionKey key = new ProcessDefinitionKey(mandatoryArgument(env, "processId"), mandatoryArgument(env, "processVersion"));
        ProcessDefinition processDefinition = cacheService.getProcessDefinitionStorage().get(key);
        if (processDefinition == null) {
            throw new IllegalArgumentException(key + "does not correspond to any existing process definition");
        }
        JsonNode input = JsonObjectUtils.fromValue(env.getArgument("input"));
        String completedInstanceId = env.getArgument(COMPLETED_INSTANCE_ID);
        if (completedInstanceId != null) {
            ProcessInstance processInstance = cacheService.getProcessInstanceStorage().get(completedInstanceId);
            if (processInstance != null) {
                JsonNode variables = processInstance.getVariables();
                if (variables instanceof ObjectNode objectNode) {
                    objectNode.remove(env.getArgumentOrDefault("excludeProperties", Set.of("workflowdatainput")));
                }
                input = MergeUtils.merge(input, variables);
            } else {
                logger.warn("Completed Instance Id {} cannot be found, using user input as it is", completedInstanceId);
            }
        } else {
            logger.warn("Missing " + COMPLETED_INSTANCE_ID + " parameter, using user input as it is");
        }
        return schemaManager.getDataIndexApiExecutor().executeProcessInstance(processDefinition, ExecuteArgs.of(input));
    }

    private static <T> T mandatoryArgument(DataFetchingEnvironment env, String name) {
        T result = env.getArgument(name);
        if (result == null) {
            throw new IllegalArgumentException("Missing " + name + " mandatory parameter");
        }
        return result;
    }

    @Override
    public TypeDefinitionRegistry registry() {
        return CommonUtils.loadSchemaDefinitionFile("mutation.schema.graphqls");
    }
}
