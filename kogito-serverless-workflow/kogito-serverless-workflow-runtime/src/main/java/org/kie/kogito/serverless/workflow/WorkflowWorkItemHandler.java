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
package org.kie.kogito.serverless.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.kie.kogito.internal.process.workitem.KogitoWorkItem;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemManager;
import org.kie.kogito.internal.process.workitem.WorkItemTransition;
import org.kie.kogito.jackson.utils.JsonObjectUtils;
import org.kie.kogito.process.workitems.impl.DefaultKogitoWorkItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WorkflowWorkItemHandler extends DefaultKogitoWorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowWorkItemHandler.class);

    @Override
    public Optional<WorkItemTransition> activateWorkItemHandler(KogitoWorkItemManager manager, KogitoWorkItemHandler handler, KogitoWorkItem workItem, WorkItemTransition transition) {
        Map<String, Object> parameters = new HashMap<>(workItem.getParameters());
        parameters.remove(SWFConstants.MODEL_WORKFLOW_VAR);
        logger.debug("Workflow workitem {} will be invoked with parameters {}", workItem.getName(), parameters);

        Map<String, Object> params = Collections.singletonMap("Result", JsonObjectUtils.fromValue(internalExecute(workItem, parameters)));
        return Optional.of(this.workItemLifeCycle.newTransition("complete", workItem.getPhaseStatus(), params));
    }

    protected abstract Object internalExecute(KogitoWorkItem workItem, Map<String, Object> parameters);

    protected static <V> V buildBody(Map<String, Object> params, Class<V> clazz) {
        for (Object obj : params.values()) {
            if (obj != null && clazz.isAssignableFrom(obj.getClass())) {
                logger.trace("Invoking workitemhandler with value {}", obj);
                return clazz.cast(obj);
            }
        }
        V value = JsonObjectUtils.convertValue(params, clazz);
        logger.trace("Invoking workitemhandler with value {}", value);
        return value;
    }

}
