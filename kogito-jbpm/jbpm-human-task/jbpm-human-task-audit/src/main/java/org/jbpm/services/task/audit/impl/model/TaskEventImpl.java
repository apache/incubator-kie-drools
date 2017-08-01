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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.audit.impl.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;

import org.kie.internal.task.api.model.TaskEvent;

/**
 *
 */
@Entity
@Table(name = "TaskEvent")
@SequenceGenerator(name = "taskEventIdSeq", sequenceName = "TASK_EVENT_ID_SEQ")
public class TaskEventImpl implements TaskEvent, Externalizable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "taskEventIdSeq")
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "OPTLOCK")
  private Integer version;

  private Long taskId;

  private Long workItemId;

  @Enumerated(EnumType.STRING)
  private TaskEventType type;

  private Long processInstanceId;

  private String userId;

  private String message;

  @Temporal(javax.persistence.TemporalType.TIMESTAMP)
  private Date logTime;

  public TaskEventImpl() {
  }

  public TaskEventImpl(long taskId, TaskEventType type, String userId) {
    this.taskId = taskId;
    this.type = type;
    this.userId = userId;
    this.logTime = new Date();
  }

  public TaskEventImpl(Long taskId, TaskEventType type, String userId, Date logTime) {
    this.taskId = taskId;
    this.type = type;
    this.userId = userId;
    this.logTime = logTime;
  }

  public TaskEventImpl(Long taskId, TaskEventType type, Long processInstanceId, Long workItemId, String userId, Date logTime) {
    this.taskId = taskId;
    this.type = type;
    this.processInstanceId = processInstanceId;
    this.workItemId = workItemId;
    this.userId = userId;
    this.logTime = logTime;
  }

  public TaskEventImpl(Long taskId, TaskEventType type, Long processInstanceId, Long workItemId, String userId) {
    this(taskId, type, processInstanceId, workItemId, userId, new Date());

  }

  public TaskEventImpl(Long taskId, TaskEventType type, Long processInstanceId, Long workItemId, String userId, String message) {
    this(taskId, type, processInstanceId, workItemId, userId, new Date());
    this.message = message;

  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public long getTaskId() {
    return taskId;
  }

  @Override
  public TaskEventType getType() {
    return type;
  }

  @Override
  public String getUserId() {
    return userId;
  }

  @Override
  public Date getLogTime() {
    return logTime;
  }

  @Override
  public Long getProcessInstanceId() {
    return processInstanceId;
  }

  public Long getWorkItemId() {
    return workItemId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException,
          ClassNotFoundException {
	  id = in.readLong();
	  
	  processInstanceId = in.readLong();
	  
	  taskId = in.readLong();
	  
	  type = TaskEventType.valueOf(in.readUTF());

      message = in.readUTF();

	  userId = in.readUTF();
	  
	  workItemId = in.readLong();
	  
	  if (in.readBoolean()) {
          logTime = new Date(in.readLong());
      }
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
	  out.writeLong( id );
	  
	  out.writeLong( processInstanceId );
	  
	  out.writeLong( taskId );
	  
	  if (type != null) {
      	out.writeUTF(type.name());
      } else {
      	out.writeUTF("");
      }

      if (message != null) {
        out.writeUTF(message);
      } else {
        out.writeUTF("");
      }


	  if (userId != null) {
      	out.writeUTF(userId);
      } else {
      	out.writeUTF("");
      }
	  
	  out.writeLong( workItemId );
	  
	  if (logTime != null) {
          out.writeBoolean(true);
          out.writeLong(logTime.getTime());
      } else {
          out.writeBoolean(false);
      }

  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
    hash = 97 * hash + (this.version != null ? this.version.hashCode() : 0);
    hash = 97 * hash + (this.taskId != null ? this.taskId.hashCode() : 0);
    hash = 97 * hash + (this.workItemId != null ? this.workItemId.hashCode() : 0);
    hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
    hash = 97 * hash + (this.message != null ? this.message.hashCode() : 0);
    hash = 97 * hash + (this.processInstanceId != null ? this.processInstanceId.hashCode() : 0);
    hash = 97 * hash + (this.userId != null ? this.userId.hashCode() : 0);
    hash = 97 * hash + (this.logTime != null ? this.logTime.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TaskEventImpl other = (TaskEventImpl) obj;
    if (!Objects.equals(this.id, other.id) && (this.id == null || !this.id.equals(other.id))) {
      return false;
    }
    if (!Objects.equals(this.version, other.version) && (this.version == null || !this.version.equals(other.version))) {
      return false;
    }
    if (!Objects.equals(this.taskId, other.taskId) && (this.taskId == null || !this.taskId.equals(other.taskId))) {
      return false;
    }
    if (!Objects.equals(this.workItemId, other.workItemId) && (this.workItemId == null || !this.workItemId.equals(other.workItemId))) {
      return false;
    }
    if (this.type != other.type) {
      return false;
    }
    if (!this.message.equals(other.message) ) {
      return false;
    }
    if (!Objects.equals(this.processInstanceId, other.processInstanceId) && (this.processInstanceId == null || !this.processInstanceId.equals(other.processInstanceId))) {
      return false;
    }
    if ((this.userId == null) ? (other.userId != null) : !this.userId.equals(other.userId)) {
      return false;
    }
    if (this.logTime != other.logTime && (this.logTime == null || !this.logTime.equals(other.logTime))) {
      return false;
    }
    return true;
  }

}
