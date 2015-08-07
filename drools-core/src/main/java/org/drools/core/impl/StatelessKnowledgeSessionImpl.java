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

import org.drools.core.SessionConfiguration;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.command.impl.ContextImpl;
import org.drools.core.command.impl.FixedKnowledgeCommandContext;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.WorkingMemoryFactory;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.ProcessEventSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.agent.KnowledgeAgent;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatelessKnowledgeSessionImpl extends AbstractRuntime
        implements
        StatelessKnowledgeSession,
        StatelessKieSession {

    private InternalKnowledgeBase kBase;
    private KnowledgeAgent   kagent;
    private MapGlobalResolver    sessionGlobals = new MapGlobalResolver();
    private Map<String, Channel> channels       = new HashMap<String, Channel>();

    /** The event mapping */
    public Set<RuleRuntimeEventListener>          cachedRuleRuntimeListeners;
    public Set<AgendaEventListener>               cachedAgendaListeners;
    public Set<ProcessEventListener>              cachedProcessEventListener;

    /** The event support */
    private AgendaEventSupport        agendaEventSupport        = new AgendaEventSupport();
    private RuleRuntimeEventSupport   ruleRuntimeEventSupport = new RuleRuntimeEventSupport();
    private ProcessEventSupport       processEventSupport       = new ProcessEventSupport();

    private KieSessionConfiguration conf;
    private Environment             environment;

    private WorkingMemoryFactory wmFactory;

    public StatelessKnowledgeSessionImpl() {
    }

    public StatelessKnowledgeSessionImpl(final InternalKnowledgeBase kBase,
                                         final KnowledgeAgent kagent,
                                         final KieSessionConfiguration conf) {
        this.kBase = kBase;
        this.kagent = kagent;
        this.conf = (conf != null) ? conf : SessionConfiguration.getDefaultInstance();
        this.environment = EnvironmentFactory.newEnvironment();
        this.wmFactory = kBase.getConfiguration().getComponentFactory().getWorkingMemoryFactory();
    }

    public InternalKnowledgeBase getKnowledgeBase() {
        if (this.kagent != null) {
            // if we have an agent always get the rulebase from there
            this.kBase = (InternalKnowledgeBase) this.kagent.getKnowledgeBase();
        }
        return this.kBase;
    }

    public KnowledgeAgent getKnowledgeAgent() {
        return this.kagent;
    }

    public StatefulKnowledgeSession newWorkingMemory() {
        if (this.kagent != null) {
            // if we have an agent always get the rulebase from there
            this.kBase = (InternalKnowledgeBase) this.kagent.getKnowledgeBase();
        }
        this.kBase.readLock();
        try {
            StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) wmFactory.createWorkingMemory(this.kBase.nextWorkingMemoryCounter(),
                                                                                                         this.kBase,
                                                                                                         (SessionConfiguration) this.conf,
                                                                                                         this.environment);
            StatefulKnowledgeSessionImpl ksessionImpl = (StatefulKnowledgeSessionImpl) ksession;

            // we don't pass the mapped listener wrappers to the session constructor anymore,
            // because they would be ignored anyway, since the wm already contains those listeners

            ((Globals) ksessionImpl.getGlobalResolver()).setDelegate(this.sessionGlobals);

            // copy over the default generated listeners that are used for internal stuff once
            registerSystemListeners(ksessionImpl);
            registerCustomListeners();

            ksessionImpl.setAgendaEventSupport( this.agendaEventSupport );
            ksessionImpl.setRuleRuntimeEventSupport(this.ruleRuntimeEventSupport);
            InternalProcessRuntime processRuntime = ksessionImpl.internalGetProcessRuntime();
            if ( processRuntime != null ) {
                processRuntime.setProcessEventSupport( this.processEventSupport );
            }

            for( Map.Entry<String, Channel> entry : this.channels.entrySet() ) {
                ksession.registerChannel( entry.getKey(), entry.getValue() );
            }

//            final InternalFactHandle handle = wm.getFactHandleFactory().newFactHandle( InitialFactImpl.getInstance(),
//                                                                                       wm.getObjectTypeConfigurationRegistry().getObjectTypeConf( EntryPointId.DEFAULT,
//                                                                                                                                                  InitialFactImpl.getInstance() ),
//                                                                                       wm,
//                                                                                       wm );
//
//            wm.queueWorkingMemoryAction( new WorkingMemoryReteAssertAction( handle,
//                                                                            false,
//                                                                            true,
//                                                                            null,
//                                                                            null ) );

            return ksession;
        } finally {
            this.kBase.readUnlock();
        }
    }

    private void registerSystemListeners(StatefulKnowledgeSessionImpl wm) {
        for (AgendaEventListener listener : wm.getAgendaEventSupport().getEventListeners()) {
            this.agendaEventSupport.addEventListener(listener);
        }
        for (RuleRuntimeEventListener listener : wm.getRuleRuntimeEventSupport().getEventListeners()) {
            this.ruleRuntimeEventSupport.addEventListener(listener);
        }
        InternalProcessRuntime processRuntime = wm.internalGetProcessRuntime();
        if ( processRuntime != null ) {
            for ( ProcessEventListener listener : processRuntime.getProcessEventListeners() ) {
                this.processEventSupport.addEventListener( listener );
            }
        }
    }

    private void registerCustomListeners() {
        if ( cachedAgendaListeners != null ) {
            for (AgendaEventListener agendaListener : cachedAgendaListeners) {
                this.agendaEventSupport.addEventListener( agendaListener );
            }
        }
        if ( cachedRuleRuntimeListeners != null ) {
            for (RuleRuntimeEventListener wmListener : cachedRuleRuntimeListeners) {
                this.ruleRuntimeEventSupport.addEventListener(wmListener);
            }
        }
        if ( cachedProcessEventListener != null ) {
            for (ProcessEventListener processListener : cachedProcessEventListener) {
                this.processEventSupport.addEventListener( processListener );
            }
        }
    }

    public void addEventListener(AgendaEventListener listener) {
        registerAgendaEventListener(listener);
    }

    private void registerAgendaEventListener(AgendaEventListener listener) {
        if ( this.cachedAgendaListeners == null ) {
            this.cachedAgendaListeners = new HashSet<AgendaEventListener>();
        }
        this.cachedAgendaListeners.add(listener);
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return cachedAgendaListeners != null ? Collections.unmodifiableCollection( cachedAgendaListeners ) : Collections.<AgendaEventListener>emptySet();
    }

    public void removeEventListener(AgendaEventListener listener) {
        if ( this.cachedAgendaListeners != null ) {
            cachedAgendaListeners.remove( listener );
            this.agendaEventSupport.removeEventListener( listener );
        }
    }

    public void addEventListener(RuleRuntimeEventListener listener) {
        registerRuleRuntimeEventListener(listener);
    }

    private void registerRuleRuntimeEventListener(RuleRuntimeEventListener listener) {
        if ( this.cachedRuleRuntimeListeners == null ) {
            this.cachedRuleRuntimeListeners = new HashSet<RuleRuntimeEventListener>();
        }
        this.cachedRuleRuntimeListeners.add(listener);
    }

    public void removeEventListener(RuleRuntimeEventListener listener) {
        if ( this.cachedRuleRuntimeListeners != null ) {
            this.ruleRuntimeEventSupport.removeEventListener(listener);
        }
    }

    public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
        if ( this.cachedRuleRuntimeListeners == null ) {
            this.cachedRuleRuntimeListeners = new HashSet<RuleRuntimeEventListener>();
        }

        return Collections.unmodifiableCollection( this.cachedRuleRuntimeListeners );
    }

    public void addEventListener(ProcessEventListener listener) {
        if ( this.cachedProcessEventListener == null ) {
            this.cachedProcessEventListener = new HashSet<ProcessEventListener>();
        }
        this.cachedProcessEventListener.add(listener);
        this.processEventSupport.addEventListener(listener);
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        return Collections.unmodifiableCollection( this.cachedProcessEventListener );
    }

    public void removeEventListener(ProcessEventListener listener) {
        if (this.cachedProcessEventListener != null) {
            this.cachedProcessEventListener.remove(listener);
        }
        this.processEventSupport.removeEventListener( listener );
    }

    public void setGlobal(String identifier,
                          Object value) {
        this.sessionGlobals.setGlobal(identifier,
                                      value);
    }

    public Globals getGlobals() {
        return this.sessionGlobals;
    }
    
    @Override
    public void registerChannel(String name,
                                Channel channel) {
        this.channels.put(name, channel);
    }
    
    @Override
    public void unregisterChannel(String name) {
        this.channels.remove(name);
    }
    
    @Override
    public Map<String, Channel> getChannels() {
        return Collections.unmodifiableMap( this.channels );
    }

    @Override
    public KieBase getKieBase() {
        return getKnowledgeBase();
    }

    public <T> T execute(Command<T> command) {
        StatefulKnowledgeSession ksession = newWorkingMemory();

        FixedKnowledgeCommandContext context = new FixedKnowledgeCommandContext( new ContextImpl( "ksession",
                                                                                                  null ),
                                                                                 null,
                                                                                 null,
                                                                                 ksession,
                                                                                 new ExecutionResultImpl() );

        try {
            ((StatefulKnowledgeSessionImpl) ksession).startBatchExecution( new ExecutionResultImpl() );

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
                ExecutionResults result = ((StatefulKnowledgeSessionImpl) ksession).getExecutionResult();
                return (T) result;
            } else {
                return (T) o;
            }
        } finally {
            ((StatefulKnowledgeSessionImpl) ksession).endBatchExecution();
            dispose(ksession);
        }
    }

    public void execute(Object object) {
        StatefulKnowledgeSession ksession = newWorkingMemory();
        try {
            ksession.insert( object );
            ksession.fireAllRules();
        } finally {
            dispose(ksession);
        }
    }

    public void execute(Iterable objects) {
        StatefulKnowledgeSession ksession = newWorkingMemory();
        try {
            for ( Object object : objects ) {
                ksession.insert( object );
            }
            ksession.fireAllRules();
        } finally {
            dispose(ksession);
        }
    }

    public List executeWithResults(Iterable objects, ObjectFilter filter) {
        List list = new ArrayList();
        StatefulKnowledgeSession ksession = newWorkingMemory();
        try {
            for ( Object object : objects ) {
                ksession.insert( object );
            }
            ksession.fireAllRules();
            for (FactHandle fh : ksession.getFactHandles(filter)) {
                list.add(((InternalFactHandle) fh).getObject());
            }
        } finally {
            dispose(ksession);
        }
        return list;
    }

    public Environment getEnvironment() {
        return environment;
    }

    protected void dispose(StatefulKnowledgeSession ksession) {
        StatefulKnowledgeSessionImpl wm = ((StatefulKnowledgeSessionImpl) ksession);

        for ( AgendaEventListener listener : wm.getAgendaEventSupport().getEventListeners() ) {
            this.agendaEventSupport.removeEventListener( listener );
        }
        for ( RuleRuntimeEventListener listener: wm.getRuleRuntimeEventSupport().getEventListeners() ) {
            this.ruleRuntimeEventSupport.removeEventListener(listener);
        }
        InternalProcessRuntime processRuntime = wm.internalGetProcessRuntime();
        if ( processRuntime != null ) {
            for ( ProcessEventListener listener: processRuntime.getProcessEventListeners() ) {
                this.processEventSupport.removeEventListener( listener );
            }
        }
        ksession.dispose();
    }

    private static class AgendaEventListenerPlaceholder implements AgendaEventListener {

        @Override
        public void matchCreated(MatchCreatedEvent event) { }

        @Override
        public void matchCancelled(MatchCancelledEvent event) { }

        @Override
        public void beforeMatchFired(BeforeMatchFiredEvent event) { }

        @Override
        public void afterMatchFired(AfterMatchFiredEvent event) { }

        @Override
        public void agendaGroupPopped(AgendaGroupPoppedEvent event) { }

        @Override
        public void agendaGroupPushed(AgendaGroupPushedEvent event) { }

        @Override
        public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) { }

        @Override
        public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) { }

        @Override
        public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) { }

        @Override
        public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) { }
    }

    private static class RuleRuntimeEventListenerPlaceholder implements RuleRuntimeEventListener {

        @Override
        public void objectInserted(ObjectInsertedEvent event) { }

        @Override
        public void objectUpdated(ObjectUpdatedEvent event) { }

        @Override
        public void objectDeleted(ObjectDeletedEvent event) { }
    }
}
