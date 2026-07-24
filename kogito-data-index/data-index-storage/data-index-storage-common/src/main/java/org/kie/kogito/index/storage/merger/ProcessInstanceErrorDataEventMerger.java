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
import org.kie.kogito.event.process.ProcessInstanceErrorDataEvent;
import org.kie.kogito.event.process.ProcessInstanceErrorEventBody;
import org.kie.kogito.index.CommonUtils;
import org.kie.kogito.index.model.CancelType;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.ProcessInstanceError;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessInstanceErrorDataEventMerger extends ProcessInstanceEventMerger {

    @Override
    public ProcessInstance merge(ProcessInstance pi, ProcessInstanceDataEvent<?> dataEvent) {
        ProcessInstanceErrorDataEvent event = (ProcessInstanceErrorDataEvent) dataEvent;
        ProcessInstanceErrorEventBody data = event.getData();
        pi = getOrNew(pi, dataEvent, data.getEventDate());
        ProcessInstanceError error = new ProcessInstanceError();
        error.setMessage(data.getErrorMessage());
        error.setNodeDefinitionId(data.getNodeDefinitionId());
        error.setNodeInstanceId(data.getNodeInstanceId());
        pi.setError(error);
        pi.setState(CommonUtils.ERROR_STATE);
        if (pi.getNodes() != null) {
            pi.getNodes().stream()
                    .filter(n -> n.getId().equals(error.getNodeInstanceId()))
                    .findAny()
                    .ifPresent(n -> {
                        n.setErrorMessage(data.getErrorMessage());
                        n.setCancelType(CancelType.ERROR);
                    });
        }
        return pi;
    }

}
