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

import org.kie.kogito.app.audit.api.DataAuditQueryService;
import org.kie.kogito.app.audit.spi.DataAuditContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import graphql.ExecutionResult;
import jakarta.annotation.PostConstruct;

import static org.kie.kogito.app.audit.api.SubsystemConstants.DATA_AUDIT_PATH;

@RestController
@Transactional
public class GraphQLJPAAuditDataRouteMapping {

    private DataAuditQueryService dataAuditQueryService;

    @Autowired
    DataAuditContextFactory dataAuditContextFactory;

    @PostConstruct
    public void init() {
        dataAuditQueryService = DataAuditQueryService.newAuditQuerySerice();

    }

    @PostMapping(value = DATA_AUDIT_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> executeQuery(@RequestBody JsonNode query) {
        ExecutionResult executionResult = dataAuditQueryService.executeQuery(dataAuditContextFactory.newDataAuditContext(), query.get("query").asText());
        return executionResult.toSpecification();
    }

}
