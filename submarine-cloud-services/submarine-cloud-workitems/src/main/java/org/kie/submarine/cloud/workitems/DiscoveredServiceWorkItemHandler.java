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

package org.kie.submarine.cloud.workitems;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;


public abstract class DiscoveredServiceWorkItemHandler implements WorkItemHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveredServiceWorkItemHandler.class);
    
    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected static final List<String> INTERNAL_FIELDS = Arrays.asList("TaskName", "ActorId", "GroupId", "Priority", "Comment", "Skippable", "Content", "Model", "Namespace");
    
    protected static final String KNATIVE_SERVICE_SERVICE_URL = "apis/serving.knative.dev/v1alpha1/namespaces/@ns@/services?labelSelector=";
    protected static final String KNATIVE_ISTIO_GATEWAY_URL = "api/v1/namespaces/istio-system/services/istio-ingressgateway";
    protected static final String SERVICE_URL = "api/v1/namespaces/@ns@/services?labelSelector=";
    
    protected String istionGatewayClusterIp;
    
    protected Map<String, ServiceInfo> serviceEndpoints = new ConcurrentHashMap<>();
    
    private volatile KubernetesClient client;
    private OkHttpClient http;
    private ObjectMapper mapper = new ObjectMapper();

    
    public DiscoveredServiceWorkItemHandler() {
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
        
        http = buildHttpClient();
    }
    
    protected OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }
    
    /**
     * Looks up service's endpoint (cluster ip + port) using label selector - meaning returns services that have given label.
     * Services are looked up only in given namespace. 
     * @param service label assign to a service that should be used as selector
     * @return valid endpoint (in URL form) if found or runtime exception in case of no services found
     */
    protected ServiceInfo findEndpoint(String namespace, String service) {
        
        LOGGER.debug("Looking up for service {} in namespace {}", service, namespace);
        DefaultKubernetesClient kubeClient = (DefaultKubernetesClient) getKubeClient();
        String host = null;
        Integer port = null;
        Map<String, String> headers = null;
        if (istionGatewayClusterIp != null) {
            LOGGER.debug("Knative environment found, looking up for ingresgateway {} and host for serving service", istionGatewayClusterIp);
            host = istionGatewayClusterIp;
            port = 80;
            headers = new HashMap<>();
            
            Request request = new Request.Builder().url(kubeClient.getMasterUrl() + KNATIVE_SERVICE_SERVICE_URL.replaceFirst("@ns@", namespace) + service)
                                                   .build();
            LOGGER.debug("About to call a search for services labeled with {}, complete url is {}", service, request.url());
            try (Response response = kubeClient.getHttpClient().newCall(request).execute()) {
                String out = response.body().string();
                if (response.isSuccessful()) {
                    Map<?, ?> data = mapper.readValue(out, Map.class);
                    headers.put("HOST", ((Map<?, ?>)((Map<?, ?>)((List<?>)data.get("items")).get(0)).get("status")).get("domain").toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            LOGGER.debug("Headers to be used for requests {}", headers);
            
        } else {
            LOGGER.debug("Looking up for service in regular kube/openshift environment");
            
            Request request = new Request.Builder()
                                                   .url(kubeClient.getMasterUrl() + SERVICE_URL.replaceFirst("@ns@", namespace) + service)
                                                   .build();

            try (Response response = kubeClient.getHttpClient().newCall(request).execute()) {
                String out = response.body().string();

                if (response.isSuccessful()) {
                    Map<?, ?> data = mapper.readValue(out, Map.class);
                    
                    Map<?, ?> spec = ((Map<?, ?>) ((Map<?, ?>) ((List<?>) data.get("items")).get(0)).get("spec"));
                    host = spec.get("clusterIP").toString();
                    port = (Integer) ((Map<?, ?>) ((List<?>)spec.get("ports")).get(0)).get("port");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }            
        }
        if (host == null) {
          throw new RuntimeException("No endpoint found for service " + service);
        }
        StringBuilder location = new StringBuilder("http://")
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(service);
        LOGGER.debug("Host {} and port {} found for service {} in namespace {}", host, port, service, namespace);
       return new ServiceInfo(location.toString(), headers);
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

        try (Response response = http.newCall(request).execute()) {
            
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
    
    protected KubernetesClient getKubeClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = new DefaultKubernetesClient();
                    LOGGER.debug("Kube Client created, looking up istio ingressgateway...");
                    Request request = new Request.Builder().url(client.getMasterUrl() + KNATIVE_ISTIO_GATEWAY_URL)
                                                           .build();

                    try (Response response = ((DefaultKubernetesClient) client).getHttpClient().newCall(request).execute()) {
                        String out = response.body().string();
                        LOGGER.debug("Request {} completed with code {}, and response {}", request.url().toString(), response.code(), out);
                        if (response.isSuccessful()) {
                            Map<?, ?> data = mapper.readValue(out, Map.class);
                            this.istionGatewayClusterIp = ((Map<?, ?>) data.get("spec")).get("clusterIP").toString();
                        }
                        LOGGER.debug("Istio geteway is " + istionGatewayClusterIp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return client;

    }
}
