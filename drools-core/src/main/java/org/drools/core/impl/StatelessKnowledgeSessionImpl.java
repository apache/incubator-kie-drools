/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
import org.drools.core.SessionConfigurationImpl;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.command.impl.ContextImpl;
import org.drools.core.command.impl.FixedKnowledgeCommandContext;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.WorkingMemoryFactory;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class StatelessKnowledgeSessionImpl extends AbstractRuntime
        implements
        StatelessKnowledgeSession,
        StatelessKieSession {

    private InternalKnowledgeBase kBase;
    private MapGlobalResolver    sessionGlobals = new MapGlobalResolver();
    private Map<String, Channel> channels       = new HashMap<String, Channel>();

    private final List<ListnerHolder> listeners = new CopyOnWriteArrayList<ListnerHolder>();

    private KieSessionConfiguration conf;
    private Environment             environment;

    private WorkingMemoryFactory wmFactory;

    public StatelessKnowledgeSessionImpl() {
    }

    public StatelessKnowledgeSessionImpl(final InternalKnowledgeBase kBase,
                                         final KieSessionConfiguration conf) {
        this.kBase = kBase;
        this.conf = (conf != null) ? conf : SessionConfigurationImpl.getDefaultInstance();
        this.environment = EnvironmentFactory.newEnvironment();
        this.wmFactory = kBase.getConfiguration().getComponentFactory().getWorkingMemoryFactory();
    }

    public InternalKnowledgeBase getKnowledgeBase() {
        return this.kBase;
    }


    public StatefulKnowledgeSession newWorkingMemory() {
        this.kBase.readLock();
        try {
            StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) wmFactory.createWorkingMemory(this.kBase.nextWorkingMemoryCounter(),
                                                                                                         this.kBase,
                                                                                                         (SessionConfiguration) this.conf,
                                                                                                         this.environment);
            StatefulKnowledgeSessionImpl ksessionImpl = (StatefulKnowledgeSessionImpl) ksession;

            ((Globals) ksessionImpl.getGlobalResolver()).setDelegate(this.sessionGlobals);

            registerListeners( ksessionImpl );

            for( Map.Entry<String, Channel> entry : this.channels.entrySet() ) {
                ksession.registerChannel( entry.getKey(), entry.getValue() );
            }

            return ksession;
        } finally {
            this.kBase.readUnlock();
        }
    }

    private void registerListeners( StatefulKnowledgeSessionImpl wm ) {
        if ( listeners.isEmpty()) {
            return;
        }
        for (ListnerHolder listnerHolder : listeners ) {
            switch (listnerHolder.type) {
                case AGENDA:
                    wm.addEventListener( (AgendaEventListener)listnerHolder.listener );
                    break;
                case RUNTIME:
                    wm.addEventListener( (RuleRuntimeEventListener)listnerHolder.listener );
                    break;
                case PROCESS:
                    wm.addEventListener( (ProcessEventListener)listnerHolder.listener );
                    break;
            }
        }
    }

    public void addEventListener(AgendaEventListener listener) {
        listeners.add( new ListnerHolder( ListnerHolder.Type.AGENDA, listener ) );
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return (Collection<AgendaEventListener>) getListeners(ListnerHolder.Type.AGENDA);
    }

    public void removeEventListener(AgendaEventListener listener) {
        listeners.remove( new ListnerHolder( ListnerHolder.Type.AGENDA, listener ) );
    }

    public void addEventListener(RuleRuntimeEventListener listener) {
        listeners.add( new ListnerHolder( ListnerHolder.Type.RUNTIME, listener ) );
    }

    public void removeEventListener(RuleRuntimeEventListener listener) {
        listeners.remove( new ListnerHolder( ListnerHolder.Type.RUNTIME, listener ) );
    }

    public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
        return (Collection<RuleRuntimeEventListener>) getListeners(ListnerHolder.Type.RUNTIME);
    }

    public void addEventListener(ProcessEventListener listener) {
        listeners.add( new ListnerHolder( ListnerHolder.Type.PROCESS, listener ) );
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        return (Collection<ProcessEventListener>) getListeners(ListnerHolder.Type.PROCESS);
    }

    private Collection<? extends EventListener> getListeners(ListnerHolder.Type type) {
        if ( listeners.isEmpty()) {
            return Collections.emptySet();
        }
        Collection<EventListener> l = new ArrayList<EventListener>();
        for (ListnerHolder listnerHolder : listeners ) {
            if (listnerHolder.type == type) {
                l.add( listnerHolder.listener );
            }
        }
        return l;
    }

    public void removeEventListener(ProcessEventListener listener) {
        listeners.remove( new ListnerHolder( ListnerHolder.Type.RUNTIME, listener ) );
    }

    public void setGlobal(String identifier, Object value) {
        this.sessionGlobals.setGlobal(identifier, value);
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
        ksession.dispose();
    }

    private static class ListnerHolder {
        enum Type { AGENDA, RUNTIME, PROCESS }

        final Type type;
        final EventListener listener;

        private ListnerHolder( Type type, EventListener listener ) {
            this.type = type;
            this.listener = listener;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( this == obj ) {
                return true;
            }
            if ( obj == null || !(obj instanceof ListnerHolder) ) {
                return false;
            }

            ListnerHolder that = (ListnerHolder) obj;
            return type == that.type && listener == that.listener;
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + listener.hashCode();
            return result;
        }
    }
}
