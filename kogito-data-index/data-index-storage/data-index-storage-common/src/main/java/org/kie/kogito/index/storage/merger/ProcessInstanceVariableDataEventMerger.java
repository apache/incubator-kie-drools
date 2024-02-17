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
package org.kie.kogito.index.storage.merger;

import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableDataEvent;
import org.kie.kogito.event.process.ProcessInstanceVariableEventBody;
import org.kie.kogito.index.json.JsonUtils;
import org.kie.kogito.index.model.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessInstanceVariableDataEventMerger extends ProcessInstanceEventMerger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessInstanceVariableDataEventMerger.class);

    @SuppressWarnings("unchecked")
    @Override
    public ProcessInstance merge(ProcessInstance pi, ProcessInstanceDataEvent<?> data) {
        pi = getOrNew(pi, data);
        ProcessInstanceVariableDataEvent event = (ProcessInstanceVariableDataEvent) data;
        ProcessInstanceVariableEventBody body = event.getData();
        pi.setVariables(JsonUtils.mergeVariable(body.getVariableName(), body.getVariableValue(), pi.getVariables()));
        return pi;
    }
}
