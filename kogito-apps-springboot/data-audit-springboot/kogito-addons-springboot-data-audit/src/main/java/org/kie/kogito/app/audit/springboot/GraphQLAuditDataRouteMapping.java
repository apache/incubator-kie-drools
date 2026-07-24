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
package org.kie.kogito.app.audit.springboot;

import java.util.Map;
import java.util.stream.Collectors;

import org.kie.kogito.app.audit.api.DataAuditQuery;
import org.kie.kogito.app.audit.api.DataAuditQueryService;
import org.kie.kogito.app.audit.api.DataAuditStoreProxyService;
import org.kie.kogito.app.audit.graphql.GraphQLSchemaBuild;
import org.kie.kogito.app.audit.spi.DataAuditContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import graphql.ExecutionResult;
import jakarta.annotation.PostConstruct;

import static org.kie.kogito.app.audit.api.SubsystemConstants.DATA_AUDIT_QUERY_PATH;
import static org.kie.kogito.app.audit.api.SubsystemConstants.DATA_AUDIT_REGISTRY_PATH;
import static org.kie.kogito.app.audit.graphql.GraphQLSchemaManager.graphQLSchemaManagerInstance;

@RestController
@Transactional
public class GraphQLAuditDataRouteMapping {

    private DataAuditQueryService dataAuditQueryService;

    @Autowired
    DataAuditContextFactory dataAuditContextFactory;

    private DataAuditStoreProxyService dataAuditStoreProxyService;

    @PostConstruct
    public void init() {
        dataAuditQueryService = DataAuditQueryService.newAuditQuerySerice();
        dataAuditStoreProxyService = DataAuditStoreProxyService.newAuditStoreService();

        Map<String, String> queries =
                dataAuditStoreProxyService.findQueries(dataAuditContextFactory.newDataAuditContext()).stream()
                        .collect(Collectors.toMap(DataAuditQuery::getIdentifier, DataAuditQuery::getGraphQLDefinition));
        graphQLSchemaManagerInstance().init(dataAuditContextFactory.newDataAuditContext(), queries);
    }

    @PostMapping(value = DATA_AUDIT_QUERY_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> executeQuery(@RequestBody JsonNode query) {
        ExecutionResult executionResult = dataAuditQueryService.executeQuery(dataAuditContextFactory.newDataAuditContext(), query.get("query").asText());
        return executionResult.toSpecification();
    }

    @PostMapping(value = DATA_AUDIT_REGISTRY_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void registerQuery(@RequestBody DataAuditQuery dataAuditQuery) {
        dataAuditStoreProxyService.storeQuery(dataAuditContextFactory.newDataAuditContext(), dataAuditQuery);
        GraphQLSchemaBuild build = graphQLSchemaManagerInstance().devireNewDataAuditQuerySchema(dataAuditContextFactory.newDataAuditContext(), dataAuditQuery);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                graphQLSchemaManagerInstance().setGraphQLSchemaBuild(build);
            }
        });
    }

    @GetMapping(path = DATA_AUDIT_REGISTRY_PATH)
    public String blockingRegistryHandlerGet() {
        return graphQLSchemaManagerInstance().getGraphQLSchemaDefinition();
    }

    @ExceptionHandler({ Throwable.class })
    public ResponseEntity<String> handleException(Throwable th) {
        return ResponseEntity.badRequest().body("An Exception occurred processing the request. Please see the logs for more details.");
    }
}
