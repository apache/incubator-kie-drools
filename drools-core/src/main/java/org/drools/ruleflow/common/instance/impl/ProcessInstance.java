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

import org.drools.ruleflow.common.core.IProcess;
import org.drools.ruleflow.common.instance.IProcessInstance;

/**
 * Default implementation of a process instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class ProcessInstance implements IProcessInstance {

    private long id;
    private IProcess process;
    private int state = STATE_PENDING;
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public void setProcess(IProcess process) {
        this.process = process;
    }
    
    public IProcess getProcess() {
        return process;
    }
    
    public void setState(int state) {
        this.state = state;
    }
    
    public int getState() {
        return state;
    }
    
    public String toString() {
    	StringBuffer b = new StringBuffer("ProcessInstance ");
    	b.append(getId());
    	b.append(" [processId=");
    	b.append(process.getId());
    	b.append(",state=");
    	b.append(state);
    	b.append("]");
    	return b.toString();
    }
}
