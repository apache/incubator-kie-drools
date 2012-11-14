package org.jbpm.persistence.processinstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.common.InternalKnowledgeRuntime;
import org.kie.definition.process.Process;
import org.drools.process.instance.WorkItem;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkflowProcessInstance;
import org.jbpm.persistence.ProcessPersistenceContext;
import org.jbpm.persistence.ProcessPersistenceContextManager;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstanceManager;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.node.StateBasedNodeInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;

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

    public void addProcessInstance(ProcessInstance processInstance) {
        ProcessInstanceInfo processInstanceInfo = new ProcessInstanceInfo( processInstance, this.kruntime.getEnvironment() );
        ProcessPersistenceContext context 
            = ((ProcessPersistenceContextManager) this.kruntime.getEnvironment()
                    .get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER ))
                    .getProcessPersistenceContext();

        context.persist( processInstanceInfo );
        ((org.jbpm.process.instance.ProcessInstance) processInstance).setId( processInstanceInfo.getId() );
        processInstanceInfo.updateLastReadDate();
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
        org.jbpm.process.instance.ProcessInstance processInstance = null;
        processInstance = (org.jbpm.process.instance.ProcessInstance) this.processInstances.get(id);
        if (processInstance != null) {
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
        processInstanceInfo.updateLastReadDate();
        processInstance = (org.jbpm.process.instance.ProcessInstance)
        	processInstanceInfo.getProcessInstance(kruntime, this.kruntime.getEnvironment());
        Process process = kruntime.getKnowledgeBase().getProcess( processInstance.getProcessId() );
        if ( process == null ) {
            throw new IllegalArgumentException( "Could not find process " + processInstance.getProcessId() );
        }
        processInstance.setProcess( process );
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
        // at this point only timers are considered as state that needs to be cleared
        TimerManager timerManager = ((InternalProcessRuntime)kruntime.getProcessRuntime()).getTimerManager();
        
        for (ProcessInstance processInstance: new ArrayList<ProcessInstance>(processInstances.values())) {
            WorkflowProcessInstance pi = ((WorkflowProcessInstance) processInstance);

            
            for (org.kie.runtime.process.NodeInstance nodeInstance : pi.getNodeInstances()) {
                if (nodeInstance instanceof StateBasedNodeInstance) {
                    List<Long> timerIds = ((StateBasedNodeInstance) nodeInstance).getTimerInstances();
                    if (timerIds != null) {
                        for (Long id: timerIds) {
                            timerManager.cancelTimer(id);
                        }
                    }
                }
            }
            
        }
    }

}
