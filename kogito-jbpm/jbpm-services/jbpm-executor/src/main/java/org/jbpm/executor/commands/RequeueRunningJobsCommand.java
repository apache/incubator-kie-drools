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

package org.jbpm.executor.commands;

import org.jbpm.executor.RequeueAware;
import org.jbpm.services.api.service.ServiceRegistry;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequeueRunningJobsCommand implements Command{
    
    private static final Logger logger = LoggerFactory.getLogger(RequeueRunningJobsCommand.class);

    public ExecutionResults execute(CommandContext ctx) {
    	
    
    	Long olderThan = (Long) ctx.getData("MaxRunningTime");
    	Long requestId = (Long) ctx.getData("RequestId");
    	try {
    		ExecutorService executorService = (ExecutorService) ServiceRegistry.get().service(ServiceRegistry.EXECUTOR_SERVICE);
    		if (executorService instanceof RequeueAware) {
    			
    			if (requestId != null) {
    				logger.info("Requeue jobs by id {}", requestId);
    				((RequeueAware)executorService).requeueById(requestId);
    			} else {
    				logger.info("Requeue jobs older than {}", olderThan);
    				((RequeueAware)executorService).requeue(olderThan);
    			}
    		} else {
    			logger.info("Executor Service is not capable of jobs requeue");
    		}
			
		} catch (Exception e) {		
			logger.error("Error while requeueing jobs", e);
		}
    	
        ExecutionResults executionResults = new ExecutionResults();
        return executionResults;
    }
    
    
}
