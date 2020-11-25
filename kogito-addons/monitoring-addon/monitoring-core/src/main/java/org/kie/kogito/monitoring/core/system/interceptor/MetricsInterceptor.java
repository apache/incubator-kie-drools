/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.monitoring.core.system.interceptor;

import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.kie.kogito.monitoring.core.system.metrics.SystemMetricsCollector;

public class MetricsInterceptor implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {
        List<String> matchedUris = requestContext.getUriInfo().getMatchedURIs();
        if (!matchedUris.isEmpty()) {
            SystemMetricsCollector.registerStatusCodeRequest(matchedUris.get(0), String.valueOf(responseContext.getStatusInfo().getStatusCode()));
        } else // Log the number of requests that did not match any Uri -> 404 not found.
        {
            SystemMetricsCollector.registerStatusCodeRequest("NOT FOUND", String.valueOf(responseContext.getStatusInfo().getStatusCode()));
        }
    }
}