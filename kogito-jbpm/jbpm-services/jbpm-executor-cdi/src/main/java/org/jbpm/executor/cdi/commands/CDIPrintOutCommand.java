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

package org.jbpm.executor.cdi.commands;

import javax.enterprise.inject.spi.BeanManager;

import org.apache.commons.lang3.StringUtils;
import org.jbpm.executor.cdi.CDIUtils;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.ExecutorService;
import org.kie.api.runtime.process.WorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple command to log the contextual data and return empty results. After attempting to get BeanManager
 * and creating simple CDI bean based on given class name as parameter. 
 * Just for demo purpose.
 * 
 */
public class CDIPrintOutCommand implements Command{
    
    private static final Logger logger = LoggerFactory.getLogger(CDIPrintOutCommand.class);

    public ExecutionResults execute(CommandContext ctx) {
    	BeanManager manager = CDIUtils.lookUpBeanManager(ctx);
    	String clazz = (String) getParameter(ctx, "CDIBeanClassName");
    	if (StringUtils.isEmpty(clazz)) {
    		clazz = ExecutorService.class.getName();
    	}
    			
    	try {
			Object cdiBean = CDIUtils.createBean(Class.forName(clazz), manager);
			logger.info("CDI bean created {}", cdiBean);
		} catch (Exception e) {		
			logger.error("Error while creating CDI bean from jbpm executor", e);
		}
    	
        logger.info("Command executed on executor with data {}", ctx.getData());
        ExecutionResults executionResults = new ExecutionResults();
        return executionResults;
    }
    
    protected Object getParameter(CommandContext commandContext, String parameterName) {
		if (commandContext.getData(parameterName) != null) {
			return commandContext.getData(parameterName);
		}
		WorkItem workItem = (WorkItem) commandContext.getData("workItem");
		if (workItem != null) {
			return workItem.getParameter(parameterName);
		}
		return null;
	}
    
}
