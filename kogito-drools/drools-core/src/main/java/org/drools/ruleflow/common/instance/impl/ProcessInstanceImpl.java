package org.drools.ruleflow.common.instance.impl;

/*
 * Copyright 2005 JBoss Inc
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

import org.drools.ruleflow.common.core.Process;
import org.drools.ruleflow.common.instance.WorkItem;

/**
 * Default implementation of a process instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class ProcessInstanceImpl
    implements
    org.drools.ruleflow.common.instance.ProcessInstance {

    private long     id;
    private Process process;
    private int      state = STATE_PENDING;

    public void setId(final long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setProcess(final Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return this.process;
    }

    public void setState(final int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }
    
    public void taskCompleted(WorkItem taskInstance) {
    }

    public void taskAborted(WorkItem taskInstance) {
    }

    public String toString() {
        final StringBuffer b = new StringBuffer( "ProcessInstance " );
        b.append( getId() );
        b.append( " [processId=" );
        b.append( this.process.getId() );
        b.append( ",state=" );
        b.append( this.state );
        b.append( "]" );
        return b.toString();
    }
}
