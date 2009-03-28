package org.drools.persistence.processinstance;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.drools.WorkingMemory;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.process.core.Process;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.ProcessInstanceManager;
import org.drools.process.instance.impl.ProcessInstanceImpl;
import org.drools.runtime.EnvironmentName;

public class JPAProcessInstanceManager
    implements
    ProcessInstanceManager {

    private WorkingMemory workingMemory;

    public void setWorkingMemory(WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public void addProcessInstance(ProcessInstance processInstance) {
        ProcessInstanceInfo processInstanceInfo = new ProcessInstanceInfo( processInstance );
        EntityManager em = (EntityManager) this.workingMemory.getEnvironment().get( EnvironmentName.ENTITY_MANAGER );
        em.persist( processInstanceInfo );
        ((ProcessInstance) processInstance).setId( processInstanceInfo.getId() );
        processInstanceInfo.updateLastReadDate();
    }

    public void internalAddProcessInstance(ProcessInstance processInstance) {
    }

    public ProcessInstance getProcessInstance(long id) {
        EntityManager em = (EntityManager) this.workingMemory.getEnvironment().get( EnvironmentName.ENTITY_MANAGER );
        ProcessInstanceInfo processInstanceInfo = em.find( ProcessInstanceInfo.class,
                                                           id );
        if ( processInstanceInfo == null ) {
            return null;
        }
        processInstanceInfo.updateLastReadDate();
        ProcessInstance processInstance = (ProcessInstance)
        	processInstanceInfo.getProcessInstance(workingMemory);
        Process process = ((InternalRuleBase) workingMemory.getRuleBase()).getProcess( processInstance.getProcessId() );
        if ( process == null ) {
            throw new IllegalArgumentException( "Could not find process " + processInstance.getProcessId() );
        }
        processInstance.setProcess( process );
        if ( processInstance.getWorkingMemory() == null ) {
            processInstance.setWorkingMemory( (InternalWorkingMemory) workingMemory );
            ((ProcessInstanceImpl) processInstance).reconnect();
        }
        return processInstance;
    }

    public Collection<ProcessInstance> getProcessInstances() {
        return new ArrayList<ProcessInstance>();
    }

    public void removeProcessInstance(ProcessInstance processInstance) {
        EntityManager em = (EntityManager) this.workingMemory.getEnvironment().get( EnvironmentName.ENTITY_MANAGER );
        ProcessInstanceInfo processInstanceInfo = em.find( ProcessInstanceInfo.class,
                                                           processInstance.getId() );
        if ( processInstanceInfo != null ) {
            em.remove( processInstanceInfo );
        }
    }

    public void internalRemoveProcessInstance(ProcessInstance processInstance) {
    }

}
