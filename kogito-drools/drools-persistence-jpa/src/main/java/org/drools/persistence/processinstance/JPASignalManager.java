package org.drools.persistence.processinstance;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.drools.WorkingMemory;
import org.drools.command.CommandService;
import org.drools.command.runtime.process.SignalEventCommand;
import org.drools.process.instance.event.DefaultSignalManager;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.process.ProcessInstance;

public class JPASignalManager extends DefaultSignalManager {

	private CommandService commandService;

    public JPASignalManager(WorkingMemory workingMemory) {
        super(workingMemory);
    }
    
    public void setCommandService(CommandService commandService) {
    	this.commandService = commandService;
    }
    
    public void signalEvent(String type,
                            Object event) {
        for ( long id : getProcessInstancesForEvent( type ) ) {
            getWorkingMemory().getProcessInstance( id );
        }
        super.signalEvent( type,
                           event );
    }

    public void signalEvent(long processInstanceId,
                            String type,
                            Object event) {
    	SignalEventCommand command = new SignalEventCommand();
    	command.setProcessInstanceId(processInstanceId);
    	command.setEventType(type);
    	command.setEvent(event);
    	commandService.execute(command);
    }

    @SuppressWarnings("unchecked")
    private List<Long> getProcessInstancesForEvent(String type) {
        EntityManager em = (EntityManager) getWorkingMemory().getEnvironment().get( EnvironmentName.ENTITY_MANAGER );
        
        Query processInstancesForEvent = em.createNamedQuery( "ProcessInstancesWaitingForEvent" );
        processInstancesForEvent.setParameter( "type",
                                               type );
        List<Long> list = (List<Long>) processInstancesForEvent.getResultList();
        return list;
    }

}