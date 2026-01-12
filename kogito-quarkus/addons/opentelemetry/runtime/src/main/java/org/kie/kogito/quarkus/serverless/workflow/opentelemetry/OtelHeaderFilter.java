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
package org.kie.kogito.quarkus.serverless.workflow.opentelemetry;

import java.io.IOException;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;

import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.ContextKeys;
import static org.kie.kogito.quarkus.serverless.workflow.opentelemetry.SonataFlowOtelAttributes.RequestProperties;

@Provider
@PreMatching
public class OtelHeaderFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    HeaderContextExtractor headerExtractor;

    @ConfigProperty(name = "sonataflow.otel.enabled", defaultValue = "true")
    boolean otelEnabled;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (!otelEnabled) {
            return;
        }

        OtelContextHolder.setHttpRequestContext(io.opentelemetry.context.Context.current());

        Map<String, String> extractedContext = headerExtractor.extractHeaders(requestContext.getHeaders());

        if (!extractedContext.isEmpty()) {
            requestContext.setProperty(ContextKeys.EXTRACTED_CONTEXT, extractedContext);

            for (Map.Entry<String, String> entry : extractedContext.entrySet()) {
                if (RequestProperties.TRANSACTION_ID.equals(entry.getKey())) {
                    OtelContextHolder.setTransactionId(entry.getValue());
                } else if (entry.getKey().startsWith(RequestProperties.TRACKER_PREFIX)) {
                    OtelContextHolder.setTrackerAttribute(entry.getKey(), entry.getValue());
                }
            }
        }

        String transactionId = extractedContext.get(RequestProperties.TRANSACTION_ID);
        if (transactionId != null) {
            requestContext.setProperty(ContextKeys.TRANSACTION_ID, transactionId);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        OtelContextHolder.clearHttpRequestContext();
        OtelContextHolder.clear();
    }
}
