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
package org.kie.kogito.serverless.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

public abstract class WorkflowWorkItemHandler implements KogitoWorkItemHandler {

    @Override
    public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        Map<String, Object> parameters = new HashMap<>(workItem.getParameters());
        parameters.remove(SWFConstants.MODEL_WORKFLOW_VAR);
        manager.completeWorkItem(workItem.getStringId(), Collections.singletonMap("Result",
                JsonObjectUtils.fromValue(internalExecute(workItem, parameters))));
    }

    protected abstract Object internalExecute(KogitoWorkItem workItem, Map<String, Object> parameters);

    protected final <V> V buildBody(Map<String, Object> params, Class<V> clazz) {
        for (Object obj : params.values()) {
            if (obj != null && clazz.isAssignableFrom(obj.getClass())) {
                return clazz.cast(obj);
            }
        }
        return ObjectMapperFactory.get().convertValue(params, clazz);
    }

    @Override
    public void abortWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
        // abort does nothing
    }

}
