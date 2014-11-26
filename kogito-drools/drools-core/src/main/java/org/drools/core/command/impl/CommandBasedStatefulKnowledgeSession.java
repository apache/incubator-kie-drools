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

package org.drools.core.command.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.core.command.CommandService;
import org.drools.core.command.GetSessionClockCommand;
import org.drools.core.command.Interceptor;
import org.drools.core.command.runtime.*;
import org.drools.core.command.runtime.process.AbortProcessInstanceCommand;
import org.drools.core.command.runtime.process.AbortWorkItemCommand;
import org.drools.core.command.runtime.process.CompleteWorkItemCommand;
import org.drools.core.command.runtime.process.CreateCorrelatedProcessInstanceCommand;
import org.drools.core.command.runtime.process.CreateProcessInstanceCommand;
import org.drools.core.command.runtime.process.GetProcessEventListenersCommand;
import org.drools.core.command.runtime.process.GetProcessInstanceByCorrelationKeyCommand;
import org.drools.core.command.runtime.process.GetProcessInstanceCommand;
import org.drools.core.command.runtime.process.GetProcessInstancesCommand;
import org.drools.core.command.runtime.process.GetWorkItemCommand;
import org.drools.core.command.runtime.process.RegisterWorkItemHandlerCommand;
import org.drools.core.command.runtime.process.SignalEventCommand;
import org.drools.core.command.runtime.process.StartCorrelatedProcessCommand;
import org.drools.core.command.runtime.process.StartProcessCommand;
import org.drools.core.command.runtime.process.StartProcessInstanceCommand;
import org.drools.core.command.runtime.rule.AgendaGroupSetFocusCommand;
import org.drools.core.command.runtime.rule.ClearActivationGroupCommand;
import org.drools.core.command.runtime.rule.ClearAgendaCommand;
import org.drools.core.command.runtime.rule.ClearAgendaGroupCommand;
import org.drools.core.command.runtime.rule.ClearRuleFlowGroupCommand;
import org.drools.core.command.runtime.rule.DeleteCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.FireUntilHaltCommand;
import org.drools.core.command.runtime.rule.GetAgendaEventListenersCommand;
import org.drools.core.command.runtime.rule.GetEntryPointCommand;
import org.drools.core.command.runtime.rule.GetEntryPointsCommand;
import org.drools.core.command.runtime.rule.GetFactHandleCommand;
import org.drools.core.command.runtime.rule.GetFactHandlesCommand;
import org.drools.core.command.runtime.rule.GetObjectCommand;
import org.drools.core.command.runtime.rule.GetObjectsCommand;
import org.drools.core.command.runtime.rule.GetRuleRuntimeEventListenersCommand;
import org.drools.core.command.runtime.rule.HaltCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.drools.core.command.runtime.rule.QueryCommand;
import org.drools.core.command.runtime.rule.UpdateCommand;
import org.drools.core.impl.AbstractRuntime;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.rule.EntryPointId;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.internal.KnowledgeBase;
import org.kie.api.command.Command;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.ObjectFilter;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.rule.ActivationGroup;
import org.kie.api.runtime.rule.Agenda;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.AgendaGroup;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.RuleFlowGroup;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionClock;

