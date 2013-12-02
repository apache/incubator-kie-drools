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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.util.AnnotationLiteral;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.events.AfterTaskAddedEvent;
import org.jbpm.services.task.events.BeforeTaskAddedEvent;
import org.jbpm.services.task.impl.model.ContentDataImpl;
import org.jbpm.services.task.impl.model.ContentImpl;
import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.I18NTextImpl;
import org.jbpm.services.task.impl.model.PeopleAssignmentsImpl;
import org.jbpm.services.task.impl.model.TaskDataImpl;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.services.task.impl.model.xml.JaxbTask;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.InternalTaskData;

/**
 * Operation.Start : [ new OperationCommand().{ status = [ Status.Ready ],
 * allowed = [ Allowed.PotentialOwner ], setNewOwnerToUser = true, newStatus =
 * Status.InProgress }, new OperationCommand().{ status = [ Status.Reserved ],
 * allowed = [ Allowed.Owner ], newStatus = Status.InProgress } ], *
 */
@Transactional
@XmlRootElement(name="add-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class AddTaskCommand extends TaskCommand<Long> {

    @XmlElement
    private JaxbTask jaxbTask;
    
    @XmlTransient
    private Task task;
    
    @XmlJavaTypeAdapter(JaxbMapAdapter.class)
    @XmlElement(name="parameter")
    private Map<String, Object> params;
    
    // TODO support ContentData marshalling
    private ContentData data;
    
    public AddTaskCommand() {
    }

    public AddTaskCommand(Task task, Map<String, Object> params) {
        setTask(task);
        this.params = params;
    }

    public AddTaskCommand(Task task, ContentData data) {
    	setTask(task);
        this.data = data;
    }

    public Long execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	if (task == null) {
        		task = jaxbTask;
        	}
        	if (task instanceof JaxbTask) {
        	    TaskImpl taskImpl = (TaskImpl) ((JaxbTask) task).getTask();
        	    if (data != null) {
                    return context.getTaskService().addTask(taskImpl, data);
                } else {
                    return context.getTaskService().addTask(taskImpl, params);
                }
            } else {
                if (data != null) {
                    return context.getTaskService().addTask(task, data);
                } else {
                    return context.getTaskService().addTask(task, params);
                }
            }
        }
        context.getTaskEvents().select(new AnnotationLiteral<BeforeTaskAddedEvent>() {
        }).fire(task);
        if (params != null) {
            ContentDataImpl contentData = ContentMarshallerHelper.marshal(params, null);
            ContentImpl content = new ContentImpl(contentData.getContent());
            context.getPm().persist(content);
            ((InternalTaskData) task.getTaskData()).setDocument(content.getId(), contentData);
        } else if (data != null) {
        	if (data != null) {
	            ContentImpl content = new ContentImpl(data.getContent());
	            context.getPm().persist(content);
	            ((InternalTaskData) task.getTaskData()).setDocument(content.getId(), data);
	        } 
        }
        
        context.getPm().persist(task);
        context.getTaskEvents().select(new AnnotationLiteral<AfterTaskAddedEvent>() {
        }).fire(task);
        return (Long) task.getId();
    }

    public JaxbTask getJaxbTask() {
        return jaxbTask;
    }

    public void setJaxbTask(JaxbTask jaxbTask) {
        this.jaxbTask = jaxbTask;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
    	this.task = task;
        if (task instanceof JaxbTask) {
        	this.jaxbTask = (JaxbTask) task;
        } else {
        	this.jaxbTask = new JaxbTask(task);
        }
    }

    public Map<String, Object> getParams() {
        return params;
    }
    
    public void setParams(Map<String, Object> params) {
    	this.params = params;
    }

    public ContentData getData() {
        return data;
    }

    public void setData(ContentData data) {
        this.data = data;
    }
}
