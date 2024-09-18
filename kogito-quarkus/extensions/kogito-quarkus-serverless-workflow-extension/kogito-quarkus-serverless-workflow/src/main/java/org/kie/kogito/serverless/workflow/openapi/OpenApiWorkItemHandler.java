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
import java.util.Optional;

import org.jbpm.process.instance.KogitoProcessContextImpl;
import org.jbpm.util.ContextFactory;
import org.jbpm.workflow.core.WorkflowProcess;
import org.kie.kogito.event.cloudevents.extension.ProcessMeta;
import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.internal.process.workitem.WorkItemExecutionException;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.kie.kogito.serverless.workflow.SWFConstants;
import org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler;

import io.quarkus.restclient.runtime.RestClientBuilderFactory;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

public abstract class OpenApiWorkItemHandler<T> extends WorkflowWorkItemHandler {

    @Override
    protected Object internalExecute(KogitoWorkItem workItem, Map<String, Object> parameters) {
        Class<T> clazz = getRestClass();
        T ref = RestClientBuilderFactory.build(clazz, calculatedConfigKey(workItem, parameters)).register(new ClientRequestFilter() {
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

    private Optional<String> calculatedConfigKey(KogitoWorkItem workItem, Map<String, Object> parameters) {
        String configKeyExpr = (String) workItem.getNodeInstance().getNode().getMetaData().get("configKey");
        if (configKeyExpr == null) {
            return Optional.empty();
        }
        KogitoProcessContextImpl context = ContextFactory.fromItem(workItem);
        String result = ExpressionHandlerFactory.get(((WorkflowProcess) workItem.getProcessInstance().getProcess()).getExpressionLanguage(), configKeyExpr)
                .eval(context.getVariable(SWFConstants.DEFAULT_WORKFLOW_VAR), String.class, context);
        if (result == null || result.isBlank()) {
            throw new IllegalArgumentException("Expression " + configKeyExpr + " returns null or empty value");
        }
        return Optional.of(result);
    }

    protected abstract Object internalExecute(T openAPIRef, Map<String, Object> parameters);

    protected Class<T> getRestClass() {
        // this does not work in quarkus dev mode, overriding through generation
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
