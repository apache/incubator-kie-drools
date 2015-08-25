/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.jobexec;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckCallCommand implements Command {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckCallCommand.class);

    private static Boolean commandExecuted = false;
    private static Object LOCK = new Object();
    
    public CheckCallCommand() {
        commandExecuted = false;
    }
    
    public static Boolean isCommandExecuted() {
        return commandExecuted;
    }
    
    public static Object getLOCK() {
        return LOCK;
    }

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        logger.info("Command executed on executor with data {}", ctx.getData());
        synchronized (LOCK) {
            commandExecuted = true;
            LOCK.notifyAll();
        }
        ExecutionResults executionResults = new ExecutionResults();
        executionResults.setData("commandExecuted", true);
        return executionResults;
    }
    
}
