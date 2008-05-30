/*
 * Copyright 2008 Red Hat
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
 *
 * Created on May 29th, 2008
 */
package org.drools.time.impl;

import org.drools.time.Job;
import org.drools.time.JobHandle;

/**
 * A default implementation for the JobHandle interface
 * 
 * @author etirelli
 */
public class DefaultJobHandle
    implements
    JobHandle {
    
    private static final long serialVersionUID = 5812005196020575395L;
    
    private final Job     job;

    public DefaultJobHandle(Job job) {
        this.job = job;
    }

    public Object getJob() {
        return job;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((job == null) ? 0 : job.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final DefaultJobHandle other = (DefaultJobHandle) obj;
        if ( job == null ) {
            if ( other.job != null ) return false;
        } else if ( !job.equals( other.job ) ) return false;
        return true;
    }
}