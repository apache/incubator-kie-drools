package org.drools.process.instance.impl;

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

import java.io.Serializable;
import java.util.Map;

import org.drools.process.core.Process;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.VariableScopeInstance;
import org.drools.process.instance.WorkItem;

/**
 * Default implementation of a process instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class ProcessInstanceImpl implements ProcessInstance, Serializable {

    private long id;
    private Process process;
    private int state = STATE_PENDING;
    private VariableScopeInstance variableScopeInstance = new VariableScopeInstanceImpl();

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
    
    public void workItemCompleted(WorkItem taskInstance) {
    }

    public void workItemAborted(WorkItem taskInstance) {
    }

    public Object getVariable(String name) {
        return variableScopeInstance.getVariable(name);
    }

    public Map<String, Object> getVariables() {
        return variableScopeInstance.getVariables();
    }

    public void setVariable(String name, Object value) {
        variableScopeInstance.setVariable(name, value);
    }
    
    public void start() {
        if ( getState() != ProcessInstanceImpl.STATE_PENDING ) {
            throw new IllegalArgumentException( "A process instance can only be started once" );
        }
        setState( ProcessInstanceImpl.STATE_ACTIVE );
        internalStart();
    }
    
    protected abstract void internalStart();

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
