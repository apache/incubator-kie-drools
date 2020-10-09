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
package org.kogito.workitem.rest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kogito.workitem.rest.jsonpath.functions.JSonPathResultHandler;
import org.kogito.workitem.rest.jsonpath.functions.JsonPathResolver;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RestTaskHandlerTest {

    @Test
    public void testReplaceTemplateTrivial() {
        Map<String, Object> parameters = Collections.emptyMap();
        String endPoint = "http://pepe:password@www.google.com/results/id/?user=pepe#at_point";
        assertEquals(
                     "http://pepe:password@www.google.com/results/id/?user=pepe#at_point",
                     RestWorkItemHandler.resolvePathParams(endPoint, parameters, e -> e));
    }

    @Test
    public void testReplaceTemplate() {
        Map<String, Object> parameters = new HashMap<>();
        // no use singletonMap here since the map must be mutable
        parameters.put("id", "pepe");
        String endPoint = "http://pepe:password@www.google.com/results/{id}/?user=pepe#at_point";
        assertEquals(
                     "http://pepe:password@www.google.com/results/pepe/?user=pepe#at_point",
                     RestWorkItemHandler.resolvePathParams(endPoint, parameters, e -> e));
    }

    @Test
    public void testReplaceTemplateMultiple() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 26);
        parameters.put("name", "pepe");
        String endPoint = "http://pepe:password@www.google.com/results/{id}/names/{name}/?user=pepe#at_point";
        assertEquals(
                     "http://pepe:password@www.google.com/results/26/names/pepe/?user=pepe#at_point",
                     RestWorkItemHandler.resolvePathParams(endPoint, parameters, e -> e));
    }

    @Test
    public void testReplaceTemplateMissing() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 26);
        String endPoint = "http://pepe:password@www.google.com/results/{id}/names/{name}/?user=pepe#at_point";
        assertTrue(
                   assertThrows(
                                IllegalArgumentException.class,
                                () -> RestWorkItemHandler.resolvePathParams(endPoint, parameters, e -> e))
                                    .getMessage()
                                    .contains("name"));
    }

    @Test
    public void testReplaceTemplateBadEnpoint() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 26);
        parameters.put("name", "pepe");
        String endPoint = "http://pepe:password@www.google.com/results/{id}/names/{name/?user=pepe#at_point";
        assertTrue(
                   assertThrows(
                                IllegalArgumentException.class,
                                () -> RestWorkItemHandler.resolvePathParams(endPoint, parameters, e -> e))
                                    .getMessage()
                                    .contains("}"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetRestTaskHandler() {
        WebClient webClient = mock(WebClient.class);
        ObjectMapper mapper = new ObjectMapper();
        HttpRequest<Buffer> request = mock(HttpRequest.class);
        HttpResponse<Buffer> response = mock(HttpResponse.class);
        AsyncResult<HttpResponse<Buffer>> event = mock(AsyncResult.class);
        when(event.result()).thenReturn(response);

        when(webClient.request(HttpMethod.GET, 8080, "localhost", "/results/26/names/pepe"))
            .thenReturn(request);
        doAnswer((Answer<Void>) invocation -> {
            Handler<AsyncResult<HttpResponse<Buffer>>> handler =
                    (Handler<AsyncResult<HttpResponse<Buffer>>>) invocation.getArgument(0);
            handler.handle(event);
            return null;
        }).when(request).send(any(Handler.class));
        when(response.bodyAsJsonObject()).thenReturn(JsonObject.mapFrom(Collections.singletonMap("num", 1)));

        Map<String, Object> parameters =
                new HashMap<>();
        parameters.put("id", new JsonPathResolver("$.id"));
        parameters.put("name", new JsonPathResolver("$.name"));
        parameters.put(RestWorkItemHandler.ENDPOINT, "http://localhost:8080/results/{id}/names/{name}");
        parameters.put(RestWorkItemHandler.METHOD, "GET");
        parameters.put(RestWorkItemHandler.RESULT_HANDLER, new JSonPathResultHandler());
        parameters.put(RestWorkItemHandler.PARAMETER, mapper.createObjectNode().put("id", 26).put("name", "pepe"));

        WorkItem workItem = mock(WorkItem.class);
        when(workItem.getId()).thenReturn("2");
        when(workItem.getParameters()).thenReturn(parameters);
        WorkItemManager manager = mock(WorkItemManager.class);

        ArgumentCaptor<Map<String, Object>> argCaptor = ArgumentCaptor.forClass(Map.class);

        RestWorkItemHandler handler = new RestWorkItemHandler(
            webClient);
        handler.executeWorkItem(workItem, manager);
        verify(manager).completeWorkItem(anyString(), argCaptor.capture());
        Map<String, Object> results = argCaptor.getValue();

        assertEquals(1, results.size());
        assertTrue(results.containsKey(RestWorkItemHandler.RESULT));
        Object result = results.get(RestWorkItemHandler.RESULT);
        assertTrue(result instanceof ObjectNode);
        assertEquals(1, ((ObjectNode) result).get("num").asInt());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetRestTaskHandlerFail() {
        WebClient webClient = mock(WebClient.class);
        ObjectMapper mapper = new ObjectMapper();
        HttpRequest<Buffer> request = mock(HttpRequest.class);
        AsyncResult<HttpResponse<Buffer>> event = mock(AsyncResult.class);
        when(event.failed()).thenReturn(true);
        when(event.cause()).thenReturn(new IllegalStateException("example"));
        when(webClient.request(HttpMethod.GET, 8080, "localhost", "/results/26/names/pepe"))
                .thenReturn(request);
        doAnswer((Answer<Void>) invocation -> {
            Handler<AsyncResult<HttpResponse<Buffer>>> handler =
                    (Handler<AsyncResult<HttpResponse<Buffer>>>) invocation.getArgument(0);
            handler.handle(event);
            return null;
        }).when(request).send(any(Handler.class));
        Map<String, Object> parameters =
                new HashMap<>();
        parameters.put("id", new JsonPathResolver("$.id"));
        parameters.put("name", new JsonPathResolver("$.name"));
        parameters.put(RestWorkItemHandler.ENDPOINT, "http://localhost:8080/results/{id}/names/{name}");
        parameters.put(RestWorkItemHandler.METHOD, "GET");
        parameters.put(RestWorkItemHandler.RESULT_HANDLER, new JSonPathResultHandler());
        parameters.put(RestWorkItemHandler.PARAMETER, mapper.createObjectNode().put("id", 26).put("name", "pepe"));
        WorkItem workItem = mock(WorkItem.class);
        when(workItem.getId()).thenReturn("2");
        when(workItem.getParameters()).thenReturn(parameters);
        WorkItemManager manager = mock(WorkItemManager.class);
        RestWorkItemHandler handler = new RestWorkItemHandler(
                webClient);
        handler.executeWorkItem(workItem, manager);
        verify(manager).abortWorkItem(anyString());
    }
}
