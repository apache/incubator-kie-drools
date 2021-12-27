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

public class CommentId extends LocalUriId implements LocalId {

    public static final String PREFIX = "comments";

    private final TaskInstanceId taskInstanceId;
    private final String commentId;

    public CommentId(TaskInstanceId taskInstanceId, String commentId) {
        super(taskInstanceId.asLocalUri().append(PREFIX).append(commentId));
        if (!taskInstanceId.asLocalUri().startsWith(LocalProcessId.PREFIX)) {
            throw new IllegalArgumentException("Not a valid process path"); // fixme use typed exception
        }

        this.taskInstanceId = taskInstanceId;
        this.commentId = commentId;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

    public TaskInstanceId taskInstanceId() {
        return taskInstanceId;
    }

    public String commentId() {
        return commentId;
    }

}
