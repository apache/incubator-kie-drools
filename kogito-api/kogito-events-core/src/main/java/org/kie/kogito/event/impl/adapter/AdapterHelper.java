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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.kie.kogito.event.process.ProcessInstanceEventMetadata;
import org.kie.kogito.event.usertask.UserTaskInstanceEventMetadata;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.model.ProcessInfo;

public class AdapterHelper {

    public static Map<String, Object> buildProcessMetadata(KogitoWorkflowProcessInstance pi) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, pi.getId());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA, pi.getProcessVersion());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA, pi.getProcessId());
        metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_STATE_META_DATA, String.valueOf(pi.getState()));
        metadata.put(ProcessInstanceEventMetadata.PROCESS_TYPE_META_DATA, pi.getProcess().getType());
        metadata.put(ProcessInstanceEventMetadata.PARENT_PROCESS_INSTANCE_ID_META_DATA, pi.getParentProcessInstanceId());
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_ID_META_DATA, pi.getRootProcessId());
        metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_INSTANCE_ID_META_DATA, pi.getRootProcessInstanceId());
        return metadata;
    }

    public static Map<String, Object> buildUserTaskMetadata(UserTaskInstance uti) {

        Map<String, Object> metadata = new HashMap<>();

        Optional<ProcessInfo> optionalProcessInfo = Optional.ofNullable(uti.getProcessInfo());

        // Conditionally adding process info metadata, it will only be available after the user task has been completely initialized
        optionalProcessInfo.ifPresent(processInfo -> {
            metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_ID_META_DATA, processInfo.getProcessInstanceId());
            metadata.put(ProcessInstanceEventMetadata.PROCESS_VERSION_META_DATA, processInfo.getProcessVersion());
            metadata.put(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA, processInfo.getProcessId());
            metadata.put(ProcessInstanceEventMetadata.PARENT_PROCESS_INSTANCE_ID_META_DATA, processInfo.getParentProcessInstanceId());
            metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_ID_META_DATA, processInfo.getRootProcessId());
            metadata.put(ProcessInstanceEventMetadata.ROOT_PROCESS_INSTANCE_ID_META_DATA, processInfo.getRootProcessInstanceId());
            metadata.put(ProcessInstanceEventMetadata.PROCESS_INSTANCE_STATE_META_DATA, String.valueOf(uti.getMetadata().get("ProcessInstanceState")));
            metadata.put(ProcessInstanceEventMetadata.PROCESS_TYPE_META_DATA, uti.getMetadata().get("ProcessType"));
        });

        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_ID_META_DATA, uti.getId());
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_REFERENCE_ID_META_DATA, uti.getUserTask().getReferenceName());
        metadata.put(UserTaskInstanceEventMetadata.USER_TASK_INSTANCE_STATE_META_DATA, uti.getStatus().getName());

        return metadata;
    }

    public static String extractRuntimeSource(String service, Map<String, String> metadata) {
        return buildSource(service, metadata.get(ProcessInstanceEventMetadata.PROCESS_ID_META_DATA));
    }

    public static String buildSource(String service, String processId) {
        if (processId == null) {
            return null;
        } else {
            processId = processId.replace(" ", "-");
            return service + "/" + (processId.contains(".") ? processId.substring(processId.lastIndexOf('.') + 1) : processId);
        }
    }
}
