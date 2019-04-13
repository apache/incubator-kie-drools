package org.kie.submarine.cloud.workitems;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public abstract class DiscoveredServiceWorkItemHandler implements WorkItemHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveredServiceWorkItemHandler.class);
    
    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected static final List<String> INTERNAL_FIELDS = Arrays.asList("TaskName", "ActorId", "GroupId", "Priority", "Comment", "Skippable", "Content", "Model", "Namespace");
    
    protected Map<String, String> serviceEndpoints = new ConcurrentHashMap<>();
    
    private volatile KubernetesClient client;
    private OkHttpClient http = new OkHttpClient();
    private ObjectMapper mapper = new ObjectMapper();

    
    public DiscoveredServiceWorkItemHandler() {
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
    }
    
    /**
     * Looks up service's endpoint (cluster ip + port) using label selector - meaning returns services that have given label.
     * Services are looked up only in given namespace. 
     * @param service label assign to a service that should be used as selector
     * @return valid endpoint (in URL form) if found or runtime exception in case of no services found
     */
    protected String findEndpoint(String namespace, String service) {

        ServiceList found = getKubeClient().services().inNamespace(namespace).withLabel(service).list();
        if (found.getItems().isEmpty()) {
            throw new RuntimeException("No endpoint found for service " + service);
        }
        Service foundService = found.getItems().get(0);

        ServiceSpec spec = foundService.getSpec();
        
        StringBuilder location = new StringBuilder("http://")
                                    .append(spec.getClusterIP())
                                    .append(":")
                                    .append(spec.getPorts().get(0).getPort())
                                    .append("/")
                                    .append(service);

        return location.toString();
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
        String endpoint = serviceEndpoints.computeIfAbsent(service, (s) -> findEndpoint(namespace, s));
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
    
    protected Request producePostRequest(String endpoint, RequestBody body) {
        Request request = new Request.Builder().url(endpoint)
                .post(body)
                .build();
        
        return request;
    }
    
    protected Request produceGetRequest(String endpoint) {
        Request request = new Request.Builder().url(endpoint)
                .get()
                .build();
        
        return request;
    }
    
    protected Request producePutRequest(String endpoint, RequestBody body) {
        Request request = new Request.Builder().url(endpoint)
                .put(body)
                .build();
        
        return request;
    }
    
    protected Request produceDeleteRequest(String endpoint, RequestBody body) {
        Request request = new Request.Builder().url(endpoint)
                .delete(body)
                .build();
        
        return request;
    }
    
    protected KubernetesClient getKubeClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = new DefaultKubernetesClient();
                }
            }
        }
        return client;

    }
}
