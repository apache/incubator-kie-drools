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

package org.jbpm.kie.services.impl.bpmn2;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.jbpm.services.api.model.UserTaskDefinition;

public class UserTaskDefinitionImpl implements UserTaskDefinition, Serializable {
		
	private static final long serialVersionUID = -8240667577168891456L;
	
	private String id;
	private String name;
	private Integer priority;
	private String comment;
	private String createdBy;
	private boolean skippable;
	private String formName;

	private Collection<String> associatedEntities;
	private Map<String, String> taskInputMappings;
	private Map<String, String> taskOutputMappings;

	@Override
	public String getName() {		
		return name;
	}

	@Override
	public Integer getPriority() {		
		return priority;
	}

	@Override
	public String getComment() {		
		return comment;
	}

	@Override
	public String getCreatedBy() {		
		return createdBy;
	}

	@Override
	public boolean isSkippable() {		
		return skippable;
	}

	@Override
	public Collection<String> getAssociatedEntities() {		
		return associatedEntities;
	}

	@Override
	public Map<String, String> getTaskInputMappings() {		
		return taskInputMappings;
	}

	@Override
	public Map<String, String> getTaskOutputMappings() {		
		return taskOutputMappings;
	}
	
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getFormName() {
        return formName;
    }

	public void setName(String name) {
		this.name = name;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setSkippable(boolean skippable) {
		this.skippable = skippable;
	}

	public void setAssociatedEntities(Collection<String> associatedEntities) {
		this.associatedEntities = associatedEntities;
	}

	public void setTaskInputMappings(Map<String, String> taskInputMappings) {
		this.taskInputMappings = taskInputMappings;
	}

	public void setTaskOutputMappings(Map<String, String> taskOutputMappings) {
		this.taskOutputMappings = taskOutputMappings;
	}
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setFormName(String formName) {
        this.formName = formName;
    }

}
