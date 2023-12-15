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
package org.kie.kogito.addons.quarkus.camel.runtime;

import java.util.Collections;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static org.kie.kogito.addons.quarkus.camel.runtime.CamelConstants.BODY;
import static org.kie.kogito.addons.quarkus.camel.runtime.CamelConstants.HEADERS;

@ApplicationScoped
public class CamelCustomWorkItemHandler extends WorkflowWorkItemHandler {

    public static final String OPERATION = "operation";
    public static final String NAME = "CamelCustomWorkItemHandler";

    private static final Logger LOGGER = LoggerFactory.getLogger(CamelCustomWorkItemHandler.class);

    @Inject
    CamelContext context;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Camel Producer Template bound to the bean lifecycle. It's responsible to call the inner routes based on the endpoints defined by users in the workflow DSL.
     *
     * @see <a href="https://camel.apache.org/manual/faq/why-does-camel-use-too-many-threads-with-producertemplate.html">WHY DOES CAMEL USE TOO MANY THREADS WITH PRODUCERTEMPLATE?</a>
     */
    ProducerTemplate template;

    @PostConstruct
    void init() {
        template = context.createProducerTemplate();
    }

    @PreDestroy
    void stop() {
        if (template != null) {
            template.stop();
        }
    }

    @Override
    protected Object internalExecute(KogitoWorkItem workItem, Map<String, Object> parameters) {
        final Map<String, Object> metadata = workItem.getNodeInstance().getNode().getMetaData();
        final String camelEndpoint = (String) metadata.get(OPERATION);

        checkEndpointExists(camelEndpoint);

        if (parameters.isEmpty()) {
            LOGGER.debug("Invoking Camel Endpoint '{}' with no body or headers", camelEndpoint);
            return template.requestBody(camelEndpoint, "");
        }

        Object body = parameters.getOrDefault(BODY, parameters.values().iterator().next());
        Map<String, Object> headers = objectMapper.convertValue(parameters.getOrDefault(HEADERS, Collections.emptyMap()), new TypeReference<>() {
        });
        LOGGER.debug("Invoking Camel Endpoint '{}' with body '{}' and headers '{}'", camelEndpoint, body, headers);
        return template.requestBodyAndHeaders(camelEndpoint, body, headers);
    }

    private void checkEndpointExists(final String endpoint) {
        if (endpoint == null) {
            throw new IllegalArgumentException("Operation (the Camel Endpoint Identifier) is a mandatory parameter");
        }
        if (context.hasEndpoint(endpoint) == null) {
            throw new IllegalArgumentException("Endpoint '" + endpoint + "' doesn't exist. Make sure that the Camel Route is within the project's context.");
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
