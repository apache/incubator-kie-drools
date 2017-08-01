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

import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple command to log the contextual data after given (10 sec by default) delay and return empty results. 
 * Just for demo purpose.
 * 
 */
public class DelayedPrintOutCommand extends PrintOutCommand {
    
    private static final Logger logger = LoggerFactory.getLogger(DelayedPrintOutCommand.class);

    public ExecutionResults execute(CommandContext ctx) {
    	Long delay = (Long)ctx.getData("delay");
    	if (delay == null) {
    		delay = 10000L;
    	}
        logger.info("Delaying execution of command for {}", delay);
        try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        return super.execute(ctx);
    }
    
}
