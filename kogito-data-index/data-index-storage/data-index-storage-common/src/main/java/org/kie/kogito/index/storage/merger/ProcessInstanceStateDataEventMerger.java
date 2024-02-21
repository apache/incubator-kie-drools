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

import java.util.Set;

import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateDataEvent;
import org.kie.kogito.event.process.ProcessInstanceStateEventBody;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.kie.kogito.index.DateTimeUtils.toZonedDateTime;

@ApplicationScoped
public class ProcessInstanceStateDataEventMerger extends ProcessInstanceEventMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessInstanceStateDataEventMerger.class);

    @Override
    public ProcessInstance merge(ProcessInstance pi, ProcessInstanceDataEvent<?> data) {
        ProcessInstanceStateDataEvent event = (ProcessInstanceStateDataEvent) data;
        pi = getOrNew(pi, data, event.getData().getEventDate());
        LOGGER.debug("Value before merging: {}", pi);
        pi.setId(event.getData().getProcessInstanceId());
        pi.setVersion(event.getData().getProcessVersion());
        pi.setProcessId(event.getData().getProcessId());
        pi.setProcessName(event.getData().getProcessName());
        pi.setRootProcessInstanceId(event.getData().getRootProcessInstanceId());
        pi.setRootProcessId(event.getData().getRootProcessId());
        pi.setParentProcessInstanceId(event.getData().getParentInstanceId());
        pi.setRoles(event.getData().getRoles());
        pi.setState(event.getData().getState());
        if (event.getData().getEventType() == null || event.getData().getEventType() == ProcessInstanceStateEventBody.EVENT_TYPE_STARTED) {
            pi.setStart(toZonedDateTime(event.getData().getEventDate()));
            pi.setCreatedBy(event.getData().getEventUser());
        } else if (event.getData().getEventType() == ProcessInstanceStateEventBody.EVENT_TYPE_STARTED) {
            pi.setEnd(toZonedDateTime(event.getData().getEventDate()));
        }
        pi.setBusinessKey(event.getData().getBusinessKey());
        pi.setAddons(isNullOrEmpty(event.getKogitoAddons()) ? null : Set.of(event.getKogitoAddons().split(",")));
        pi.setEndpoint(event.getSource() == null ? null : event.getSource().toString());
        pi.setLastUpdate(toZonedDateTime(event.getTime()));
        pi.setDefinition(definitions(event));
        pi.setUpdatedBy(event.getData().getEventUser());
        LOGGER.debug("Value after merging: {}", pi);
        return pi;
    }

    private ProcessDefinition definitions(ProcessInstanceStateDataEvent event) {
        LOGGER.debug("Value before merging: {}", event);
        ProcessDefinition pd = new ProcessDefinition();
        pd.setId(event.getData().getProcessId());
        pd.setName(event.getData().getProcessName());
        pd.setVersion(event.getData().getProcessVersion());
        pd.setAddons(isNullOrEmpty(event.getKogitoAddons()) ? null : Set.of(event.getKogitoAddons().split(",")));
        pd.setRoles(event.getData().getRoles());
        pd.setType(event.getKogitoProcessType());
        pd.setEndpoint(event.getSource() == null ? null : event.getSource().toString());
        LOGGER.debug("Value after merging: {}", pd);
        return pd;

    }

}
