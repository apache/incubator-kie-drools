/**
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

package org.jbpm.process.instance.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.common.InternalKnowledgeRuntime;
import org.kie.definition.process.Process;
import org.kie.runtime.rule.Agenda;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;

/**
 * Default implementation of a process instance.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public abstract class ProcessInstanceImpl implements ProcessInstance, Serializable {

	private static final long serialVersionUID = 510l;
	
	private long id;
    private String processId;
    private transient Process process;
    private int state = STATE_PENDING;
    private Map<String, ContextInstance> contextInstances = new HashMap<String, ContextInstance>();
    private Map<String, List<ContextInstance>> subContextInstances = new HashMap<String, List<ContextInstance>>();
    private transient InternalKnowledgeRuntime kruntime;
    private Map<String, Object> metaData = new HashMap<String, Object>();
    private String outcome;

    public void setId(final long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setProcess(final Process process) {
        this.processId = process.getId();
        this.process = ( Process ) process;
    }

    public Process getProcess() {
        if (this.process == null) {
            this.process = kruntime.getKnowledgeBase().getProcess(processId);
        }
        return this.process;
    }
    
    public void setProcessId(String processId) {
    	this.processId = processId;
    }
    
    public String getProcessId() {
        return processId;
    }
    
    public String getProcessName() {
    	return getProcess().getName();
    }

    public void setState(final int state) {
        internalSetState(state);
    }
    
    public void setState(final int state, String outcome) {
        this.outcome = outcome;
        internalSetState(state);
    }
    
    public void internalSetState(final int state) {
    	this.state = state;
    }

    public int getState() {
        return this.state;
    }
    
    public void setKnowledgeRuntime(final InternalKnowledgeRuntime kruntime) {
        if ( this.kruntime != null ) {
            throw new IllegalArgumentException( "Runtime can only be set once." );
        }
        this.kruntime = kruntime;
    }

    public InternalKnowledgeRuntime getKnowledgeRuntime() {
        return this.kruntime;
    }
    
	public Agenda getAgenda() {
		if (getKnowledgeRuntime() == null) {
			return null;
		}
		return getKnowledgeRuntime().getAgenda();
	}

    public ContextContainer getContextContainer() {
        return ( ContextContainer ) getProcess();
    }
    
    public void setContextInstance(String contextId, ContextInstance contextInstance) {
        this.contextInstances.put(contextId, contextInstance);
    }
    
    public ContextInstance getContextInstance(String contextId) {
        ContextInstance contextInstance = this.contextInstances.get(contextId);
        if (contextInstance != null) {
            return contextInstance;
        }
        Context context = ((ContextContainer)getProcess()).getDefaultContext(contextId);
        if (context != null) {
            contextInstance = getContextInstance(context);
            return contextInstance;
        }
        return null;
    }
    
    public List<ContextInstance> getContextInstances(String contextId) {
        return this.subContextInstances.get(contextId);
    }
    
    public void addContextInstance(String contextId, ContextInstance contextInstance) {
        List<ContextInstance> list = this.subContextInstances.get(contextId);
        if (list == null) {
            list = new ArrayList<ContextInstance>();
            this.subContextInstances.put(contextId, list);
        }
        list.add(contextInstance);
    }

    public void removeContextInstance(String contextId, ContextInstance contextInstance) {
        List<ContextInstance> list = this.subContextInstances.get(contextId);
        if (list != null) {
            list.remove(contextInstance);
        }
    }

    public ContextInstance getContextInstance(String contextId, long id) {
        List<ContextInstance> contextInstances = subContextInstances.get(contextId);
        if (contextInstances != null) {
            for (ContextInstance contextInstance: contextInstances) {
                if (contextInstance.getContextId() == id) {
                    return contextInstance;
                }
            }
        }
        return null;
    }

    public ContextInstance getContextInstance(final Context context) {
        ContextInstanceFactory conf = ContextInstanceFactoryRegistry.INSTANCE.getContextInstanceFactory(context);
        if (conf == null) {
            throw new IllegalArgumentException("Illegal context type (registry not found): " + context.getClass());
        }
        ContextInstance contextInstance = (ContextInstance) conf.getContextInstance(context, this, this);
        if (contextInstance == null) {
            throw new IllegalArgumentException("Illegal context type (instance not found): " + context.getClass());
        }
        return contextInstance;
    }
    
    public void signalEvent(String type, Object event) {
    }

    public void start() {
    	synchronized (this) {
            if ( getState() != ProcessInstanceImpl.STATE_PENDING ) {
                throw new IllegalArgumentException( "A process instance can only be started once" );
            }
            setState( ProcessInstanceImpl.STATE_ACTIVE );
            internalStart();
		}
    }
    
    protected abstract void internalStart();
    
    public void disconnect() {
        ((InternalProcessRuntime) kruntime.getProcessRuntime()).getProcessInstanceManager().internalRemoveProcessInstance(this);
        process = null;
        kruntime = null;
    }
    
    public void reconnect() {
    	((InternalProcessRuntime) kruntime.getProcessRuntime()).getProcessInstanceManager().internalAddProcessInstance(this);
    }

    public String[] getEventTypes() {
    	return null;
    }
    
    public String toString() {
        final StringBuilder b = new StringBuilder( "ProcessInstance " );
        b.append( getId() );
        b.append( " [processId=" );
        b.append( this.process.getId() );
        b.append( ",state=" );
        b.append( this.state );
        b.append( "]" );
        return b.toString();
    }

	public Map<String, Object> getMetaData() {
		return this.metaData;
	}

    public void setMetaData(String name, Object data) {
        this.metaData.put(name, data);
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getOutcome() {
        return outcome;
    }
    
}
