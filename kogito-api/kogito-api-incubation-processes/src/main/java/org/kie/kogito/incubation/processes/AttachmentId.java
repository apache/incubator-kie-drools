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

import org.kie.kogito.incubation.common.LocalId;
import org.kie.kogito.incubation.common.LocalUriId;

public class AttachmentId extends LocalUriId implements LocalId {

    public static final String PREFIX = "attachments";

    private final TaskInstanceId taskId;
    private final String attachmentId;

    public AttachmentId(TaskInstanceId taskId, String attachmentId) {
        super(taskId.asLocalUri().append(PREFIX).append(attachmentId));
        if (!taskId.asLocalUri().startsWith(LocalProcessId.PREFIX)) {
            throw new IllegalArgumentException("Not a valid process path"); // fixme use typed exception
        }

        this.taskId = taskId;
        this.attachmentId = attachmentId;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

    public TaskInstanceId taskId() {
        return taskId;
    }

    public String attachmentId() {
        return attachmentId;
    }

}
