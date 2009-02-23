package org.drools.process.command.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.process.command.AbortWorkItemCommand;
import org.drools.process.command.CommandService;
import org.drools.process.command.CompleteWorkItemCommand;
import org.drools.process.command.FireAllRulesCommand;
import org.drools.process.command.FireUntilHaltCommand;
import org.drools.process.command.GetAgendaCommand;
import org.drools.process.command.GetEnvironmentCommand;
import org.drools.process.command.GetFactHandleCommand;
import org.drools.process.command.GetGlobalCommand;
import org.drools.process.command.GetGlobalsCommand;
import org.drools.process.command.GetKnowledgeBaseCommand;
import org.drools.process.command.GetObjectCommand;
import org.drools.process.command.GetObjectsCommand;
import org.drools.process.command.GetProcessInstanceCommand;
import org.drools.process.command.GetProcessInstancesCommand;
import org.drools.process.command.GetSessionClockCommand;
import org.drools.process.command.GetWorkingMemoryEntryPointCommand;
import org.drools.process.command.HaltCommand;
import org.drools.process.command.InsertObjectCommand;
import org.drools.process.command.RegisterWorkItemHandlerCommand;
import org.drools.process.command.RetractCommand;
import org.drools.process.command.SetGlobalCommand;
import org.drools.process.command.SignalEventCommand;
import org.drools.process.command.StartProcessCommand;
import org.drools.process.command.UpdateCommand;
import org.drools.runtime.Environment;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.Globals;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.rule.Agenda;
import org.drools.runtime.rule.AgendaFilter;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.time.SessionClock;

