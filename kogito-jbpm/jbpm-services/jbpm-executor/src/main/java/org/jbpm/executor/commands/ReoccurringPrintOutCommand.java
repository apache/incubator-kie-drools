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

import java.util.Date;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.executor.Reoccurring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple command to log the contextual data and return empty results as reoccurring job. 
 * Just for demo purpose.
 * 
 */
public class ReoccurringPrintOutCommand implements Command, Reoccurring {
    
    private static final Logger logger = LoggerFactory.getLogger(ReoccurringPrintOutCommand.class);

    public ExecutionResults execute(CommandContext ctx) {
        logger.info("Command executed on executor with data {} at {}", ctx.getData(), new Date());
        ExecutionResults executionResults = new ExecutionResults();
        return executionResults;
    }

	@Override
	public Date getScheduleTime() {
		long currentTime = System.currentTimeMillis();
		return new Date(currentTime + 1000);
	}
    
}
