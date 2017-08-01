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

import org.kie.api.runtime.Context;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement(name="get-task-owned-by-exp-date-before-date-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTaskOwnedByExpDateBeforeDateCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

	private static final long serialVersionUID = 5077599352603072633L;
	
    @XmlElement
	private List<Status> statuses;
    
    @XmlElement
    private Date expirationDate;
	
	public GetTaskOwnedByExpDateBeforeDateCommand() {
	}
	
	public GetTaskOwnedByExpDateBeforeDateCommand(String userId, List<Status> status, Date expirationDate) {
		this.userId = userId;
		this.statuses = status;
		this.expirationDate = expirationDate;
    }
	
	public List<Status> getStatuses() {
		return statuses;
	}
	
	public void setStatuses(List<Status> status) {
		this.statuses = status;
	}

    public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}


	public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        doCallbackUserOperation(userId, context);
        
    	return context.getTaskQueryService().getTasksOwnedByExpirationDateBeforeSpecifiedDate(userId, statuses, expirationDate);
    
    }

}
