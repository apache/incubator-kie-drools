/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task.commands;

import java.util.List;
import org.drools.core.command.impl.GenericCommand;

/**
 *
 */
public abstract class TaskCommand<T> implements GenericCommand {

    protected long taskId;
    protected String userId;
    protected List<String> groupsIds;
    protected String targetEntityId;

    public long getTaskId() {
        return this.taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getGroupsIds() {
        return this.groupsIds;
    }

    public void setGroupsIds(List<String> groupsIds) {
        this.groupsIds = groupsIds;
    }

    public String getTargetEntityId() {
        return this.targetEntityId;
    }

    public void setTargetEntityId(String targetEntityId) {
        this.targetEntityId = targetEntityId;
    }
}
