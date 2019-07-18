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
import java.util.Collections;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.kogito.cloud.kubernetes.client.KogitoKubeClient;
import org.kie.kogito.cloud.workitems.service.discovery.ServiceDiscovery;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscoveredServiceWorkItemHandlerTest {
    
    
    private OkHttpClient httpClient;

    @Before
    public void setup() {
        httpClient = mock(OkHttpClient.class);
    }
    
    @Test
    public void testGivenEndpoint() throws IOException {
        DiscoveredServiceWorkItemHandler handler = new TestDiscoveredServiceWorkItemHandler("test", "http://testhost:9000"); 
        
        WorkItem workItem = Mockito.mock(WorkItem.class);
        when(workItem.getParameters()).thenReturn(Collections.singletonMap("service", "test"));
        
        Call call = mock(Call.class);
        ResponseBody body = new ResponseBody() {
            private String content = "{\"test\" : \"fake\"}";
            @Override
            public BufferedSource source() {
                Buffer b = new Buffer();
                b.write(content.getBytes());
                return b;
            }
            
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/json");
            }
            
            @Override
            public long contentLength() {
                return content.length();
            }
        };
        Response response = new Response.Builder().body(body).protocol(Protocol.HTTP_1_1).message("test").request(new Request.Builder().url("http://localhost:9000").build()).code(200).build();
                
     
        when(call.execute()).thenReturn(response);
        when(httpClient.newCall(any())).thenReturn(call);
        
        Map<String, Object> results = handler.discoverAndCall(workItem, "", "service", HttpMethods.POST);
        
        assertThat(results).isNotNull().containsKey("test").containsValue("fake");
    }
    
    private class TestDiscoveredServiceWorkItemHandler extends DiscoveredServiceWorkItemHandler {

        public TestDiscoveredServiceWorkItemHandler(String service, String endpoint) {
            super();
            this.serviceEndpoints.put(service, new ServiceInfo(endpoint, null));
        }
        
        @Override
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        }

        @Override
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }

        @Override
        protected OkHttpClient buildHttpClient() {
            return httpClient;
        }
        
        @Override
        protected ServiceDiscovery buildServiceDiscovery(KogitoKubeClient kubeClient) {
            return mock(ServiceDiscovery.class);
        }
    }
}
