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

package org.drools.impl;

import org.drools.SessionConfiguration;
import org.drools.base.MapGlobalResolver;
import org.drools.command.impl.ContextImpl;
import org.drools.command.impl.FixedKnowledgeCommandContext;
import org.drools.command.impl.GenericCommand;
import org.drools.command.runtime.BatchExecutionCommandImpl;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.event.AgendaEventSupport;
import org.drools.event.ProcessEventSupport;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.impl.StatefulKnowledgeSessionImpl.AgendaEventListenerWrapper;
import org.drools.impl.StatefulKnowledgeSessionImpl.WorkingMemoryEventListenerWrapper;
import org.drools.reteoo.InitialFactImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.ReteooWorkingMemory.WorkingMemoryReteAssertAction;
import org.drools.rule.EntryPoint;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.process.InternalProcessRuntime;
import org.kie.agent.KnowledgeAgent;
import org.kie.command.Command;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.rule.AgendaEventListener;
import org.kie.event.rule.WorkingMemoryEventListener;
import org.kie.runtime.Environment;
import org.kie.runtime.ExecutionResults;
import org.kie.runtime.Globals;
import org.kie.runtime.KieSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKieSession;
import org.kie.runtime.StatelessKnowledgeSession;
import org.kie.runtime.rule.AgendaFilter;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public class StatelessKnowledgeSessionImpl
    implements
    StatelessKnowledgeSession, StatelessKieSession {

    private InternalRuleBase                                                  ruleBase;
    private KnowledgeAgent                                                    kagent;
    private AgendaFilter                                                      agendaFilter;
    private MapGlobalResolver                                                 sessionGlobals            = new MapGlobalResolver();

    /** The event mapping */
    public Map<WorkingMemoryEventListener, WorkingMemoryEventListenerWrapper> mappedWorkingMemoryListeners;
    public Map<AgendaEventListener, AgendaEventListenerWrapper>               mappedAgendaListeners;

    /** The event support */
    public AgendaEventSupport                                                 agendaEventSupport        = new AgendaEventSupport();
    public WorkingMemoryEventSupport                                          workingMemoryEventSupport = new WorkingMemoryEventSupport();
    public ProcessEventSupport                                                processEventSupport       = new ProcessEventSupport();
    private boolean                                                           initialized;

    private KieSessionConfiguration                                     conf;
    private Environment                                                       environment;
    
    public StatelessKnowledgeSessionImpl() {
    }

    public StatelessKnowledgeSessionImpl(final InternalRuleBase ruleBase,
                                         final KnowledgeAgent kagent,
                                         final KieSessionConfiguration conf) {
        this.ruleBase = ruleBase;
        this.kagent = kagent;
        this.conf = (conf != null) ? conf : SessionConfiguration.getDefaultInstance();
        this.environment = EnvironmentFactory.newEnvironment();

        if ( this.ruleBase != null ) {
            // FIXME: this same code exists in ReteooRuleBase#newStatelessSession()
            this.ruleBase.lock();
            try {
                if ( ruleBase.getConfiguration().isSequential() ) {
                    this.ruleBase.getReteooBuilder().order();
                }
            } finally {
                this.ruleBase.unlock();
            }
        }
    }

    public InternalRuleBase getRuleBase() {
        if ( this.kagent != null ) {
            // if we have an agent always get the rulebase from there
            this.ruleBase = (InternalRuleBase) ((KnowledgeBaseImpl) this.kagent.getKnowledgeBase()).ruleBase;
        }
        return this.ruleBase;
    }
    
    public KnowledgeAgent getKnowledgeAgent() {
        return this.kagent;
    }

    public StatefulKnowledgeSession newWorkingMemory() {
        if ( this.kagent != null ) {
            // if we have an agent always get the rulebase from there
            this.ruleBase = (InternalRuleBase) ((KnowledgeBaseImpl) this.kagent.getKnowledgeBase()).ruleBase;
        }
        this.ruleBase.readLock();
        try {
            ReteooWorkingMemory wm = new ReteooWorkingMemory( this.ruleBase.nextWorkingMemoryCounter(),
                                                              this.ruleBase,
                                                              (SessionConfiguration) this.conf,
                                                              this.environment );

            // we don't pass the mapped listener wrappers to the session constructor anymore,
            // because they would be ignored anyway, since the wm already contains those listeners
            StatefulKnowledgeSessionImpl ksession = new StatefulKnowledgeSessionImpl( wm,
                                                                                      new KnowledgeBaseImpl( this.ruleBase ) );

            ((Globals) wm.getGlobalResolver()).setDelegate( this.sessionGlobals );
            if (!initialized) {
            	// copy over the default generated listeners that are used for internal stuff once
            	for (org.drools.event.AgendaEventListener listener: wm.getAgendaEventSupport().getEventListeners()) {
            		this.agendaEventSupport.addEventListener(listener);
            	}
                for (org.drools.event.WorkingMemoryEventListener listener: wm.getWorkingMemoryEventSupport().getEventListeners()) {
                	this.workingMemoryEventSupport.addEventListener(listener);
                }
                InternalProcessRuntime processRuntime = wm.getProcessRuntime();
                if (processRuntime != null) {
                	for (ProcessEventListener listener: processRuntime.getProcessEventListeners()) {
                		this.processEventSupport.addEventListener(listener);
                	}
                }
                initialized = true;
            }
            wm.setAgendaEventSupport( this.agendaEventSupport );
            wm.setWorkingMemoryEventSupport( this.workingMemoryEventSupport );
            InternalProcessRuntime processRuntime = wm.getProcessRuntime();
            if (processRuntime != null) {
                processRuntime.setProcessEventSupport( this.processEventSupport );
            }

            final InternalFactHandle handle =  wm.getFactHandleFactory().newFactHandle( InitialFactImpl.getInstance(),
                                                                                        wm.getObjectTypeConfigurationRegistry().getObjectTypeConf( EntryPoint.DEFAULT,
                                                                                                                                                   InitialFactImpl.getInstance() ),
                                                                                        wm,
                                                                                        wm);

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
        this.processEventSupport.addEventListener(listener);
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        return this.processEventSupport.getEventListeners();
    }

    public void removeEventListener(ProcessEventListener listener) {
        this.processEventSupport.removeEventListener(listener);
    }

    public void setGlobal(String identifier,
                          Object value) {
        this.sessionGlobals.setGlobal( identifier,
                                       value );
    }

    public Globals getGlobals() {
        return this.sessionGlobals;
    }

    public <T> T execute(Command<T> command) {
        StatefulKnowledgeSession ksession = newWorkingMemory();

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
                ksession.fireAllRules( );
            }
            if ( command instanceof BatchExecutionCommandImpl) {
                ExecutionResults result = ((StatefulKnowledgeSessionImpl) ksession).session.getExecutionResult();
                return (T) result;
            } else {
                return (T) o;
            }
        } finally {
            ((StatefulKnowledgeSessionImpl) ksession).session.endBatchExecution();
            ksession.dispose();
        }
    }

    public void execute(Object object) {
        StatefulKnowledgeSession ksession = newWorkingMemory();

        ksession.insert( object );
        ksession.fireAllRules( );
        ksession.dispose();
    }

    public void execute(Iterable objects) {
        StatefulKnowledgeSession ksession = newWorkingMemory();

        for ( Object object : objects ) {
            ksession.insert( object );
        }
        ksession.fireAllRules( );
        ksession.dispose();
    }
    
    public Environment getEnvironment() {
        return environment;
    }

}