public class CommandBasedStatefulKnowledgeSession
    implements
    StatefulKnowledgeSession {

    private CommandService            commandService;
    private transient WorkItemManager workItemManager;

    public CommandBasedStatefulKnowledgeSession(CommandService commandService) {
        this.commandService = commandService;
    }

    public ProcessInstance getProcessInstance(long id) {
        GetProcessInstanceCommand command = new GetProcessInstanceCommand();
        command.setProcessInstanceId( id );
        return commandService.execute( command );
    }

    public CommandService getCommandService() {
        return commandService;
    }

    public Collection<ProcessInstance> getProcessInstances() {
        return this.commandService.execute( new GetProcessInstancesCommand() );
    }

    public WorkItemManager getWorkItemManager() {
        if ( workItemManager == null ) {
            workItemManager = new WorkItemManager() {
                public void completeWorkItem(long id,
                                             Map<String, Object> results) {
                    CompleteWorkItemCommand command = new CompleteWorkItemCommand();
                    command.setWorkItemId( id );
                    command.setResults( results );
                    commandService.execute( command );
                }

                public void abortWorkItem(long id) {
                    AbortWorkItemCommand command = new AbortWorkItemCommand();
                    command.setWorkItemId( id );
                    commandService.execute( command );
                }

                public void registerWorkItemHandler(String workItemName,
                                                    WorkItemHandler handler) {
                    RegisterWorkItemHandlerCommand command = new RegisterWorkItemHandlerCommand();
                    command.setWorkItemName( workItemName );
                    command.setHandler( handler );
                    commandService.execute( command );
                }
            };
        }
        return workItemManager;
    }

    public void signalEvent(String type,
                            Object event) {
        SignalEventCommand command = new SignalEventCommand();
        command.setEventType( type );
        command.setEvent( event );
        commandService.execute( command );
    }

    public ProcessInstance startProcess(String processId) {
        return startProcess( processId,
                             null );
    }

    public ProcessInstance startProcess(String processId,
                                        Map<String, Object> parameters) {
        StartProcessCommand command = new StartProcessCommand();
        command.setProcessId( processId );
        command.setParameters( parameters );
        return commandService.execute( command );
    }

    public void dispose() {
        commandService.dispose();
    }

    public int fireAllRules() {
        return this.commandService.execute( new FireAllRulesCommand() );
    }

    public int fireAllRules(int max) {
        return this.commandService.execute( new FireAllRulesCommand( max ) );
    }

    public int fireAllRules(AgendaFilter agendaFilter) {
        return this.commandService.execute( new FireAllRulesCommand( agendaFilter ) );
    }

    public void fireUntilHalt() {
        this.commandService.execute( new FireUntilHaltCommand() );
    }

    public void fireUntilHalt(AgendaFilter agendaFilter) {
        this.commandService.execute( new FireUntilHaltCommand( agendaFilter ) );
    }

    public KnowledgeBase getKnowledgeBase() {
        return this.commandService.execute( new GetKnowledgeBaseCommand() );
    }

    public void registerExitPoint(String name,
                                  ExitPoint exitPoint) {
        throw new UnsupportedOperationException();

    }

    public void unregisterExitPoint(String name) {
        throw new UnsupportedOperationException();

    }

    public Agenda getAgenda() {
        return this.commandService.execute( new GetAgendaCommand() );
    }

    public FactHandle getFactHandle(Object object) {
        return this.commandService.execute( new GetFactHandleCommand( object ) );
    }

    public Collection< ? extends FactHandle> getFactHandles() {
        throw new UnsupportedOperationException();

    }

    public Collection< ? extends FactHandle> getFactHandles(ObjectFilter filter) {
        throw new UnsupportedOperationException();
    }

    public Collection< ? > getObjects() {
        Collection<Object> result = new ArrayList<Object>();
        Iterator< ? > iterator = commandService.execute( new GetObjectsCommand() );
        if ( iterator != null ) {
            while ( iterator.hasNext() ) {
                result.add( iterator.next() );
            }
        }
        return result;
    }

    public Collection< ? > getObjects(ObjectFilter filter) {
        Collection<Object> result = new ArrayList<Object>();
        Iterator< ? > iterator = commandService.execute( new GetObjectsCommand( filter ) );
        if ( iterator != null ) {
            while ( iterator.hasNext() ) {
                result.add( iterator.next() );
            }
        }
        return result;
    }

    public SessionClock getSessionClock() {
        return this.commandService.execute( new GetSessionClockCommand() );
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        return this.commandService.execute( new GetWorkingMemoryEntryPointCommand( name ) );
    }

    public void halt() {
        this.commandService.execute( new HaltCommand() );
    }

    public FactHandle insert(Object object) {
        return commandService.execute( new InsertObjectCommand( object ) );
    }

    public void retract(FactHandle handle) {
        commandService.execute( new RetractCommand( handle ) );
    }

    public void update(FactHandle handle,
                       Object object) {
        commandService.execute( new UpdateCommand( handle,
                                                   object ) );
    }

    public void addEventListener(WorkingMemoryEventListener listener) {
        throw new UnsupportedOperationException();
    }

    public void addEventListener(AgendaEventListener listener) {
        throw new UnsupportedOperationException();
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        throw new UnsupportedOperationException();
    }

    public Collection<WorkingMemoryEventListener> getWorkingMemoryEventListeners() {
        throw new UnsupportedOperationException();
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
        throw new UnsupportedOperationException();
    }

    public void removeEventListener(AgendaEventListener listener) {
        throw new UnsupportedOperationException();
    }

    public void addEventListener(ProcessEventListener listener) {
        throw new UnsupportedOperationException();
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        throw new UnsupportedOperationException();
    }

    public void removeEventListener(ProcessEventListener listener) {
        throw new UnsupportedOperationException();
    }

    public Object getGlobal(String identifier) {
        return commandService.execute( new GetGlobalCommand( identifier ) );
    }

    public void setGlobal(String identifier,
                          Object object) {
        this.commandService.execute( new SetGlobalCommand( identifier,
                                                           object ) );
    }

    public Globals getGlobals() {
        return commandService.execute( new GetGlobalsCommand() );
    }

    public Object getObject(FactHandle factHandle) {
        return commandService.execute( new GetObjectCommand( factHandle ) );
    }

    public Environment getEnvironment() {
        return commandService.execute( new GetEnvironmentCommand() );
    }

}