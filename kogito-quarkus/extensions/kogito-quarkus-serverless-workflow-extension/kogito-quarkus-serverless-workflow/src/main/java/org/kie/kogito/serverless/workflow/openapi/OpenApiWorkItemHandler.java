/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.openapi;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.process.meta.ProcessMeta;
import org.kie.kogito.serverless.workflow.SWFConstants;

import io.quarkus.restclient.runtime.RestClientBuilderFactory;

public abstract class OpenApiWorkItemHandler<T> implements KogitoWorkItemHandler {

    @Override
    public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        Class<T> clazz = getRestClass();
        T ref = RestClientBuilderFactory.build(clazz).register(new ClientRequestFilter() {
            @Override
            public void filter(ClientRequestContext requestContext) throws IOException {
                ProcessMeta.fromKogitoWorkItem(workItem).asMap().forEach((k, v) -> requestContext.getHeaders().put(k, Collections.singletonList(v)));
            }
        }).build(clazz);
        Map<String, Object> parameters = new HashMap<>(workItem.getParameters());
        parameters.remove(SWFConstants.DEFAULT_WORKFLOW_VAR);
        manager.completeWorkItem(workItem.getStringId(), Collections.singletonMap("Result",
                JsonObjectUtils.fromValue(internalExecute(ref, parameters))));
    }

    @Override
    public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
    }

    protected final <V> V buildBody(Map<String, Object> params, Class<V> clazz) {
        for (Object obj : params.values()) {
            if (obj != null && clazz.isAssignableFrom(obj.getClass())) {
                return clazz.cast(obj);
            }
        }
        return ObjectMapperFactory.get().convertValue(params, clazz);
    }

    protected abstract Object internalExecute(T openAPIRef, Map<String, Object> parameters);

    protected Class<T> getRestClass() {
        // this does not work in quarkus dev mode, overriding through generation
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
