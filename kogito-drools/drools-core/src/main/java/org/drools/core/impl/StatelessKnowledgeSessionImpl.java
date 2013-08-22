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

package org.drools.core.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.drools.core.SessionConfiguration;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.command.impl.ContextImpl;
import org.drools.core.command.impl.FixedKnowledgeCommandContext;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.common.AbstractWorkingMemory;
import org.drools.core.common.AbstractWorkingMemory.WorkingMemoryReteAssertAction;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleBase;
import org.drools.core.common.WorkingMemoryFactory;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.ProcessEventSupport;
import org.drools.core.event.WorkingMemoryEventSupport;
import org.drools.core.impl.StatefulKnowledgeSessionImpl.AgendaEventListenerWrapper;
import org.drools.core.impl.StatefulKnowledgeSessionImpl.WorkingMemoryEventListenerWrapper;
import org.drools.core.reteoo.InitialFactImpl;
import org.drools.core.rule.EntryPointId;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.agent.KnowledgeAgent;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;

public class StatelessKnowledgeSessionImpl
        implements
        StatelessKnowledgeSession,
        StatelessKieSession {

    private InternalRuleBase ruleBase;
    private KnowledgeAgent   kagent;
    private MapGlobalResolver    sessionGlobals = new MapGlobalResolver();
    private Map<String, Channel> channels       = new HashMap<String, Channel>();

    /** The event mapping */
    public Map<WorkingMemoryEventListener, WorkingMemoryEventListenerWrapper> mappedWorkingMemoryListeners;
    public Map<AgendaEventListener, AgendaEventListenerWrapper>               mappedAgendaListeners;

    /** The event support */
    public AgendaEventSupport        agendaEventSupport        = new AgendaEventSupport();
    public WorkingMemoryEventSupport workingMemoryEventSupport = new WorkingMemoryEventSupport();
    public ProcessEventSupport       processEventSupport       = new ProcessEventSupport();
    private boolean initialized;

    private KieSessionConfiguration conf;
    private Environment             environment;

    private transient StatefulKnowledgeSession ksession;

    private WorkingMemoryFactory wmFactory;

    public StatelessKnowledgeSessionImpl() {
    }

    public StatelessKnowledgeSessionImpl(final InternalRuleBase ruleBase,
                                         final KnowledgeAgent kagent,
                                         final KieSessionConfiguration conf) {
        this.ruleBase = ruleBase;
        this.kagent = kagent;
        this.conf = (conf != null) ? conf : SessionConfiguration.getDefaultInstance();
        this.environment = EnvironmentFactory.newEnvironment();
        this.wmFactory = ruleBase.getConfiguration().getComponentFactory().getWorkingMemoryFactory();
    }

    public InternalRuleBase getRuleBase() {
        if (this.kagent != null) {
            // if we have an agent always get the rulebase from there
            this.ruleBase = (InternalRuleBase) ((KnowledgeBaseImpl) this.kagent.getKnowledgeBase()).ruleBase;
        }
        return this.ruleBase;
    }

    public KnowledgeAgent getKnowledgeAgent() {
        return this.kagent;
    }

    public StatefulKnowledgeSession newWorkingMemory() {
        if (ksession != null && ((StatefulKnowledgeSessionImpl)ksession).isAlive()) {
            return ksession;
        }
        if (this.kagent != null) {
            // if we have an agent always get the rulebase from there
            this.ruleBase = (InternalRuleBase) ((KnowledgeBaseImpl) this.kagent.getKnowledgeBase()).ruleBase;
        }
        this.ruleBase.readLock();
        try {
            AbstractWorkingMemory wm = (AbstractWorkingMemory) wmFactory.createWorkingMemory(this.ruleBase.nextWorkingMemoryCounter(), this.ruleBase,
                                                                                             (SessionConfiguration) this.conf, this.environment);

            // we don't pass the mapped listener wrappers to the session constructor anymore,
            // because they would be ignored anyway, since the wm already contains those listeners
            ksession = new StatefulKnowledgeSessionImpl(wm,
                                                        new KnowledgeBaseImpl(this.ruleBase));

            ((Globals) wm.getGlobalResolver()).setDelegate(this.sessionGlobals);
            if (!initialized) {
                // copy over the default generated listeners that are used for internal stuff once
                for (org.drools.core.event.AgendaEventListener listener : wm.getAgendaEventSupport().getEventListeners()) {
                    this.agendaEventSupport.addEventListener(listener);
                }
                for (org.drools.core.event.WorkingMemoryEventListener listener : wm.getWorkingMemoryEventSupport().getEventListeners()) {
                    this.workingMemoryEventSupport.addEventListener(listener);
                }
                InternalProcessRuntime processRuntime = wm.getProcessRuntime();
                if ( processRuntime != null ) {
                    for ( ProcessEventListener listener : processRuntime.getProcessEventListeners() ) {
                        this.processEventSupport.addEventListener( listener );
                    }
                }
                initialized = true;
            }
            wm.setAgendaEventSupport( this.agendaEventSupport );
            wm.setWorkingMemoryEventSupport( this.workingMemoryEventSupport );
            InternalProcessRuntime processRuntime = wm.getProcessRuntime();
            if ( processRuntime != null ) {
                processRuntime.setProcessEventSupport( this.processEventSupport );
            }
            
            for( Map.Entry<String, Channel> entry : this.channels.entrySet() ) {
                wm.registerChannel( entry.getKey(), entry.getValue() );
            }

            final InternalFactHandle handle = wm.getFactHandleFactory().newFactHandle( InitialFactImpl.getInstance(),
                                                                                       wm.getObjectTypeConfigurationRegistry().getObjectTypeConf( EntryPointId.DEFAULT,
                                                                                                                                                  InitialFactImpl.getInstance() ),
                                                                                       wm,
                                                                                       wm );

            wm.queueWorkingMemoryAction( new WorkingMemoryReteAssertAction( handle,
                                                                            false,
                                                                            true,
                                                                            null,
                                                                            null ) );
            return ksession;
        } finally {
            this.ruleBase.readUnlock();
        }
    }

    public void addEventListener(AgendaEventListener listener) {
        if ( this.mappedAgendaListeners == null ) {
            this.mappedAgendaListeners = new IdentityHashMap<AgendaEventListener, AgendaEventListenerWrapper>();
        }

        AgendaEventListenerWrapper wrapper = new AgendaEventListenerWrapper( listener );
        this.mappedAgendaListeners.put( listener,
                                        wrapper );
        this.agendaEventSupport.addEventListener( wrapper );
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        if ( this.mappedAgendaListeners == null ) {
            this.mappedAgendaListeners = new IdentityHashMap<AgendaEventListener, AgendaEventListenerWrapper>();
        }

        return Collections.unmodifiableCollection( this.mappedAgendaListeners.keySet() );
    }

    public void removeEventListener(AgendaEventListener listener) {
        if ( this.mappedAgendaListeners == null ) {
            this.mappedAgendaListeners = new IdentityHashMap<AgendaEventListener, AgendaEventListenerWrapper>();
        }

        AgendaEventListenerWrapper wrapper = this.mappedAgendaListeners.remove( listener );
        this.agendaEventSupport.removeEventListener( wrapper );
    }

    public void addEventListener(WorkingMemoryEventListener listener) {
        if ( this.mappedWorkingMemoryListeners == null ) {
            this.mappedWorkingMemoryListeners = new IdentityHashMap<WorkingMemoryEventListener, WorkingMemoryEventListenerWrapper>();
        }

        WorkingMemoryEventListenerWrapper wrapper = new WorkingMemoryEventListenerWrapper( listener );
        this.mappedWorkingMemoryListeners.put( listener,
                                               wrapper );
        this.workingMemoryEventSupport.addEventListener( wrapper );
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
        if ( this.mappedWorkingMemoryListeners == null ) {
            this.mappedWorkingMemoryListeners = new IdentityHashMap<WorkingMemoryEventListener, WorkingMemoryEventListenerWrapper>();
        }

        WorkingMemoryEventListenerWrapper wrapper = this.mappedWorkingMemoryListeners.remove( listener );
        this.workingMemoryEventSupport.removeEventListener( wrapper );
    }

    public Collection<WorkingMemoryEventListener> getWorkingMemoryEventListeners() {
        if ( this.mappedWorkingMemoryListeners == null ) {
            this.mappedWorkingMemoryListeners = new IdentityHashMap<WorkingMemoryEventListener, WorkingMemoryEventListenerWrapper>();
        }

        return Collections.unmodifiableCollection( this.mappedWorkingMemoryListeners.keySet() );
    }

    public void addEventListener(ProcessEventListener listener) {
        this.processEventSupport.addEventListener( listener );
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        return this.processEventSupport.getEventListeners();
    }

    public void removeEventListener(ProcessEventListener listener) {
        this.processEventSupport.removeEventListener( listener );
    }

    public void setGlobal(String identifier,
                          Object value) {
        this.sessionGlobals.setGlobal( identifier,
                                       value );
    }

    public Globals getGlobals() {
        return this.sessionGlobals;
    }
    
    @Override
    public void registerChannel(String name,
                                Channel channel) {
        this.channels.put( name, channel );
    }
    
    @Override
    public void unregisterChannel(String name) {
        this.channels.remove( name );
    }
    
    @Override
    public Map<String, Channel> getChannels() {
        return Collections.unmodifiableMap( this.channels );
    }

    @Override
    public KieBase getKieBase() {
        return newWorkingMemory().getKieBase();
    }

    public <T> T execute(Command<T> command) {
        newWorkingMemory();

        FixedKnowledgeCommandContext context = new FixedKnowledgeCommandContext( new ContextImpl( "ksession",
                                                                                                  null ),
                                                                                 null,
                                                                                 null,
                                                                                 ksession,
                                                                                 null );

        try {
            ((StatefulKnowledgeSessionImpl) ksession).session.startBatchExecution( new ExecutionResultImpl() );

            Object o = ((GenericCommand) command).execute( context );
            // did the user take control of fireAllRules, if not we will auto execute
            boolean autoFireAllRules = true;
            if ( command instanceof FireAllRulesCommand ) {
                autoFireAllRules = false;
            } else if ( command instanceof BatchExecutionCommandImpl ) {
                for ( Command nestedCmd : ((BatchExecutionCommandImpl) command).getCommands() ) {
                    if ( nestedCmd instanceof FireAllRulesCommand ) {
                        autoFireAllRules = false;
                        break;
                    }
                }
            }
            if ( autoFireAllRules ) {
                ksession.fireAllRules();
            }
            if ( command instanceof BatchExecutionCommandImpl ) {
                ExecutionResults result = ((StatefulKnowledgeSessionImpl) ksession).session.getExecutionResult();
                return (T) result;
            } else {
                return (T) o;
            }
        } finally {
            ((StatefulKnowledgeSessionImpl) ksession).session.endBatchExecution();
            ksession.dispose();
            ksession = null;
        }
    }

    public void execute(Object object) {
        newWorkingMemory();
        try {
            ksession.insert( object );
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
            ksession = null;
        }
    }

    public void execute(Iterable objects) {
        newWorkingMemory();
        try {
            for ( Object object : objects ) {
                ksession.insert( object );
            }
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
            ksession = null;
        }
    }

    public Environment getEnvironment() {
        return environment;
    }

}
