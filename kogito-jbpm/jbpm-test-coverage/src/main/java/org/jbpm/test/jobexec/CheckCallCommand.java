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

package org.jbpm.test.jobexec;

import java.util.concurrent.CyclicBarrier;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * If this class is used by more than one test, then 
 * this class needs to be changed so that multiple tests 
 * do not cause deadlocks by using the same CyclicBarrier instance. 
 * 
 * At the moment, this test is only used by the 
 * @{link {@link AsyncCaseTest#testAsyncWorkItem()}
 * test method.
 */
public class CheckCallCommand implements Command {
    
    private static final Logger logger = LoggerFactory.getLogger(CheckCallCommand.class);

    private static CyclicBarrier barrier = new CyclicBarrier(2);
    
    public CheckCallCommand() {
   
    }
    
    public static CyclicBarrier getBarrier() {
        return barrier;
    }

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        logger.debug("Command executed on executor with data {}", ctx.getData());
        
        // wait for all parties to wait on barrier
        barrier.await();
        
        ExecutionResults executionResults = new ExecutionResults();
        executionResults.setData("commandExecuted", true);
        return executionResults;
    }
    
}
