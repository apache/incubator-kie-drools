package org.drools.persistence.processinstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.drools.common.InternalKnowledgeRuntime;
import org.drools.definition.process.Process;
import org.drools.process.instance.ProcessInstanceManager;
import org.drools.process.instance.impl.ProcessInstanceImpl;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.process.ProcessInstance;

public class JPAProcessInstanceManager
    implements
    ProcessInstanceManager {

    private InternalKnowledgeRuntime kruntime;
    private transient Map<Long, ProcessInstance> processInstances;

    public void setKnowledgeRuntime(InternalKnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
    }

    public void addProcessInstance(ProcessInstance processInstance) {
        ProcessInstanceInfo processInstanceInfo = new ProcessInstanceInfo( processInstance, this.kruntime.getEnvironment() );
        EntityManager em = (EntityManager) this.kruntime.getEnvironment().get( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER );
        em.persist( processInstanceInfo );
        //em.refresh( processInstanceInfo  );
//        em.flush();
        //em.getTransaction().commit();
        ((org.drools.process.instance.ProcessInstance) processInstance).setId( processInstanceInfo.getId() );
        processInstanceInfo.updateLastReadDate();
        internalAddProcessInstance(processInstance);
    }

    public void internalAddProcessInstance(ProcessInstance processInstance) {
    	if (this.processInstances == null) {
        	this.processInstances = new HashMap<Long, ProcessInstance>();
        }
        processInstances.put(processInstance.getId(), processInstance);
    }

    public ProcessInstance getProcessInstance(long id) {
    	org.drools.process.instance.ProcessInstance processInstance = null;
    	if (this.processInstances != null) {
	    	processInstance = (org.drools.process.instance.ProcessInstance) this.processInstances.get(id);
	    	if (processInstance != null) {
	    		return processInstance;
	    	}
    	}
    	
        EntityManager em = (EntityManager) this.kruntime.getEnvironment().get( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER );
        ProcessInstanceInfo processInstanceInfo = em.find( ProcessInstanceInfo.class,
                                                           id );
        if ( processInstanceInfo == null ) {
            return null;
        }
        processInstanceInfo.updateLastReadDate();
        processInstance = (org.drools.process.instance.ProcessInstance)
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
        return new ArrayList<ProcessInstance>();
    }

    public void removeProcessInstance(ProcessInstance processInstance) {
        EntityManager em = (EntityManager) this.kruntime.getEnvironment().get( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER );
        ProcessInstanceInfo processInstanceInfo = em.find( ProcessInstanceInfo.class,
                                                           processInstance.getId() );
        if ( processInstanceInfo != null ) {
            em.remove( processInstanceInfo );
        }
        internalRemoveProcessInstance(processInstance);
    }

    public void internalRemoveProcessInstance(ProcessInstance processInstance) {
    	if (this.processInstances != null) {
            processInstances.remove( processInstance.getId() );
        }
    }
    
    public void clearProcessInstances() {
    	if (processInstances != null) {
    		for (ProcessInstance processInstance: new ArrayList<ProcessInstance>(processInstances.values())) {
    			((ProcessInstanceImpl) processInstance).disconnect();
    		}
    	}
    }

}
