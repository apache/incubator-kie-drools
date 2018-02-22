/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.executor.impl.concurrent;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionSynchronization;
import org.jbpm.executor.entities.RequestInfo;
import org.jbpm.executor.impl.AvailableJobsExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleTaskTransactionSynchronization implements TransactionSynchronization {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduleTaskTransactionSynchronization.class);
    
    private ScheduledExecutorService scheduler;
    private RequestInfo requestInfo;
    private Date date;
    private AvailableJobsExecutor jobProcessor;
    
    public ScheduleTaskTransactionSynchronization(ScheduledExecutorService scheduler, RequestInfo requestInfo, Date date, AvailableJobsExecutor jobProcessor) {
        super();
        this.scheduler = scheduler;
        this.requestInfo = requestInfo;
        this.date = date;
        this.jobProcessor = jobProcessor;
    }

    @Override
    public void beforeCompletion() {
        // no-op
    }
    
    @Override
    public void afterCompletion(int status) {
        
        if (status == TransactionManager.STATUS_COMMITTED) {
            PrioritisedRunnable jobExecution = new PrioritisedRunnable(requestInfo.getId(), requestInfo.getPriority(), requestInfo.getTime(), jobProcessor);
            if (date == null) {
                logger.debug("Directly executing request {}", requestInfo.getId());
                scheduler.execute(jobExecution);
            } else {
                long delay = date.getTime() - System.currentTimeMillis();
                logger.debug("Scheduling with delay {} for request {}", delay, requestInfo.getId());
                scheduler.schedule(jobExecution, delay, TimeUnit.MILLISECONDS);
            }
        }
    }
}
