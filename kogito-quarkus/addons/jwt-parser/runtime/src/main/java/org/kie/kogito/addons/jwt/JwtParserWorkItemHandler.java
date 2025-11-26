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
package org.kie.kogito.addons.jwt;

import java.util.Map;
import java.util.Optional;

import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemManager;
import org.kie.kogito.internal.process.workitem.WorkItemTransition;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * WorkItem handler for JWT token parsing operations in SonataFlow
 */
public class JwtParserWorkItemHandler extends DefaultKogitoWorkItemHandler {

    public static final String NAME = "jwt-parser";
    public static final String TOKEN_PARAM = "token";
    public static final String CLAIM_PARAM = "claim";
    public static final String OPERATION_PARAM = "operation";

    // Operations
    public static final String PARSE_OPERATION = "parse";
    public static final String EXTRACT_USER_OPERATION = "extractUser";
    public static final String EXTRACT_CLAIM_OPERATION = "extractClaim";

    private static final Logger logger = LoggerFactory.getLogger(JwtParserWorkItemHandler.class);

    private final JwtTokenParser jwtTokenParser;

    public JwtParserWorkItemHandler() {
        this.jwtTokenParser = new JwtTokenParser();
    }

    public JwtParserWorkItemHandler(JwtTokenParser jwtTokenParser) {
        this.jwtTokenParser = jwtTokenParser;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Optional<WorkItemTransition> activateWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        try {
            Map<String, Object> parameters = workItem.getParameters();
            String token = (String) parameters.get(TOKEN_PARAM);
            String operation = (String) parameters.getOrDefault(OPERATION_PARAM, PARSE_OPERATION);

            logger.debug("Executing JWT parser operation: {}", operation);

            JsonNode result;
            switch (operation.toLowerCase()) {
                case EXTRACT_USER_OPERATION:
                    result = jwtTokenParser.extractUser(token);
                    break;
                case EXTRACT_CLAIM_OPERATION:
                    String claimName = (String) parameters.get(CLAIM_PARAM);
                    if (claimName == null) {
                        throw new IllegalArgumentException("Claim name is required for extractClaim operation");
                    }
                    result = jwtTokenParser.extractClaim(token, claimName);
                    break;
                case PARSE_OPERATION:
                default:
                    result = jwtTokenParser.parseToken(token);
                    break;
            }

            // Complete the work item with the parsed result
            // Use JsonObjectUtils.fromValue to ensure proper serialization for workflow data access
            // Note: "Result" with capital R is the standard constant used by SonataFlow
            return Optional.of(handler.completeTransition(workItem.getPhaseStatus(), Map.of("Result", JsonObjectUtils.fromValue(result))));

        } catch (Exception e) {
            logger.error("Error executing JWT parser work item: {}", e.getMessage(), e);
            return Optional.of(handler.abortTransition(workItem.getPhaseStatus()));
        }
    }
}
