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

import java.util.ArrayList;
import java.util.Date;

import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.index.DateTimeUtils;
import org.kie.kogito.index.model.ProcessInstance;

public abstract class ProcessInstanceEventMerger implements Merger<ProcessInstanceDataEvent<?>, ProcessInstance> {

    protected ProcessInstance getOrNew(ProcessInstance pi, ProcessInstanceDataEvent<?> event, Date date) {
        if (pi == null) {
            pi = new ProcessInstance();
            pi.setId(event.getKogitoProcessInstanceId());
            pi.setProcessId(event.getKogitoProcessId());
            pi.setLastUpdate(DateTimeUtils.toZonedDateTime(date));
            pi.setMilestones(new ArrayList<>());
            pi.setNodes(new ArrayList<>());
        }
        return pi;
    }
}
