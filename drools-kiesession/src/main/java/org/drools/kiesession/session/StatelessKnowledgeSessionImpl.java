/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.kiesession.session;

import org.drools.core.SessionConfiguration;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.AbstractRuntime;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.management.DroolsManagementAgent;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.RegistryContext;
import org.kie.internal.runtime.StatefulKnowledgeSession;

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

public class StatelessKnowledgeSessionImpl extends AbstractRuntime implements StatelessKieSession {

    private InternalKnowledgeBase kBase;
    private MapGlobalResolver    sessionGlobals = new MapGlobalResolver();
    private Map<String, Channel> channels       = new HashMap<>();

    private final List<ListnerHolder> listeners = new CopyOnWriteArrayList<>();

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
        this.kBase = kBase;
        this.conf = conf != null ? conf.as(SessionConfiguration.KEY) : kBase.getSessionConfiguration().as(SessionConfiguration.KEY);
        this.environment = EnvironmentFactory.newEnvironment();
        this.pool = null;
        wmCreated = new AtomicLong(0);
    }

    public StatelessKnowledgeSessionImpl(KieSessionConfiguration conf,
                                         StatefulSessionPool pool) {
        this.kBase = pool.getKieBase();
        this.conf = conf != null ? conf.as(SessionConfiguration.KEY) : kBase.getSessionConfiguration().as(SessionConfiguration.KEY);
        this.environment = null;
        this.pool = pool;
        wmCreated = new AtomicLong(1);
    }

    public InternalKnowledgeBase getKnowledgeBase() {
        return this.kBase;
    }

    private StatefulKnowledgeSession newWorkingMemory() {
        StatefulKnowledgeSessionImpl ksession = (StatefulKnowledgeSessionImpl) ( pool != null ? pool.get() : createWorkingMemory() );

        ((Globals ) ksession.getGlobalResolver()).setDelegate(this.sessionGlobals);

        registerListeners( ksession );

        for( Map.Entry<String, Channel> entry : this.channels.entrySet() ) {
            ksession.registerChannel( entry.getKey(), entry.getValue() );
        }

        return ksession;
    }

    private InternalWorkingMemory createWorkingMemory() {
        this.kBase.readLock();
        try {
            InternalWorkingMemory ksession = ((StatefulKnowledgeSessionImpl) RuntimeComponentFactory.get()
                    .createStatefulSession(kBase, this.environment, this.conf, false ) )
                    .setStateless( true );
            wmCreated.incrementAndGet();
            return ksession;
        } finally {
            this.kBase.readUnlock();
        }
    }

    public void initMBeans(String containerId, String kbaseId, String ksessionName) {
        if (kBase.getConfiguration() != null && kBase.getKieBaseConfiguration().isMBeansEnabled() && mbeanRegistered.compareAndSet(false, true)) {
            this.mbeanRegisteredCBSKey = new DroolsManagementAgent.CBSKey(containerId, kbaseId, ksessionName);
            DroolsManagementAgent.getInstance().registerKnowledgeSessionUnderName(mbeanRegisteredCBSKey, this);
        }
    }
    
    public long getWorkingMemoryCreated() {
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
        Collection<EventListener> l = new ArrayList<>();
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
    public void registerChannel(String name, Channel channel) {
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

        RegistryContext context = RegistryContext.create().register( KieSession.class, ksession );

        try {
            if ( command instanceof BatchExecutionCommand ) {
                context.register( ExecutionResults.class, KieServices.get().getCommands().newExecutionResults() );
            }

            ((StatefulKnowledgeSessionImpl) ksession).startBatchExecution();

            ExecutableCommand executableCommand = (ExecutableCommand) command;
            Object o = executableCommand.execute( context );
            // did the user take control of fireAllRules, if not we will auto execute
            if ( executableCommand.autoFireAllRules() ) {
                ksession.fireAllRules();
            }
            if ( command instanceof BatchExecutionCommand ) {
                return (T) context.lookup( ExecutionResults.class );
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
