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
package org.kie.kogito.addons.k8s.workitems;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.kie.api.runtime.process.WorkItem;
import org.kie.kogito.addons.k8s.Endpoint;
import org.kie.kogito.addons.k8s.EndpointDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This service is meant to be used with KogitoWorkItemHandlers to call well-known Kogito services deployed in the very same Kubernetes cluster.
 */
/*
 * TODO: review this implementation with the team. This class should be part of kogito-rest-workitem. Then we can inject the discoverability to that use case.
 * see more at https://issues.redhat.com/browse/KOGITO-6109. This implementation is inherited from the old "DiscoveredServiceWorkItemHandler".
 */
public abstract class AbstractDiscoveredEndpointCaller {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDiscoveredEndpointCaller.class);
    private static final List<String> INTERNAL_FIELDS = Arrays.asList("TaskName", "ActorId", "GroupId", "Priority", "Comment", "Skippable", "Content", "Model", "Namespace");

    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;

    public AbstractDiscoveredEndpointCaller(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    protected abstract EndpointDiscovery getEndpointDiscovery();

    /**
     * Central entry point for a WorkItemHandler to discover an endpoint based on its labels and call a Kogito REST service.
     *
     * @param workItem The given workitem for the current process
     * @param namespace Where the service is located
     * @param workItemServiceKey the key from the workitem parameter that holds the service label to search for. It will be used to build the final URL.
     *        For example: `my-service` can be the label key of the Service and also the path of the target endpoint
     * @param httpMethod the HTTP method to make the request
     * @return the result of the REST call
     */
    public Map<String, Object> discoverAndCall(WorkItem workItem, String namespace, String workItemServiceKey, String httpMethod) {
        final Map<String, Object> data = new HashMap<>(workItem.getParameters());
        final String service = (String) data.remove(workItemServiceKey);

        final List<Endpoint> endpoint = this.getEndpointDiscovery().findEndpoint(namespace, Collections.singletonMap(service, null));
        if (endpoint.isEmpty()) {
            throw new IllegalArgumentException("Kubernetes service with label " + service + " not found in the namespace " + namespace);
        }
        if (endpoint.size() > 1) {
            LOGGER.warn("Found more than one endpoint using labels {}:null. Returning the first one in the list. Try to be more specific in the query search.", service);
        }
        LOGGER.debug("Found endpoint for service {} in namespace {} with URL {}", service, namespace, endpoint.get(0).getUrl());

        INTERNAL_FIELDS.forEach(data::remove);

        final Request request = createRequest(String.format("%s/%s", endpoint.get(0).getUrl(), service), createRequestPayload(data), httpMethod);
        try (Response response = this.httpClient.newCall(request).execute()) {
            return createResultsFromResponse(response, request.url().toString());
        } catch (IOException e) {
            throw new EndpointCallerException(e);
        }
    }

    private RequestBody createRequestPayload(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        try {
            final String json = objectMapper.writeValueAsString(data);
            LOGGER.debug("Sending body {}", json);
            return RequestBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON), json);
        } catch (Exception e) {
            throw new EndpointCallerException("Unexpected error when producing request payload", e);
        }
    }

    private Request createRequest(String url, RequestBody body, String httpMethod) {
        Request.Builder builder;
        switch (httpMethod) {
            case HttpMethod.DELETE:
                builder = new Request.Builder().url(url).delete(body);
                break;
            case HttpMethod.POST:
                builder = new Request.Builder().url(url).post(body);
                break;
            case HttpMethod.PUT:
                builder = new Request.Builder().url(url).put(body);
                break;
            default:
                builder = new Request.Builder().url(url).get();
                break;
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> createResultsFromResponse(final Response response, final String url) throws IOException {
        String payload = "";
        if (response.body() != null) {
            payload = response.body().string();
        }
        LOGGER.debug("Response code {} and payload {}", response.code(), payload);
        if (!response.isSuccessful()) {
            throw new EndpointCallerException("Unsuccessful response from service (" + url + "). Response: " + response.message() + " (code " + response.code() + ")");
        }
        return objectMapper.readValue(payload, Map.class);
    }
}
