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
package org.kie.kogito.serverless.workflow.openapi;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.Map;

import org.kie.kogito.event.cloudevents.extension.ProcessMeta;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.process.workitem.WorkItemExecutionException;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;

import io.quarkus.restclient.runtime.RestClientBuilderFactory;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

public abstract class OpenApiWorkItemHandler<T> extends WorkflowWorkItemHandler {

    @Override
    protected Object internalExecute(KogitoWorkItem workItem, Map<String, Object> parameters) {
        Class<T> clazz = getRestClass();
        T ref = RestClientBuilderFactory.build(clazz).register(new ClientRequestFilter() {
            @Override
            public void filter(ClientRequestContext requestContext) throws IOException {
                ProcessMeta.fromKogitoWorkItem(workItem).asMap().forEach((k, v) -> requestContext.getHeaders().put(k, Collections.singletonList(v)));
            }
        }).build(clazz);
        try {
            return internalExecute(ref, parameters);
        } catch (WebApplicationException ex) {
            throw new WorkItemExecutionException(Integer.toString(ex.getResponse().getStatus()), ex.getMessage());
        }
    }

    protected abstract Object internalExecute(T openAPIRef, Map<String, Object> parameters);

    protected Class<T> getRestClass() {
        // this does not work in quarkus dev mode, overriding through generation
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
