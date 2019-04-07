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

import org.jbpm.executor.impl.AvailableJobsExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrioritisedRunnable implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(PrioritisedRunnable.class);

    private long id;
    private int priority;
    private Date fireDate;
    
    private AvailableJobsExecutor availableJobsExecutor;

    public PrioritisedRunnable(long id, int priority, Date fireDate, AvailableJobsExecutor availableJobsExecutor) {
        this.id = id;
        this.priority = priority;
        this.fireDate = fireDate;
        
        this.availableJobsExecutor = availableJobsExecutor;
    }

    public void run() {
        try {
            logger.debug("About to execute jobs...");
            
            this.availableJobsExecutor.executeJob(this.id);
        } catch (Throwable e) {
            logger.warn("Error while executing jobs  with id {} due to {}", this.id, e.getMessage(), e);
        }
    }

    public long getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public Date getFireDate() {
        return fireDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PrioritisedRunnable other = (PrioritisedRunnable) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
