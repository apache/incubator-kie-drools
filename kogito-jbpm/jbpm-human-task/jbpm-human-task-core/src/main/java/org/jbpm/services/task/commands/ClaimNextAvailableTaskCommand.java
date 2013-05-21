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

import org.jboss.seam.transaction.Transactional;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

/**

 */
@Transactional
public class ClaimNextAvailableTaskCommand extends TaskCommand<Void> {

    private String language;
    
    public ClaimNextAvailableTaskCommand() {
    }
    
	public ClaimNextAvailableTaskCommand(String userId, String language) {
        this.userId = userId;
        this.language = language;
    }

    public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        if (context.getTaskService() != null) {
        	context.getTaskService().claimNextAvailable(userId, language);
        	return null;
        }
        List<TaskSummary> tasks = context.getTaskQueryService().getTasksAssignedAsPotentialOwner(userId, language);
        if(tasks.size() > 0){
            new ClaimTaskCommand(tasks.get(0).getId(), userId).execute(cntxt);
        }    
        return null;
    }
}
