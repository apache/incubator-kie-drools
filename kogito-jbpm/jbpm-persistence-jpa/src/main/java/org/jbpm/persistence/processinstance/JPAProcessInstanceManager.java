package org.jbpm.persistence.processinstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.common.InternalKnowledgeRuntime;
import org.drools.definition.process.Process;
import org.drools.persistence.TransactionManager;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.persistence.ProcessPersistenceContext;
import org.jbpm.persistence.ProcessPersistenceContextManager;
import org.jbpm.process.instance.ProcessInstanceManager;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;

/**
 * This is an implementation of the {@link ProcessInstanceManager} that uses JPA.
 * </p>
 * Currently (11/2011), the implementation is not totally thread-safe, but there haven't any issues yet. 
 */
public class JPAProcessInstanceManager
    implements
    ProcessInstanceManager {

    private InternalKnowledgeRuntime kruntime;
    private transient ConcurrentHashMap<Long, ProcessInstance> processInstances;

    public void setKnowledgeRuntime(InternalKnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
    }

    public void addProcessInstance(ProcessInstance processInstance) {
        ProcessInstanceInfo processInstanceInfo = new ProcessInstanceInfo( processInstance, this.kruntime.getEnvironment() );
        ProcessPersistenceContext context 
            = ((ProcessPersistenceContextManager) this.kruntime.getEnvironment()
                    .get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER ))
                    .getProcessPersistenceContext();
        // @PrePersist added to ProcessInstanceInfo because of this
        context.persist( processInstanceInfo );
        ((org.jbpm.process.instance.ProcessInstance) processInstance).setId( processInstanceInfo.getId() );
        processInstanceInfo.updateLastReadDate();
        internalAddProcessInstance(processInstance);
    }
    
    public void internalAddProcessInstance(ProcessInstance processInstance) {
        synchronized (this) {
            if(this.processInstances == null ) { 
                this.processInstances = new ConcurrentHashMap<Long, ProcessInstance>();
            }
        }
        if( processInstances.putIfAbsent(processInstance.getId(), processInstance) != null ) { 
            throw new ConcurrentModificationException(
                    "Duplicate process instance [" + processInstance.getProcessId() + "/" + processInstance.getId() + "]"
                    + " added to process instance manager." );
        }
    }

    public ProcessInstance getProcessInstance(long id) {
    	org.jbpm.process.instance.ProcessInstance processInstance = null;
    	if (getProcessInstancesMap() != null) {
	    	processInstance = (org.jbpm.process.instance.ProcessInstance) this.processInstances.get(id);
	    	if (processInstance != null) {
	    		return processInstance;
	    	}
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
        processInstanceInfo.updateLastReadDate();
        processInstance = (org.jbpm.process.instance.ProcessInstance)
        	processInstanceInfo.getProcessInstance(kruntime, this.kruntime.getEnvironment());
        Process process = kruntime.getKnowledgeBase().getProcess( processInstance.getProcessId() );
        if ( process == null ) {
            throw new IllegalArgumentException( "Could not find process " + processInstance.getProcessId() );
        }
        processInstance.setProcess( process );
        if ( processInstance.getKnowledgeRuntime() == null ) {
            processInstance.setKnowledgeRuntime( kruntime );
            ((ProcessInstanceImpl) processInstance).reconnect();
        }
        return processInstance;
    }

    public Collection<ProcessInstance> getProcessInstances() {
        return Collections.unmodifiableCollection(getProcessInstancesMap().values());
    }

    private Map<Long, ProcessInstance> getProcessInstancesMap() {
        Map<Long, ProcessInstance> processInstancesMap = null;
        synchronized(this) { 
            processInstancesMap = this.processInstances;
        }
        return processInstancesMap;
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
    	if (getProcessInstancesMap() != null) {
            processInstances.remove( processInstance.getId() );
        }
    }
    
    public void clearProcessInstances() {
    	if (getProcessInstancesMap() != null) {
    		for (ProcessInstance processInstance: new ArrayList<ProcessInstance>(processInstances.values())) {
    			((ProcessInstanceImpl) processInstance).disconnect();
    		}
    	}
    }

}
