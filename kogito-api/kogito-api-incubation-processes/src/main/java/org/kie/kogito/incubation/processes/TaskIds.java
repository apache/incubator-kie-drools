/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.incubation.processes;

import org.kie.kogito.incubation.common.LocalUriId;

public class TaskIds extends LocalUriId {
    private final ProcessInstanceId processInstanceId;

    public TaskIds(ProcessInstanceId processInstanceId) {
        super(processInstanceId.asLocalUri().append("tasks"));
        this.processInstanceId = processInstanceId;
    }

    public TaskId get(String taskId) {
        return new TaskId(this.processInstanceId, taskId);
    }

    public ProcessInstanceId processInstanceId() {
        return processInstanceId;
    }
}
