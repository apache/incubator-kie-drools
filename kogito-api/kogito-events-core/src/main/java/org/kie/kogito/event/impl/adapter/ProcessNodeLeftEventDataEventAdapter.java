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
package org.kie.kogito.event.impl.adapter;

import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.process.ProcessInstanceNodeEventBody;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;

public class ProcessNodeLeftEventDataEventAdapter extends AbstractDataEventAdapter {

    public ProcessNodeLeftEventDataEventAdapter() {
        super(ProcessNodeLeftEvent.class);
    }

    @Override
    public DataEvent<?> adapt(Object payload) {
        ProcessNodeLeftEvent event = (ProcessNodeLeftEvent) payload;
        KogitoNodeInstance nodeInstance = (KogitoNodeInstance) event.getNodeInstance();
        int eventType = ProcessInstanceNodeEventBody.EVENT_TYPE_EXIT;

        if (nodeInstance.getCancelType() != null) {
            switch (nodeInstance.getCancelType()) {
                case ABORTED:
                    eventType = ProcessInstanceNodeEventBody.EVENT_TYPE_ABORTED;
                    break;
                case SKIPPED:
                    eventType = ProcessInstanceNodeEventBody.EVENT_TYPE_SKIPPED;
                    break;
                case OBSOLETE:
                    eventType = ProcessInstanceNodeEventBody.EVENT_TYPE_OBSOLETE;
                    break;
                case ERROR:
                    eventType = ProcessInstanceNodeEventBody.EVENT_TYPE_ERROR;
            }
        }

        return toProcessInstanceNodeEvent((ProcessNodeLeftEvent) payload, eventType);
    }

}
