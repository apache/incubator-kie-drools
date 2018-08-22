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

package org.jbpm.process.instance.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.util.MVELSafeHelper;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.impl.XmlProcessDumper;
import org.jbpm.process.core.impl.XmlProcessDumperFactory;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.util.PatternConstants;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.impl.ProcessInstanceResolverFactory;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.rule.Agenda;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of a process instance.
 * 
 */
public abstract class ProcessInstanceImpl implements ProcessInstance, Serializable {
	private static final Logger logger = LoggerFactory.getLogger(ProcessInstanceImpl.class);
	private static final long serialVersionUID = 510l;
	
	private long id;
    private String processId;
    private transient Process process;
    private String processXml;
    private int state = STATE_PENDING;
    private Map<String, ContextInstance> contextInstances = new HashMap<String, ContextInstance>();
    private Map<String, List<ContextInstance>> subContextInstances = new HashMap<String, List<ContextInstance>>();
    private transient InternalKnowledgeRuntime kruntime;
    private Map<String, Object> metaData = new HashMap<String, Object>();
    private String outcome;
    private long parentProcessInstanceId = -1;
    private String description;

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
    
    public void updateProcess(final Process process) {
    	setProcess(process);
    	XmlProcessDumper dumper = XmlProcessDumperFactory.newXmlProcessDumperFactory();
    	this.processXml = dumper.dumpProcess(process);
    }
    
    public String getProcessXml() {
    	return processXml;
    }
    
    public void setProcessXml(String processXml) {
    	if (processXml != null && processXml.trim().length() > 0) {
    		this.processXml = processXml;
    	}
    }

    public Process getProcess() {
        if (this.process == null) {
        	if (processXml == null) {
        		if (kruntime == null) {
                    throw new IllegalStateException("Process instance " + id + "[" + processId + "] is disconnected.");
        		}
        		this.process = kruntime.getKieBase().getProcess(processId);
        	} else {
    	    	XmlProcessDumper dumper = XmlProcessDumperFactory.newXmlProcessDumperFactory();
        		this.process = dumper.readProcess(processXml);
        	}
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
    	start(null);
    }
    
    public void start(String trigger) {
    	synchronized (this) {
            if ( getState() != ProcessInstanceImpl.STATE_PENDING ) {
                throw new IllegalArgumentException( "A process instance can only be started once" );
            }
            setState( ProcessInstanceImpl.STATE_ACTIVE );
            internalStart(trigger);
		}
    }
    
    protected abstract void internalStart(String trigger);
    
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

    public long getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(long parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }
    
    public String getDescription() {
		if (description == null) {
			description = process.getName();			
			if (process != null) {
				Object metaData = process.getMetaData().get("customDescription");
				if (metaData instanceof String) {
					String customDescription = (String) metaData;
					Map<String, String> replacements = new HashMap<String, String>();
					Matcher matcher = PatternConstants.PARAMETER_MATCHER.matcher(customDescription);
					while (matcher.find()) {
						String paramName = matcher.group(1);
						if (replacements.get(paramName) == null) {
							try {
								String value = (String) MVELSafeHelper.getEvaluator()
										.eval(paramName,new ProcessInstanceResolverFactory(((WorkflowProcessInstance) this)));
								replacements.put(paramName, value);
							} catch (Throwable t) {
								logger.error("Could not resolve customDescription, parameter " + paramName, t);
								logger.error("Continuing without setting description.");
							}
						}
					}
					for (Map.Entry<String, String> replacement : replacements.entrySet()) {
						customDescription = customDescription.replace("#{" + replacement.getKey() + "}", replacement.getValue());
					}
					
					description = customDescription;
				}
			}
		}
    	
    	return description;
    }
    
    public void setDescription(String description) {
    	this.description = description;
    }
}
