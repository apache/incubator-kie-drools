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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.drools.core.command.impl.GenericCommand;

/**
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class TaskCommand<T> implements GenericCommand<T> {

    @XmlElement(name="task-id")
    @XmlSchemaType(name="long")
    protected Long taskId;
    
    @XmlElement(name="user-id")
    @XmlSchemaType(name="string")
    protected String userId;
    
    @XmlElement(name="group-id")
    protected List<String> groupsIds;
    
    @XmlElement(name="target-entity-id")
    @XmlSchemaType(name="string")
    protected String targetEntityId;

    public Long getTaskId() {
        return this.taskId;
    }

    public void setTaskId(Long taskId) {
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
