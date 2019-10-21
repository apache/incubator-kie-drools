/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.cloud.workitems.httpcalls;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.kogito.cloud.workitems.DiscoveredServiceWorkItemHandler;
import org.kie.kogito.cloud.workitems.HttpMethods;

@Path("/httpcall")
public class HttpCallsEndpoint {

    private static final String SERVICE_KEY = "ServiceToCall";

    private String namespace = System.getenv("NAMESPACE");

    DiscoveredServiceWorkItemHandlerImpl workItemHandler = new DiscoveredServiceWorkItemHandlerImpl();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("")
    public Map<String, Object> callServiceForWorkItem(HttpCall httpCallData) {
        Map<String, Object> params = httpCallData.getData();
        params.put(SERVICE_KEY, httpCallData.getService());

        return workItemHandler.makeCall(namespace, SERVICE_KEY, HttpMethods.valueOf(httpCallData.getHttpMethod().toUpperCase()), params);
    }

    static class DiscoveredServiceWorkItemHandlerImpl extends DiscoveredServiceWorkItemHandler {

        @Override
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {}

        @Override
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {}

        public Map<String, Object> makeCall(String namespace, String serviceName, HttpMethods method, Map<String, Object> params) {
            WorkItem item = new WorkItemImpl();
            item.getParameters().putAll(params);

            return discoverAndCall(item, namespace, serviceName, method);
        }
    }
}