public class CommandBasedStatefulKnowledgeSession extends AbstractRuntime
    implements
    StatefulKnowledgeSession, CorrelationAwareProcessRuntime {

    private CommandService            commandService;
    private transient WorkItemManager workItemManager;
    private transient Agenda          agenda;

    public CommandBasedStatefulKnowledgeSession(CommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * Deprecated use {@link #getIdentifier()} instead
     */
    @Deprecated
    public int getId() {
        return commandService.execute( new GetIdCommand() ).intValue();
    }
    public long getIdentifier() {
        return commandService.execute( new GetIdCommand() );
    }

    public ProcessInstance getProcessInstance(long id) {
        GetProcessInstanceCommand command = new GetProcessInstanceCommand();
        command.setProcessInstanceId( id );
        return commandService.execute( command );
    }

    public ProcessInstance getProcessInstance(long id, boolean readOnly) {
        GetProcessInstanceCommand command = new GetProcessInstanceCommand();
        command.setProcessInstanceId( id );
        command.setReadOnly( readOnly );
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

                @Override
                public void signalEvent(String type, Object event) {
                    SignalEventCommand command = new SignalEventCommand(type, event);
                    commandService.execute(command);
                }

                @Override
                public void signalEvent(String type, Object event, long processInstanceId) {
                    SignalEventCommand command = new SignalEventCommand(processInstanceId, type, event);
                    commandService.execute(command);
                }

                @Override
                public void dispose() {
                    // no-op
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
        command.setParameters( parameters );
        return commandService.execute( command );
    }

	public ProcessInstance createProcessInstance(String processId,
			                                     Map<String, Object> parameters) {
        CreateProcessInstanceCommand command = new CreateProcessInstanceCommand();
        command.setProcessId( processId );
        command.setParameters( parameters );
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

    public void destroy() {
        commandService.execute( new DestroySessionCommand(commandService));
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

    public KnowledgeBase getKieBase() {
        return this.commandService.execute( new GetKnowledgeBaseCommand() );
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

    public <T extends FactHandle> Collection<T> getFactHandles() {
        return (Collection<T>) this.commandService.execute( new GetFactHandlesCommand() );

    }

    public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        return (Collection<T>) this.commandService.execute( new GetFactHandlesCommand( filter ) );
    }

    public Collection<? extends Object> getObjects() {
        return getObjects( null );
    }

    public Collection<? extends Object> getObjects(ObjectFilter filter) {
        Collection result = commandService.execute( new GetObjectsCommand( filter ) );
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T extends SessionClock> T getSessionClock() {
        return (T) this.commandService.execute( new GetSessionClockCommand() );
    }

    public EntryPoint getEntryPoint(String name) {
        return this.commandService.execute( new GetEntryPointCommand( name ) );
    }

    public Collection< ? extends EntryPoint> getEntryPoints() {
        return this.commandService.execute( new GetEntryPointsCommand() );
    }

    public void halt() {
        this.commandService.execute( new HaltCommand() );
    }

    public FactHandle insert(Object object) {
        return commandService.execute( new InsertObjectCommand( object ) );
    }

    public void retract(FactHandle handle) {
        commandService.execute( new DeleteCommand( handle ) );
    }

    public void delete(FactHandle handle) {
        commandService.execute( new DeleteCommand( handle ) );
    }

    public void update(FactHandle handle,
                       Object object) {
        commandService.execute( new UpdateCommand( handle,
                                                   object ) );
    }

    public void addEventListener(RuleRuntimeEventListener listener) {
        commandService.execute(new AddEventListenerCommand(listener));
    }

    public void addEventListener(AgendaEventListener listener) {
        commandService.execute( new AddEventListenerCommand( listener ) );
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return commandService.execute( new GetAgendaEventListenersCommand() );
    }

    public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
        return commandService.execute( new GetRuleRuntimeEventListenersCommand() );
    }

    public void removeEventListener(RuleRuntimeEventListener listener) {
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
        return (T) this.commandService.execute( command );
    }

    public QueryResults getQueryResults(String query,
                                        Object... arguments) {
        QueryCommand cmd = new QueryCommand( (String)null,
                                             query,
                                             arguments );
        return this.commandService.execute( cmd );
    }

    public String getEntryPointId() {
        return EntryPointId.DEFAULT.getEntryPointId();
    }

    public long getFactCount() {
        return commandService.execute( new GetFactCountCommand() );
    }

    public LiveQuery openLiveQuery(String query,
                                   Object[] arguments,
                                   ViewChangedEventListener listener) {
        // TODO: implement thiss        
        return null;
    }
    
    public KieSessionConfiguration getSessionConfiguration() {
        return ((KnowledgeCommandContext) commandService.getContext()).getKieSession().getSessionConfiguration();
    }

    public void addInterceptor(Interceptor interceptor) {
    	interceptor.setNext(this.commandService);
    	this.commandService = interceptor;
    }

    @Override
    public ProcessInstance startProcess(String processId,
            CorrelationKey correlationKey, Map<String, Object> parameters) {
        
        return this.commandService.execute(new StartCorrelatedProcessCommand(processId, correlationKey, parameters));
    }

    @Override
    public ProcessInstance createProcessInstance(String processId,
            CorrelationKey correlationKey, Map<String, Object> parameters) {
        
        return this.commandService.execute(
                new CreateCorrelatedProcessInstanceCommand(processId, correlationKey, parameters));
    }

    @Override
    public ProcessInstance getProcessInstance(CorrelationKey correlationKey) {
        
        return this.commandService.execute(new GetProcessInstanceByCorrelationKeyCommand(correlationKey));
    }
}
