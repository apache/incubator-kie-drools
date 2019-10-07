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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.SessionConfiguration;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.command.impl.ContextImpl;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.management.DroolsManagementAgent;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.api.KieBase;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;

public class StatelessKnowledgeSessionImpl extends AbstractRuntime
        implements
        StatelessKnowledgeSession,
        StatelessKieSession {

    private KnowledgeBaseImpl    kBase;
    private MapGlobalResolver    sessionGlobals = new MapGlobalResolver();
    private Map<String, Channel> channels       = new HashMap<String, Channel>();

    private final List<ListnerHolder> listeners = new CopyOnWriteArrayList<ListnerHolder>();

    private SessionConfiguration    conf;
    private Environment             environment;

    private AtomicBoolean mbeanRegistered = new AtomicBoolean(false);
    private DroolsManagementAgent.CBSKey mbeanRegisteredCBSKey;
    private final AtomicLong wmCreated;

    private final StatefulSessionPool pool;

    public StatelessKnowledgeSessionImpl() {
        pool = null;
        wmCreated = new AtomicLong(0);
    }

    public StatelessKnowledgeSessionImpl(InternalKnowledgeBase kBase,
                                         KieSessionConfiguration conf) {
        this.kBase = (KnowledgeBaseImpl)kBase;
        this.conf = conf != null ? (SessionConfiguration) conf : kBase.getSessionConfiguration();
        this.environment = EnvironmentFactory.newEnvironment();
        this.pool = null;
        wmCreated = new AtomicLong(0);
    }

    public StatelessKnowledgeSessionImpl(KieSessionConfiguration conf,
                                         StatefulSessionPool pool) {
        this.kBase = pool.getKieBase();
        this.conf = conf != null ? (SessionConfiguration) conf : kBase.getSessionConfiguration();
        this.environment = null;
        this.pool = pool;
        wmCreated = new AtomicLong(1);
    }

    public InternalKnowledgeBase getKnowledgeBase() {
        return this.kBase;
    }

    private StatefulKnowledgeSession newWorkingMemory() {
        StatefulKnowledgeSessionImpl ksession = pool != null ? pool.get() : createWorkingMemory();

        ((Globals ) ksession.getGlobalResolver()).setDelegate(this.sessionGlobals);

        registerListeners( ksession );

        for( Map.Entry<String, Channel> entry : this.channels.entrySet() ) {
            ksession.registerChannel( entry.getKey(), entry.getValue() );
        }

        return ksession;
    }

    private StatefulKnowledgeSessionImpl createWorkingMemory() {
        this.kBase.readLock();
        try {
            StatefulKnowledgeSessionImpl ksession = kBase
                    .internalCreateStatefulKnowledgeSession( this.environment, this.conf, false )
                    .setStateless( true );
            wmCreated.incrementAndGet();
            return ksession;
        } finally {
            this.kBase.readUnlock();
        }
    }

    public void initMBeans(String containerId, String kbaseId, String ksessionName) {
        if (kBase.getConfiguration() != null && kBase.getConfiguration().isMBeansEnabled() && mbeanRegistered.compareAndSet(false, true)) {
            this.mbeanRegisteredCBSKey = new DroolsManagementAgent.CBSKey(containerId, kbaseId, ksessionName);
            DroolsManagementAgent.getInstance().registerKnowledgeSessionUnderName(mbeanRegisteredCBSKey, this);
        }
    }
    
    public long getWorkingMemoryCreatec() {
        return wmCreated.get();
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

        RegistryContext context = new ContextImpl().register( KieSession.class, ksession );

        try {
            if ( command instanceof BatchExecutionCommand ) {
                ((RegistryContext) context).register( ExecutionResultImpl.class, new ExecutionResultImpl() );
            }

            ((StatefulKnowledgeSessionImpl) ksession).startBatchExecution();

            Object o = ((ExecutableCommand) command).execute( context );
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
            if ( command instanceof BatchExecutionCommand ) {
                return (T) ((RegistryContext) context).lookup( ExecutionResultImpl.class );
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

    private void dispose(StatefulKnowledgeSession ksession) {
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
            if ( !(obj instanceof ListnerHolder) ) {
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
