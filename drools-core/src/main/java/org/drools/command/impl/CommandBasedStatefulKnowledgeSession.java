/*
 * Copyright 2010 JBoss Inc
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

package org.drools.command.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.KnowledgeBase;
import org.drools.command.Command;
import org.drools.command.CommandService;
import org.drools.command.ExecuteCommand;
import org.drools.command.GetSessionClockCommand;
import org.drools.command.runtime.AddEventListenerCommand;
import org.drools.command.runtime.DisposeCommand;
import org.drools.command.runtime.GetCalendarsCommand;
import org.drools.command.runtime.GetChannelsCommand;
import org.drools.command.runtime.GetEnvironmentCommand;
import org.drools.command.runtime.GetGlobalCommand;
import org.drools.command.runtime.GetGlobalsCommand;
import org.drools.command.runtime.GetIdCommand;
import org.drools.command.runtime.GetKnowledgeBaseCommand;
import org.drools.command.runtime.RegisterChannelCommand;
import org.drools.command.runtime.RegisterExitPointCommand;
import org.drools.command.runtime.RemoveEventListenerCommand;
import org.drools.command.runtime.SetGlobalCommand;
import org.drools.command.runtime.UnregisterChannelCommand;
import org.drools.command.runtime.UnregisterExitPointCommand;
import org.drools.command.runtime.process.AbortProcessInstanceCommand;
import org.drools.command.runtime.process.AbortWorkItemCommand;
import org.drools.command.runtime.process.CompleteWorkItemCommand;
import org.drools.command.runtime.process.CreateProcessInstanceCommand;
import org.drools.command.runtime.process.GetProcessEventListenersCommand;
import org.drools.command.runtime.process.GetProcessInstanceCommand;
import org.drools.command.runtime.process.GetProcessInstancesCommand;
import org.drools.command.runtime.process.GetWorkItemCommand;
import org.drools.command.runtime.process.RegisterWorkItemHandlerCommand;
import org.drools.command.runtime.process.SignalEventCommand;
import org.drools.command.runtime.process.StartProcessCommand;
import org.drools.command.runtime.process.StartProcessInstanceCommand;
import org.drools.command.runtime.rule.AgendaGroupSetFocusCommand;
import org.drools.command.runtime.rule.ClearActivationGroupCommand;
import org.drools.command.runtime.rule.ClearAgendaCommand;
import org.drools.command.runtime.rule.ClearAgendaGroupCommand;
import org.drools.command.runtime.rule.ClearRuleFlowGroupCommand;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.command.runtime.rule.FireUntilHaltCommand;
import org.drools.command.runtime.rule.GetAgendaEventListenersCommand;
import org.drools.command.runtime.rule.GetFactHandleCommand;
import org.drools.command.runtime.rule.GetFactHandlesCommand;
import org.drools.command.runtime.rule.GetObjectCommand;
import org.drools.command.runtime.rule.GetObjectsCommand;
import org.drools.command.runtime.rule.GetWorkingMemoryEntryPointCommand;
import org.drools.command.runtime.rule.GetWorkingMemoryEntryPointsCommand;
import org.drools.command.runtime.rule.GetWorkingMemoryEventListenersCommand;
import org.drools.command.runtime.rule.HaltCommand;
import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.command.runtime.rule.QueryCommand;
import org.drools.command.runtime.rule.RetractCommand;
import org.drools.command.runtime.rule.UpdateCommand;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.impl.StatefulKnowledgeSessionImpl.AgendaFilterWrapper;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemManager;
import org.drools.rule.EntryPoint;
import org.drools.runtime.Calendars;
import org.drools.runtime.Channel;
import org.drools.runtime.Environment;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.Globals;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.rule.ActivationGroup;
import org.drools.runtime.rule.Agenda;
import org.drools.runtime.rule.AgendaFilter;
import org.drools.runtime.rule.AgendaGroup;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.LiveQuery;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.RuleFlowGroup;
import org.drools.runtime.rule.ViewChangedEventListener;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.time.SessionClock;

public class CommandBasedStatefulKnowledgeSession
    implements
    StatefulKnowledgeSession {

    private CommandService            commandService;
    private transient WorkItemManager workItemManager;
    private transient Agenda          agenda;

    public CommandBasedStatefulKnowledgeSession(CommandService commandService) {
        this.commandService = commandService;
    }

    public int getId() {
        return commandService.execute( new GetIdCommand() );
    }

    public ProcessInstance getProcessInstance(long id) {
        GetProcessInstanceCommand command = new GetProcessInstanceCommand();
        command.setProcessInstanceId( id );
        return commandService.execute( command );
    }

    public void abortProcessInstance(long id) {
        AbortProcessInstanceCommand command = new AbortProcessInstanceCommand();
        command.setProcessInstanceId( id );
        commandService.execute( command );
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

                public WorkItem getWorkItem(long id) {
                    GetWorkItemCommand command = new GetWorkItemCommand();
                    command.setWorkItemId( id );
                    return commandService.execute( command );
                }

                public void clear() {
                    throw new UnsupportedOperationException();
                }

                public Set<WorkItem> getWorkItems() {
                    throw new UnsupportedOperationException();
                }

                public void internalAbortWorkItem(long id) {
                    throw new UnsupportedOperationException();
                }

                public void internalAddWorkItem(WorkItem workItem) {
                    throw new UnsupportedOperationException();
                }

                public void internalExecuteWorkItem(WorkItem workItem) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return workItemManager;
    }

    public void signalEvent(String type,
                            Object event) {
        SignalEventCommand command = new SignalEventCommand( type,
                                                             event );
        commandService.execute( command );
    }

    public void signalEvent(String type,
                            Object event,
                            long processInstanceId) {
        SignalEventCommand command = new SignalEventCommand( processInstanceId,
                                                             type,
                                                             event );
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
        command.setParameters( (HashMap<String, Object>) parameters );
        return commandService.execute( command );
    }

	public ProcessInstance createProcessInstance(String processId,
			                                     Map<String, Object> parameters) {
        CreateProcessInstanceCommand command = new CreateProcessInstanceCommand();
        command.setProcessId( processId );
        command.setParameters( (HashMap<String, Object>) parameters );
        return commandService.execute( command );
	}

	public ProcessInstance startProcessInstance(long processInstanceId) {
        StartProcessInstanceCommand command = new StartProcessInstanceCommand();
        command.setProcessInstanceId( processInstanceId );
        return commandService.execute( command );
	}

    public void dispose() {
        commandService.execute( new DisposeCommand() );
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

    public int fireAllRules(AgendaFilter agendaFilter, int max) {
        return this.commandService.execute( new FireAllRulesCommand( agendaFilter, max ) );
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

    /**
     * @deprecated Use {@link #registerChannel(String, Channel)} instead
     */
    @Deprecated
    public void registerExitPoint(String name,
                                  ExitPoint exitPoint) {
        this.commandService.execute( new RegisterExitPointCommand( name,
                                                                   exitPoint ) );
    }

    /**
     * @deprecated Use {@link #unregisterChannel(String)} instead.
     */
    @Deprecated
    public void unregisterExitPoint(String name) {
        this.commandService.execute( new UnregisterExitPointCommand( name ) );

    }

    public void registerChannel(String name,
                                Channel channel) {
        this.commandService.execute( new RegisterChannelCommand( name,
                                                                 channel ) );
    }

    public void unregisterChannel(String name) {
        this.commandService.execute( new UnregisterChannelCommand( name ) );
    }

    @SuppressWarnings("unchecked")
    public Map<String, Channel> getChannels() {
        return (Map<String, Channel>) this.commandService.execute( new GetChannelsCommand() );
    }

    public Agenda getAgenda() {
        if ( agenda == null ) {
            agenda = new Agenda() {
                public void clear() {
                    ClearAgendaCommand command = new ClearAgendaCommand();
                    commandService.execute( command );
                }

                public ActivationGroup getActivationGroup(final String name) {
                    return new ActivationGroup() {
                        public void clear() {
                            ClearActivationGroupCommand command = new ClearActivationGroupCommand();
                            command.setName( name );
                            commandService.execute( command );
                        }

                        public String getName() {
                            return name;
                        }
                    };
                }

                public AgendaGroup getAgendaGroup(final String name) {
                    return new AgendaGroup() {
                        public void clear() {
                            ClearAgendaGroupCommand command = new ClearAgendaGroupCommand();
                            command.setName( name );
                            commandService.execute( command );
                        }

                        public String getName() {
                            return name;
                        }

                        public void setFocus() {
                            AgendaGroupSetFocusCommand command = new AgendaGroupSetFocusCommand();
                            command.setName( name );
                            commandService.execute( command );
                        }
                    };
                }

                public RuleFlowGroup getRuleFlowGroup(final String name) {
                    return new RuleFlowGroup() {
                        public void clear() {
                            ClearRuleFlowGroupCommand command = new ClearRuleFlowGroupCommand();
                            command.setName( name );
                            commandService.execute( command );
                        }

                        public String getName() {
                            return name;
                        }
                    };
                }
            };
        }
        return agenda;
    }

    public FactHandle getFactHandle(Object object) {
        return this.commandService.execute( new GetFactHandleCommand( object ) );
    }

    public <T extends org.drools.runtime.rule.FactHandle> Collection<T> getFactHandles() {
        return (Collection<T>) this.commandService.execute( new GetFactHandlesCommand() );

    }

    public <T extends org.drools.runtime.rule.FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        return (Collection<T>) this.commandService.execute( new GetFactHandlesCommand( filter ) );
    }

    public Collection<Object> getObjects() {
        return getObjects( null );
    }

    public Collection<Object> getObjects(ObjectFilter filter) {
        Collection result = commandService.execute( new GetObjectsCommand( filter ) );
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T extends SessionClock> T getSessionClock() {
        return (T) this.commandService.execute( new GetSessionClockCommand() );
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        return this.commandService.execute( new GetWorkingMemoryEntryPointCommand( name ) );
    }

    public Collection< ? extends WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints() {
        return this.commandService.execute( new GetWorkingMemoryEntryPointsCommand() );
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
        commandService.execute( new AddEventListenerCommand( listener ) );
    }

    public void addEventListener(AgendaEventListener listener) {
        commandService.execute( new AddEventListenerCommand( listener ) );
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return commandService.execute( new GetAgendaEventListenersCommand() );
    }

    public Collection<WorkingMemoryEventListener> getWorkingMemoryEventListeners() {
        return commandService.execute( new GetWorkingMemoryEventListenersCommand() );
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
        commandService.execute( new RemoveEventListenerCommand( listener ) );
    }

    public void removeEventListener(AgendaEventListener listener) {
        commandService.execute( new RemoveEventListenerCommand( listener ) );
    }

    public void addEventListener(ProcessEventListener listener) {
        commandService.execute( new AddEventListenerCommand( listener ) );
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        return commandService.execute( new GetProcessEventListenersCommand() );
    }

    public void removeEventListener(ProcessEventListener listener) {
        commandService.execute( new RemoveEventListenerCommand( listener ) );
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

    public Calendars getCalendars() {
        return commandService.execute( new GetCalendarsCommand() );
    }

    public Object getObject(FactHandle factHandle) {
        return commandService.execute( new GetObjectCommand( factHandle ) );
    }

    public Environment getEnvironment() {
        return commandService.execute( new GetEnvironmentCommand() );
    }

    public <T> T execute(Command<T> command) {
        return (T) this.commandService.execute( new ExecuteCommand( command ) );
    }

    public QueryResults getQueryResults(String query,
                                        Object... arguments) {
        QueryCommand cmd = new QueryCommand( (String)null,
                                             query,
                                             arguments );
        return this.commandService.execute( cmd );
    }

    public String getEntryPointId() {
        return EntryPoint.DEFAULT.getEntryPointId();
    }

    public long getFactCount() {
        // TODO: implement this
        return 0;
    }

    public LiveQuery openLiveQuery(String query,
                                   Object[] arguments,
                                   ViewChangedEventListener listener) {
        // TODO: implement thiss        
        return null;
    }
    
    public KnowledgeSessionConfiguration getSessionConfiguration() {
        return ((KnowledgeCommandContext) commandService.getContext()).getStatefulKnowledgesession().getSessionConfiguration();
    }

}
