/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.monitoring.core.quarkus;

import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.kie.kogito.monitoring.core.common.system.interceptor.MetricsInterceptor;

public class QuarkusMetricsInterceptor implements ContainerResponseFilter {

    private final MetricsInterceptor metricsInterceptor;

    public QuarkusMetricsInterceptor(MetricsInterceptor metricsInterceptor) {
        this.metricsInterceptor = metricsInterceptor;
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) {
        List<String> matchedUris = requestContext.getUriInfo().getMatchedURIs();
        String matchedUrl = matchedUris.isEmpty() ? null : matchedUris.get(0);
        metricsInterceptor.filter(matchedUrl, responseContext.getStatusInfo().getStatusCode());
    }
}
