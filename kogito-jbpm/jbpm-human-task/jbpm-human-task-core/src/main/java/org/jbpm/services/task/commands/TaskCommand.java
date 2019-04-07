/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.command.ExecutableCommand;

/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class TaskCommand<T> implements ExecutableCommand<T> {

	private static final long serialVersionUID = -8814813191723981179L;
	
	@XmlElement(name="task-id")
    @XmlSchemaType(name="long")
    protected Long taskId;
    
    @XmlElement(name="user-id")
    @XmlSchemaType(name="string")
    protected String userId;
    
    @XmlElement(name="group-id")
    protected List<String> groupIds;
    
    @XmlElement(name="target-entity-id")
    @XmlSchemaType(name="string")
    protected String targetEntityId;

    public Long getTaskId() {
        return this.taskId;
    }

    public final void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public final String getUserId() {
        return userId;
    }

    public final void setUserId(String userId) {
        this.userId = userId;
    }

    public final List<String> getGroupsIds() {
        return this.groupIds;
    }

    public final void setGroupsIds(List<String> groupsIds) {
        this.groupIds = groupsIds;
    }

    public final String getTargetEntityId() {
        return this.targetEntityId;
    }

    public final void setTargetEntityId(String targetEntityId) {
        this.targetEntityId = targetEntityId;
    }
}
