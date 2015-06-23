/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.persistence.processinstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionManagerHelper;
import org.jbpm.persistence.ProcessPersistenceContext;
import org.jbpm.persistence.ProcessPersistenceContextManager;
import org.jbpm.persistence.correlation.CorrelationKeyInfo;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstanceManager;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.StateBasedNodeInstance;
import org.jbpm.workflow.instance.node.TimerNodeInstance;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

/**
 * This is an implementation of the {@link ProcessInstanceManager} that uses JPA.
 * </p>
 * What's important to remember here is that we have a jbpm-console which has 1 static (stateful) knowledge session
 * which is used by multiple threads: each request sent to the jbpm-console is picked up in it's own thread. 
 * </p>
 * This means that multiple threads can be using the same instance of this class. 
 */
public class JPAProcessInstanceManager
    implements
    ProcessInstanceManager {

    private InternalKnowledgeRuntime kruntime;
    // In a scenario in which 1000's of processes are running daily,
    //   lazy initialization is more costly than eager initialization
    // Added volatile so that if something happens, we can figure out what
    private volatile transient Map<Long, ProcessInstance> processInstances = new ConcurrentHashMap<Long, ProcessInstance>();

    
    public void setKnowledgeRuntime(InternalKnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
    }

    public void addProcessInstance(ProcessInstance processInstance, CorrelationKey correlationKey) {
        ProcessInstanceInfo processInstanceInfo = new ProcessInstanceInfo( processInstance, this.kruntime.getEnvironment() );
        ProcessPersistenceContext context 
            = ((ProcessPersistenceContextManager) this.kruntime.getEnvironment()
                    .get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER ))
                    .getProcessPersistenceContext();

        processInstanceInfo = context.persist( processInstanceInfo );
        ((org.jbpm.process.instance.ProcessInstance) processInstance).setId( processInstanceInfo.getId() );
        processInstanceInfo.updateLastReadDate();
        // persist correlation if exists
        if (correlationKey != null) {
            CorrelationKeyInfo correlationKeyInfo = (CorrelationKeyInfo) correlationKey;
            correlationKeyInfo.setProcessInstanceId(processInstanceInfo.getId());
            context.persist(correlationKeyInfo);
        }
        internalAddProcessInstance(processInstance);
    }
    
    public void internalAddProcessInstance(ProcessInstance processInstance) {
        if( ((ConcurrentHashMap<Long, ProcessInstance>) processInstances)
                .putIfAbsent(processInstance.getId(), processInstance) 
                != null ) { 
            throw new ConcurrentModificationException(
                    "Duplicate process instance [" + processInstance.getProcessId() + "/" + processInstance.getId() + "]"
                    + " added to process instance manager." );
        }
    }

    public ProcessInstance getProcessInstance(long id) {
        return getProcessInstance(id, false);
    }

    public ProcessInstance getProcessInstance(long id, boolean readOnly) {
        InternalRuntimeManager manager = (InternalRuntimeManager) kruntime.getEnvironment().get("RuntimeManager");
        if (manager != null) {
            manager.validate((KieSession) kruntime, ProcessInstanceIdContext.get(id));
        }
        TransactionManager txm = (TransactionManager) this.kruntime.getEnvironment().get( EnvironmentName.TRANSACTION_MANAGER );
        org.jbpm.process.instance.ProcessInstance processInstance = null;
        processInstance = (org.jbpm.process.instance.ProcessInstance) this.processInstances.get(id);
        if (processInstance != null) {
            if (((WorkflowProcessInstanceImpl) processInstance).isPersisted() && !readOnly) {
            	ProcessPersistenceContextManager ppcm 
        	    = (ProcessPersistenceContextManager) this.kruntime.getEnvironment().get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER );
            	ppcm.beginCommandScopedEntityManager();
            	ProcessPersistenceContext context = ppcm.getProcessPersistenceContext();
                ProcessInstanceInfo processInstanceInfo = context.findProcessInstanceInfo( id );
                if ( processInstanceInfo == null ) {
                    return null;
                }                
                TransactionManagerHelper.addToUpdatableSet(txm, processInstanceInfo);
                processInstanceInfo.updateLastReadDate();
  
            }
        	return processInstance;
        }

    	// Make sure that the cmd scoped entity manager has started
    	ProcessPersistenceContextManager ppcm 
    	    = (ProcessPersistenceContextManager) this.kruntime.getEnvironment().get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER );
    	ppcm.beginCommandScopedEntityManager();
    	
        ProcessPersistenceContext context = ppcm.getProcessPersistenceContext();
        ProcessInstanceInfo processInstanceInfo = context.findProcessInstanceInfo( id );
        if ( processInstanceInfo == null ) {
            return null;
        }
        processInstance = (org.jbpm.process.instance.ProcessInstance)
        	processInstanceInfo.getProcessInstance(kruntime, this.kruntime.getEnvironment());
        if (!readOnly) {
            processInstanceInfo.updateLastReadDate();
            TransactionManagerHelper.addToUpdatableSet(txm, processInstanceInfo);
        }
        if (((ProcessInstanceImpl) processInstance).getProcessXml() == null) {
	        Process process = kruntime.getKieBase().getProcess( processInstance.getProcessId() );
	        if ( process == null ) {
	            throw new IllegalArgumentException( "Could not find process " + processInstance.getProcessId() );
	        }
	        processInstance.setProcess( process );
        }
        if ( processInstance.getKnowledgeRuntime() == null ) {
            Long parentProcessInstanceId = (Long) ((ProcessInstanceImpl) processInstance).getMetaData().get("ParentProcessInstanceId");
            if (parentProcessInstanceId != null) {
                kruntime.getProcessInstance(parentProcessInstanceId);
            }
            processInstance.setKnowledgeRuntime( kruntime );
            ((ProcessInstanceImpl) processInstance).reconnect();
        }
        return processInstance;
    }

    public Collection<ProcessInstance> getProcessInstances() {
        return Collections.unmodifiableCollection(processInstances.values());
    }

    public void removeProcessInstance(ProcessInstance processInstance) {
        ProcessPersistenceContext context = ((ProcessPersistenceContextManager) this.kruntime.getEnvironment().get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER )).getProcessPersistenceContext();
        ProcessInstanceInfo processInstanceInfo = context.findProcessInstanceInfo( processInstance.getId() );
        
        if ( processInstanceInfo != null ) {
            context.remove( processInstanceInfo );
        }
        internalRemoveProcessInstance(processInstance);
    }

    public void internalRemoveProcessInstance(ProcessInstance processInstance) {
        processInstances.remove( processInstance.getId() );
    }
    
    public void clearProcessInstances() {
        for (ProcessInstance processInstance: new ArrayList<ProcessInstance>(processInstances.values())) {
            ((ProcessInstanceImpl) processInstance).disconnect();
        }
    }

    public void clearProcessInstancesState() {
        try {
            // at this point only timers are considered as state that needs to be cleared
            TimerManager timerManager = ((InternalProcessRuntime)kruntime.getProcessRuntime()).getTimerManager();
            
            for (ProcessInstance processInstance: new ArrayList<ProcessInstance>(processInstances.values())) {
                WorkflowProcessInstance pi = ((WorkflowProcessInstance) processInstance);
    
                
                for (org.kie.api.runtime.process.NodeInstance nodeInstance : pi.getNodeInstances()) {
                    if (nodeInstance instanceof TimerNodeInstance){
                        if (((TimerNodeInstance)nodeInstance).getTimerInstance() != null) {
                            timerManager.cancelTimer(((TimerNodeInstance)nodeInstance).getTimerInstance().getId());
                        }
                    } else if (nodeInstance instanceof StateBasedNodeInstance) {
                        List<Long> timerIds = ((StateBasedNodeInstance) nodeInstance).getTimerInstances();
                        if (timerIds != null) {
                            for (Long id: timerIds) {
                                timerManager.cancelTimer(id);
                            }
                        }
                    }
                }
                
            }
        } catch (Exception e) {
            // catch everything here to make sure it will not break any following 
            // logic to allow complete clean up 
        }
    }

    @Override
    public ProcessInstance getProcessInstance(CorrelationKey correlationKey) {
        ProcessPersistenceContext context = ((ProcessPersistenceContextManager) this.kruntime.getEnvironment()
                .get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER ))
                .getProcessPersistenceContext();
        Long processInstanceId = context.getProcessInstanceByCorrelationKey(correlationKey);
        if (processInstanceId == null) {
            return null;
        }
        return getProcessInstance(processInstanceId);
    }

}
