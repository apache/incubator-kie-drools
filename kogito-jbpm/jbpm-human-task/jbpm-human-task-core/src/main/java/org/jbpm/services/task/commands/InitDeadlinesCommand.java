/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.services.task.utils.ClassUtil;
import org.kie.internal.command.Context;
import org.kie.internal.task.api.TaskDeadlinesService;
import org.kie.internal.task.api.TaskDeadlinesService.DeadlineType;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.DeadlineSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name="init-deadlines-command")
@XmlAccessorType(XmlAccessType.NONE)
public class InitDeadlinesCommand extends TaskCommand<Void> {
	
	private static final long serialVersionUID = -8095766991770311489L;
	private static final Logger logger = LoggerFactory.getLogger(InitDeadlinesCommand.class);

	public InitDeadlinesCommand() {		
	}

	@Override
	public Void execute(Context context) {
		TaskContext ctx = (TaskContext) context;
		
		TaskPersistenceContext persistenceContext = ctx.getPersistenceContext();
		TaskDeadlinesService deadlineService = ctx.getTaskDeadlinesService();
		
        try {
	        long now = System.currentTimeMillis();
	        List<DeadlineSummary> resultList = persistenceContext.queryInTransaction("UnescalatedStartDeadlines",
	        										ClassUtil.<List<DeadlineSummary>>castClass(List.class));
	        for (DeadlineSummary summary : resultList) {
	            long delay = summary.getDate().getTime() - now;
	            deadlineService.schedule(summary.getTaskId(), summary.getDeadlineId(), delay, DeadlineType.START);
	
	        }
	        
	        resultList = persistenceContext.queryInTransaction("UnescalatedEndDeadlines",
	        		ClassUtil.<List<DeadlineSummary>>castClass(List.class));
	        for (DeadlineSummary summary : resultList) {
	            long delay = summary.getDate().getTime() - now;
	            deadlineService.schedule(summary.getTaskId(), summary.getDeadlineId(), delay, DeadlineType.END);
	        }
        } catch (Exception e) {

        	logger.error("Error when executing deadlines", e);
        }
		return null;
	}

}
