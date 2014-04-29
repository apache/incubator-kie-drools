/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.executor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Heart of the executor component - executes the actual tasks.
 * Handles retries and error management. Based on results of execution notifies
 * defined callbacks about the execution results.
 *
 */
public class ExecutorRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorRunnable.class);

    private AvailableJobsExecutor availableJobsExecutor;       
    
    public void setAvailableJobsExecutor(AvailableJobsExecutor availableJobsExecutor) {
		this.availableJobsExecutor = availableJobsExecutor;
	}

    public void run() {
    	try {
    		logger.debug("About to execute jobs...");
    		
    		this.availableJobsExecutor.executeJob();
    	} catch (Exception e) {
    		logger.warn("Error while executing jobs due to {}" + e.getMessage());
    	}
    }
    
   
}
