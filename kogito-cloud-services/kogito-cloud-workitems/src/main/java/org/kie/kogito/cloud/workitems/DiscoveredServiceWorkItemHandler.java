/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.cloud.workitems;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.kogito.cloud.kubernetes.client.DefaultKogitoKubeClient;
import org.kie.kogito.cloud.kubernetes.client.KogitoKubeClient;
import org.kie.kogito.cloud.workitems.service.discovery.ServiceDiscovery;
import org.kie.kogito.cloud.workitems.service.discovery.ServiceDiscoveryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DiscoveredServiceWorkItemHandler implements WorkItemHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveredServiceWorkItemHandler.class);

    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected static final List<String> INTERNAL_FIELDS = Arrays.asList("TaskName", "ActorId", "GroupId", "Priority", "Comment", "Skippable", "Content", "Model", "Namespace");

    private Map<String, ServiceInfo> serviceEndpoints;

    private OkHttpClient http;
    private ObjectMapper mapper;
    private ServiceDiscovery serviceDiscovery;
    private KogitoKubeClient kubeClient;

    public DiscoveredServiceWorkItemHandler() {
        this(null);
    }

    protected DiscoveredServiceWorkItemHandler(final KogitoKubeClient kubeClient) {
        LOGGER.debug("New instance of discovered service work item with kubeclient: {}", kubeClient);
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
        /*
         *  Delaying buildServiceDiscovery and buildHttpClient to avoid problems with okhttp3 dependency during GraalVM native builds. 
         *  OKHttp3 references SSLContextFactory in static fields, which lead to errors. See: https://quarkus.io/guides/writing-native-applications-tips#delaying-class-initialization
         *  We're trying to not use static fields that depends on unknown and uncontrolled dependencies to avoid errors like this.
         *  That's why we're just holding the kube reference in the constructor, thus lazy building the HTTP objects. 
         */ 
        this.kubeClient = kubeClient;
        this.serviceEndpoints = new ConcurrentHashMap<>();
    }
    
    /**
     * Returns the {@link ServiceDiscovery} reference that will be used during the endpoint discovery.
     * @return
     */
    protected ServiceDiscovery buildServiceDiscovery() {
        if (kubeClient == null) {
            LOGGER.debug("Kubernetes client configuration is null, using default values");
            kubeClient = new DefaultKogitoKubeClient();
        }
        if (serviceDiscovery == null) {
            LOGGER.debug("Creating and caching a new reference of ServiceDiscoveryFactory");
            serviceDiscovery = new ServiceDiscoveryFactory(kubeClient).build();
        }
        return serviceDiscovery;
    }
    
    /**
     * Build a {@link ServiceDiscovery} reference with the custom {@link KogitoKubeClient}.
     * @see #buildServiceDiscovery()
     * @param kubeClient
     * @return
     */
    protected ServiceDiscovery buildServiceDiscovery(KogitoKubeClient kubeClient) {
        this.kubeClient = kubeClient;
        return this.buildServiceDiscovery();
    }

    protected OkHttpClient buildHttpClient() {
        if (http == null) {
            LOGGER.debug("Creating and caching a new reference of OkHttpClient");
            http = new OkHttpClient.Builder()
                                             .connectTimeout(60, TimeUnit.SECONDS)
                                             .writeTimeout(60, TimeUnit.SECONDS)
                                             .readTimeout(60, TimeUnit.SECONDS)
                                             .build();
        }
        return http;
    }
    
    /**
     * Removes a service from the registry
     *  
     * @param serviceName
     * @return true if removed successfully
     */
    protected boolean removeService(String serviceName) {
        return this.serviceEndpoints.remove(serviceName) != null;
    }

    /**
     * Add a new service into the internal registry
     * 
     * @param serviceName
     * @param service
     */
    protected void addServices(String serviceName, ServiceInfo service) {
        if(service != null) {
            LOGGER.debug("Adding a new service '{}' to the registry: {}", serviceName, service);
            this.serviceEndpoints.put(serviceName, service);
        }
    }
    
    /**
     * Retrieves a immutable list of services added to this reference. 
     * 
     * @return
     */
    protected Map<String, ServiceInfo> getServices() {
        return Collections.unmodifiableMap(this.serviceEndpoints);
    }
    
    /**
     * Looks up service's endpoint (cluster ip + port) using label selector - meaning returns services that have given label.
     * Services are looked up only in given namespace. 
     * @param service label assign to a service that should be used as selector
     * @return valid endpoint (in URL form) if found or runtime exception in case of no services found
     */
    protected ServiceInfo findEndpoint(String namespace, String service) {
        LOGGER.debug("Looking for services. Services discovered so far {}", this.serviceEndpoints);
        return this.buildServiceDiscovery().findEndpoint(namespace, service).orElseThrow(() -> new RuntimeException("No endpoint found for service " + service));
    }

    /**
     * Discover valid service to be invoked in given namespace and serviceName. Where serviceName is 
     * considered to be a label on the service .It uses service discovery
     * base on label selectors to find the matching service endpoint (cluster ip and port)
     * @param workItem work item that this handler is working on
     * @param namespace namespace to look up services in
     * @param serviceName name of the service to look up by - label
     * @param method http method to be used when calling a service (supports GET, POST, PUT, DELETE)
     * @return returns map of data that was returned from the service call
     */
    protected Map<String, Object> discoverAndCall(WorkItem workItem, String namespace, String serviceName, HttpMethods method) {
        Map<String, Object> data = new HashMap<>(workItem.getParameters());
        String service = (String) data.remove(serviceName);

        // remove all internal fields before sending
        INTERNAL_FIELDS.forEach(field -> data.remove(field));

        // discover service endpoint
        ServiceInfo endpoint = serviceEndpoints.computeIfAbsent(service, (s) -> findEndpoint(namespace, s));
        LOGGER.debug("Found endpoint for service {} with location {}", service, endpoint);

        RequestBody body = produceRequestPayload(data);
        Request request = null;

        switch (method) {
            case POST:
                request = producePostRequest(endpoint, body);
                break;
            case GET:
                request = produceGetRequest(endpoint);
                break;
            case PUT:
                request = producePutRequest(endpoint, body);
                break;
            case DELETE:
                request = produceDeleteRequest(endpoint, body);
                break;
            default:
                break;
        }

        try (Response response = this.buildHttpClient().newCall(request).execute()) {

            Map<String, Object> results = produceResultsFromResponse(response);

            return results;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected RequestBody produceRequestPayload(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        try {
            String json = mapper.writeValueAsString(data);
            LOGGER.debug("Sending body {}", json);
            RequestBody body = RequestBody.create(JSON, json);

            return body;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error when producing request payload", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> produceResultsFromResponse(Response response) throws IOException {
        String payload = response.body().string();
        LOGGER.debug("Resonse code {} and payload {}", response.code(), payload);

        if (!response.isSuccessful()) {
            throw new RuntimeException("Unsuccessful response from service " + response.message() + " (code " + response.code() + ")");
        }

        Map<String, Object> results = mapper.readValue(payload, Map.class);

        return results;
    }

    protected Request producePostRequest(ServiceInfo endpoint, RequestBody body) {
        Builder builder = new Request.Builder().url(endpoint.getUrl())
                                               .post(body);
        applyHeaders(endpoint, builder);

        return builder.build();
    }

    protected Request produceGetRequest(ServiceInfo endpoint) {
        Builder builder = new Request.Builder().url(endpoint.getUrl())
                                               .get();
        applyHeaders(endpoint, builder);

        return builder.build();
    }

    protected Request producePutRequest(ServiceInfo endpoint, RequestBody body) {
        Builder builder = new Request.Builder().url(endpoint.getUrl())
                                               .put(body);
        applyHeaders(endpoint, builder);

        return builder.build();
    }

    protected Request produceDeleteRequest(ServiceInfo endpoint, RequestBody body) {
        Builder builder = new Request.Builder().url(endpoint.getUrl())
                                               .delete(body);
        applyHeaders(endpoint, builder);

        return builder.build();
    }

    protected void applyHeaders(ServiceInfo endpoint, Builder builder) {

        if (endpoint.getHeaders() != null) {
            for (Entry<String, String> header : endpoint.getHeaders().entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }
    }

}
